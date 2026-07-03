package com.benbenlaw.shops.item;

import com.benbenlaw.shops.attachments.PlayerBalanceData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.config.StartUpConfig;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.sound.ShopsSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            player.getItemInHand(hand).shrink(1);

            PlayerBalanceData current = player.getData(ShopsAttachments.PLAYER_BALANCE.get());
            PlayerBalanceData updated = current.addBalance(value);
            player.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);

            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(updated.getBalance()));
            level.playSound(null, player.blockPosition(), ShopsSounds.COIN_COLLECTED.get(), player.getSoundSource(), 0.5f, 1.0f);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (!level.isClientSide()) {

            if (StartUpConfig.autoConsumeCoins.get()) {
                if (owner instanceof Player player && level.getGameTime() % 20 == 0) {

                    itemStack.shrink(1);

                    PlayerBalanceData current = player.getData(ShopsAttachments.PLAYER_BALANCE.get());
                    PlayerBalanceData updated = current.addBalance(value);
                    player.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);

                    PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(updated.getBalance()));
                    level.playSound(null, player.blockPosition(), ShopsSounds.COIN_COLLECTED.get(), player.getSoundSource(), 0.5f, 1.0f);
                }
            }
        }
    }
}
