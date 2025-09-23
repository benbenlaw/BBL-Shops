package com.benbenlaw.shops.block.entity;

import com.benbenlaw.shops.capability.PlayerBalanceData;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.item.ShopsDataComponents;
import com.benbenlaw.shops.item.util.ShopEntry;
import com.benbenlaw.shops.item.util.ShopRegistry;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.screen.ShopMenu;
import com.benbenlaw.shops.util.ExchangeRegistry;
import com.benbenlaw.shops.util.InputOutputItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ShopBlockEntity extends BlockEntity implements MenuProvider {

    public final ContainerData data;
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public static final int PLAYER_BALANCE_CARD = 0;
    public static final int INPUT_SLOT = 1;
    public static final int CATALOG = 2;
    public static final int OUTPUT_SLOT = 3;

    private ItemStack autoProduced = ItemStack.EMPTY;

    public ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    private final IItemHandler shopItemHandler = new InputOutputItemHandler(itemHandler,
            (slot, stack) -> {
                if (slot == INPUT_SLOT) return true;
                return false;
            },
            (slot) -> {
                if (slot == OUTPUT_SLOT) return true;
                return false;
            }
    );

    public IItemHandler getShopItemHandler(Direction side) {
        return shopItemHandler;
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable("block.shops.shop");
    }

    @Override
    public AbstractContainerMenu createMenu(int container, @NotNull Inventory inventory, @NotNull Player player) {
        return new ShopMenu(container, inventory, this.getBlockPos(), data);
    }

    public ShopBlockEntity(BlockPos pos, BlockState state) {
        super(ShopsBlockEntities.SHOP_BLOCK_ENTITY.get(), pos, state);

        this.data = new ContainerData() {;
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {

            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    public void tick() {

        assert level != null;
        if (level.isClientSide()) return;

        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);

        if (!input.isEmpty() && itemHandler.getStackInSlot(PLAYER_BALANCE_CARD).has(ShopsDataComponents.PLAYER_UUID)) {
            int value = ExchangeRegistry.getValue(input);

            if (value > 0) {
                int requiredAmount = ExchangeRegistry.getRequiredAmount(input);

                if (input.getCount() < requiredAmount) return;

                itemHandler.extractItem(INPUT_SLOT, requiredAmount, false);

                Player cardPlayer = level.getPlayerByUUID(UUID.fromString(
                        Objects.requireNonNull(itemHandler.getStackInSlot(PLAYER_BALANCE_CARD)
                                .get(ShopsDataComponents.PLAYER_UUID))
                ));

                if (cardPlayer != null) {
                    PlayerBalanceData cardData = cardPlayer.getData(ShopsAttachments.PLAYER_BALANCE);
                    cardData.addBalance(value);
                    PacketDistributor.sendToPlayer((ServerPlayer) cardPlayer, new SyncPlayerBalanceToClient(cardData.getBalance()));

                }

            }
        }

        if (!autoProduced.isEmpty()) {
            ItemStack balanceCard = itemHandler.getStackInSlot(PLAYER_BALANCE_CARD);

            if (balanceCard.has(ShopsDataComponents.PLAYER_UUID)) {
                assert level != null;
                Player cardPlayer = level.getPlayerByUUID(
                        UUID.fromString(Objects.requireNonNull(balanceCard.get(ShopsDataComponents.PLAYER_UUID)))
                );

                if (cardPlayer != null) {
                    PlayerBalanceData cardData = cardPlayer.getData(ShopsAttachments.PLAYER_BALANCE);
                    ShopEntry shopEntry = ShopRegistry.getShopItem(autoProduced.getItem());

                    if (shopEntry != null && cardData.getBalance() >= shopEntry.getPrice()) {

                        ItemStack requiredCatalogueItem = shopEntry.getRequiredCatalogueItem();

                        if (playerHasCatalogueItem(requiredCatalogueItem)) {
                            ItemStack stackToAdd = autoProduced.copy();

                            if (canOutput(stackToAdd)) {
                                int newBalance = cardData.getBalance() - shopEntry.getPrice();
                                cardData.setBalance(newBalance);

                                addToOutputSlot(stackToAdd);
                                PacketDistributor.sendToPlayer((ServerPlayer) cardPlayer, new SyncPlayerBalanceToClient(cardData.getBalance()));

                                setChanged();
                            }
                        }
                    }
                }
            }
        }

    }

    private boolean playerHasCatalogueItem(ItemStack catalogueItem) {
        return ItemStack.isSameItemSameComponents(itemHandler.getStackInSlot(CATALOG), catalogueItem);

    }

    private boolean canOutput(ItemStack stackToAdd) {
        ItemStack currentStack = itemHandler.getStackInSlot(OUTPUT_SLOT);

        if (currentStack.isEmpty()) return true;

        if (ItemStack.isSameItem(currentStack, stackToAdd)) {
            int combined = currentStack.getCount() + stackToAdd.getCount();
            return combined <= currentStack.getMaxStackSize();
        }

        return false;
    }

    private void addToOutputSlot(ItemStack stackToAdd) {
        ItemStack currentStack = itemHandler.getStackInSlot(OUTPUT_SLOT);

        if (currentStack.isEmpty()) {
            int count = Math.min(stackToAdd.getCount(), stackToAdd.getMaxStackSize());
            ItemStack newStack = stackToAdd.copy();
            newStack.setCount(count);
            itemHandler.setStackInSlot(OUTPUT_SLOT, newStack);
        } else if (ItemStack.isSameItemSameComponents(currentStack, stackToAdd)) {
            int combined = currentStack.getCount() + stackToAdd.getCount();
            int max = currentStack.getMaxStackSize();
            currentStack.setCount(Math.min(combined, max));
            itemHandler.setStackInSlot(OUTPUT_SLOT, currentStack);
        }
    }

    public void setAutoProduced(ItemStack stack) {
        autoProduced = stack.copy();
        setChanged();
    }

    public ItemStack getAutoProduced() {
        return autoProduced;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(compoundTag, provider);

        compoundTag.put("inventory", this.itemHandler.serializeNBT(provider));

        if (!autoProduced.isEmpty()) {
            compoundTag.put("autoProduced", autoProduced.save(provider));
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(compoundTag, provider);

        this.itemHandler.deserializeNBT(provider, compoundTag.getCompound("inventory"));

        if (compoundTag.contains("autoProduced")) {
            this.autoProduced = ItemStack.parse(provider, compoundTag.getCompound("autoProduced")).orElse(ItemStack.EMPTY);
        } else {
            this.autoProduced = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_323910_) {
        return saveWithoutMetadata(p_323910_);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadAdditional(tag, lookupProvider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
