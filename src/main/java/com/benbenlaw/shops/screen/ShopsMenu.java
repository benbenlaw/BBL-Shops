package com.benbenlaw.shops.screen;

import com.benbenlaw.core.block.entity.handler.item.SyncableItemHandler;
import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.core.screen.util.slot.InputSlot;
import com.benbenlaw.core.screen.util.slot.ResultSlot;
import com.benbenlaw.shops.block.entity.ShopBlockEntity;
import com.benbenlaw.shops.item.ShopsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class ShopsMenu extends SimpleAbstractContainerMenu {

    public static final int OPEN_SELECTOR_BUTTON = 0;
    private static final int PLAYER_INV_START = 36;
    private final ShopBlockEntity blockEntity;

    public ShopsMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, extraData.readBlockPos());
    }

    public ShopsMenu(int containerId, Inventory inventory, BlockPos pos) {
        super(ShopsMenuTypes.SHOP_MENU.get(), containerId, inventory, pos, 7);

        Level level = inventory.player.level();
        this.blockEntity = (ShopBlockEntity) level.getBlockEntity(pos);
        assert blockEntity != null;

        SyncableItemHandler handler = blockEntity.getItemHandler();

        this.addSlot(new WhitelistSlot(handler, handler::set, ShopBlockEntity.SLOT_BALANCE_CARD, 80, 23,
                new ItemStack(ShopsItems.PLAYER_BALANCE_CARD.get())));

        this.addSlot(new InputSlot(handler, handler::set, ShopBlockEntity.SLOT_SELL_INPUT_1, 8, 41));
        this.addSlot(new InputSlot(handler, handler::set, ShopBlockEntity.SLOT_SELL_INPUT_2, 26, 41));
        this.addSlot(new InputSlot(handler, handler::set, ShopBlockEntity.SLOT_SELL_INPUT_3, 44, 41));

        this.addSlot(new ResultSlot(handler, handler::set, ShopBlockEntity.SLOT_BUY_OUTPUT_1, 116, 41));
        this.addSlot(new ResultSlot(handler, handler::set, ShopBlockEntity.SLOT_BUY_OUTPUT_2, 134, 41));
        this.addSlot(new ResultSlot(handler, handler::set, ShopBlockEntity.SLOT_BUY_OUTPUT_3, 152, 41));
    }

    public ShopBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == OPEN_SELECTOR_BUTTON && player instanceof ServerPlayer serverPlayer) {
            blockEntity.openSelectorFor(serverPlayer);
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public @NonNull ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (index < PLAYER_INV_START) {
            int sellInputStart = PLAYER_INV_START + ShopBlockEntity.SLOT_SELL_INPUT_1;
            int sellInputEnd = PLAYER_INV_START + ShopBlockEntity.SLOT_SELL_INPUT_3 + 1;
            if (!moveItemStackTo(original, sellInputStart, sellInputEnd, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(original, 0, PLAYER_INV_START, true)) {
                return ItemStack.EMPTY;
            }
        }

        if (original.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }
}
