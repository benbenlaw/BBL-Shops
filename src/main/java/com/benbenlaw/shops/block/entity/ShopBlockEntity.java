package com.benbenlaw.shops.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.shops.attachments.PlayerBalanceData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.block.ShopsBlockEntities;
import com.benbenlaw.shops.component.ShopsDataComponents;
import com.benbenlaw.shops.entity.ShopTraderMob;
import com.benbenlaw.shops.item.PlayerBalanceCardItem;
import com.benbenlaw.shops.network.packets.OpenShopBlockSelectorScreen;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.recipe.ShopEntryRecipe;
import com.benbenlaw.shops.recipe.ShopsRecipeTypes;
import com.benbenlaw.shops.screen.ShopsMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ShopBlockEntity extends SyncableBlockEntity implements MenuProvider {

    public static final int SLOT_BALANCE_CARD = 0;
    public static final int SLOT_SELL_INPUT_1 = 1;
    public static final int SLOT_SELL_INPUT_2 = 2;
    public static final int SLOT_SELL_INPUT_3 = 3;
    public static final int SLOT_BUY_OUTPUT_1 = 4;
    public static final int SLOT_BUY_OUTPUT_2 = 5;
    public static final int SLOT_BUY_OUTPUT_3 = 6;

    private static final int[] SELL_INPUT_SLOTS = {SLOT_SELL_INPUT_1, SLOT_SELL_INPUT_2, SLOT_SELL_INPUT_3};
    private static final int[] BUY_OUTPUT_SLOTS = {SLOT_BUY_OUTPUT_1, SLOT_BUY_OUTPUT_2, SLOT_BUY_OUTPUT_3};
    private static final int MAX_AUTO_BUY_TARGETS = BUY_OUTPUT_SLOTS.length;

    private static final double TRADER_SEARCH_RADIUS = 5.0;
    private static final int PROCESS_INTERVAL_TICKS = 10;
    private static final int TRADER_REFRESH_INTERVAL_TICKS = 40;

    private final SyncableItemHandler inventory = new SyncableItemHandler(this, 7,
            (slot, stack) -> switch (slot) {
                case SLOT_BALANCE_CARD -> stack.getItem() instanceof PlayerBalanceCardItem;
                case SLOT_SELL_INPUT_1, SLOT_SELL_INPUT_2, SLOT_SELL_INPUT_3 -> true;
                default -> false;
            },
            slot -> slot == SLOT_BUY_OUTPUT_1 || slot == SLOT_BUY_OUTPUT_2 || slot == SLOT_BUY_OUTPUT_3
    );

    private List<Identifier> autoBuyTargets = new ArrayList<>();
    private Set<Identifier> sellWhitelist = new LinkedHashSet<>();
    private Set<Identifier> cachedNearbyTraders = Set.of();
    private int tickCounter = 0;

    public ShopBlockEntity(BlockPos pos, BlockState state) {
        super(ShopsBlockEntities.SHOP_BLOCK_ENTITY.get(), pos, state);
    }

    public SyncableItemHandler getItemHandler() {
        return inventory;
    }

    public Set<Identifier> nearbyTraders() {
        return cachedNearbyTraders;
    }

    public void refreshNearbyTraders(Level level, BlockPos pos) {
        AABB area = new AABB(pos).inflate(TRADER_SEARCH_RADIUS);
        Set<Identifier> traders = new LinkedHashSet<>();
        for (ShopTraderMob trader : level.getEntitiesOfClass(ShopTraderMob.class, area)) {
            trader.getTraderId().ifPresent(traders::add);
        }
        this.cachedNearbyTraders = traders;
    }

    private boolean isTraderAllowed(Optional<Identifier> trader) {
        return trader.isEmpty() || cachedNearbyTraders.contains(trader.get());
    }

    public void openSelectorFor(ServerPlayer player) {
        if (level == null) return;
        refreshNearbyTraders(level, worldPosition);
        PacketDistributor.sendToPlayer(player, new OpenShopBlockSelectorScreen(
                worldPosition, List.copyOf(autoBuyTargets), List.copyOf(sellWhitelist), List.copyOf(cachedNearbyTraders)));
    }

    public void toggleAutoBuyTarget(Identifier recipeId, Player player) {
        if (autoBuyTargets.contains(recipeId)) {
            autoBuyTargets.remove(recipeId);
            player.sendSystemMessage(Component.translatable("message.shops.auto_buy_removed"));
        } else {
            if (autoBuyTargets.size() >= MAX_AUTO_BUY_TARGETS) {
                player.sendSystemMessage(Component.translatable("message.shops.auto_buy_full"));
                return;
            }
            autoBuyTargets.add(recipeId);
            player.sendSystemMessage(Component.translatable("message.shops.auto_buy_added"));
        }
        setChanged();
        sync();
    }

    public void toggleSellWhitelist(Identifier recipeId, Player player) {
        if (sellWhitelist.contains(recipeId)) {
            sellWhitelist.remove(recipeId);
            player.sendSystemMessage(Component.translatable("message.shops.sell_whitelist_removed"));
        } else {
            sellWhitelist.add(recipeId);
            player.sendSystemMessage(Component.translatable("message.shops.sell_whitelist_added"));
        }
        setChanged();
        sync();
    }

    @Nullable
    private ServerPlayer resolveBoundPlayer(ServerLevel level) {
        ItemStack card = ItemUtil.getStack(inventory, SLOT_BALANCE_CARD);
        if (card.isEmpty()) return null;

        ShopsDataComponents.BoundPlayer bound = card.get(ShopsDataComponents.BOUND_PLAYER.get());
        if (bound == null) return null;

        return level.getServer().getPlayerList().getPlayer(bound.uuid());
    }

    public void tick() {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
        tickCounter++;

        if (tickCounter % TRADER_REFRESH_INTERVAL_TICKS == 0) {
            refreshNearbyTraders(serverLevel, worldPosition);
        }

        if (tickCounter % PROCESS_INTERVAL_TICKS != 0) return;

        ServerPlayer player = resolveBoundPlayer(serverLevel);
        if (player == null) return;

        processSell(serverLevel, player);
        processBuy(serverLevel, player);
    }

    private void processSell(ServerLevel level, ServerPlayer player) {
        if (sellWhitelist.isEmpty()) return;

        for (int slot : SELL_INPUT_SLOTS) {
            ItemStack input = ItemUtil.getStack(inventory, slot);
            if (input.isEmpty()) continue;

            for (RecipeHolder<ShopEntryRecipe> holder : level.recipeAccess().recipeMap().byType(ShopsRecipeTypes.SHOP_TYPE.get())) {
                ShopEntryRecipe recipe = holder.value();
                if (recipe.sellPrice() <= 0) continue;
                if (!sellWhitelist.contains(holder.id().identifier())) continue;
                if (!isTraderAllowed(recipe.trader())) continue;

                ItemStack wanted = recipe.stack().create();
                if (!ItemStack.isSameItemSameComponents(wanted, input)) continue;

                int perTrade = wanted.getCount();
                if (perTrade <= 0) continue;

                int trades = input.getCount() / perTrade;
                if (trades <= 0) continue;

                PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE.get());
                if (!recipe.tier().isEmpty() && !data.hasStage(recipe.tier())) continue;

                int sold = perTrade * trades;
                int remaining = input.getCount() - sold;
                inventory.set(slot, remaining > 0 ? ItemResource.of(input) : ItemResource.EMPTY, remaining);

                PlayerBalanceData updated = data.addBalance(recipe.sellPrice() * trades);
                String unlock = recipe.unlocksTierWhenSold();
                if (!unlock.isEmpty() && !updated.hasStage(unlock)) updated = updated.addStage(unlock);

                player.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);
                PacketDistributor.sendToPlayer(player, new SyncPlayerBalanceToClient(updated.getBalance()));
                break;
            }
        }
    }

    private void processBuy(ServerLevel level, ServerPlayer player) {
        if (autoBuyTargets.isEmpty()) return;

        PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE.get());

        for (Identifier targetId : autoBuyTargets) {
            ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, targetId);
            RecipeHolder<?> holder = level.recipeAccess().recipeMap().byKey(key);
            if (holder == null || !(holder.value() instanceof ShopEntryRecipe recipe)) continue;
            if (recipe.buyPrice() <= 0) continue;
            if (!isTraderAllowed(recipe.trader())) continue;
            if (!recipe.tier().isEmpty() && !data.hasStage(recipe.tier())) continue;

            ItemStack template = recipe.stack().create();
            int perTrade = template.getCount();
            if (perTrade <= 0) continue;

            int targetSlot = -1;
            for (int slot : BUY_OUTPUT_SLOTS) {
                ItemStack existing = ItemUtil.getStack(inventory, slot);
                if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, template)) {
                    targetSlot = slot;
                    break;
                }
            }
            if (targetSlot == -1) {
                for (int slot : BUY_OUTPUT_SLOTS) {
                    if (ItemUtil.getStack(inventory, slot).isEmpty()) {
                        targetSlot = slot;
                        break;
                    }
                }
            }
            if (targetSlot == -1) continue;

            ItemStack output = ItemUtil.getStack(inventory, targetSlot);
            int space = output.isEmpty() ? template.getMaxStackSize() : output.getMaxStackSize() - output.getCount();

            int affordableTrades = data.getBalance() / recipe.buyPrice();
            int spaceTrades = space / perTrade;
            int trades = Math.min(affordableTrades, spaceTrades);
            if (trades <= 0) continue;

            int totalItems = perTrade * trades;
            ItemStack newOutput = output.isEmpty() ? template.copy() : output.copy();
            newOutput.setCount(output.getCount() + totalItems);
            inventory.set(targetSlot, ItemResource.of(newOutput), newOutput.getCount());

            PlayerBalanceData updated = data.subtractBalance(recipe.buyPrice() * trades);
            String unlock = recipe.unlocksTierWhenBought();
            if (!unlock.isEmpty() && !updated.hasStage(unlock)) updated = updated.addStage(unlock);
            data = updated;

            player.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);
            PacketDistributor.sendToPlayer(player, new SyncPlayerBalanceToClient(updated.getBalance()));
        }
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        dropInventoryContents(inventory);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.shops.shop_block");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ShopsMenu(containerId, playerInventory, this.worldPosition);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output.child("inventory"));
        output.store("auto_buy_targets", Identifier.CODEC.listOf(), autoBuyTargets);
        output.store("sell_whitelist", Identifier.CODEC.listOf(), List.copyOf(sellWhitelist));
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input.childOrEmpty("inventory"));
        autoBuyTargets = new ArrayList<>(input.read("auto_buy_targets", Identifier.CODEC.listOf()).orElse(List.of()));
        sellWhitelist = new LinkedHashSet<>(input.read("sell_whitelist", Identifier.CODEC.listOf()).orElse(List.of()));
    }
}
