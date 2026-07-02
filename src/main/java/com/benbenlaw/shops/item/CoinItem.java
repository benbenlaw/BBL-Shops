package com.benbenlaw.shops.item;

import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.sound.ShopsSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.Nullable;

public class CoinItem extends Item {

    int value;

    public CoinItem(Properties properties, int value) {
        super(properties);
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (!level.isClientSide()) {

            if (owner instanceof Player player && level.getGameTime() % 20 == 0) {

                itemStack.shrink(1);
                int currentBalance = player.getData(ShopsAttachments.PLAYER_BALANCE).getBalance();
                int newBalance = currentBalance + value;

                player.getData(ShopsAttachments.PLAYER_BALANCE).setBalance(newBalance);
                PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(newBalance));
                level.playSound(null, player.blockPosition(), ShopsSounds.COIN_COLLECTED.get(), player.getSoundSource(), 0.5f, 1.0f);
            }
        }
    }
}
