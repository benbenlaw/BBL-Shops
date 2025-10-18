package com.benbenlaw.shops.item;

import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.sound.ShopsSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class CoinItem extends Item {

    int value;

    public CoinItem(Properties properties, int value) {
        super(properties);
        this.value = value;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (!level.isClientSide()) {

            if (entity instanceof Player player && level.getGameTime() % 20 == 0) {

                stack.shrink(1);
                int currentBalance = player.getData(ShopsAttachments.PLAYER_BALANCE).getBalance();
                int newBalance = currentBalance + value;

                player.getData(ShopsAttachments.PLAYER_BALANCE).setBalance(newBalance);
                PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(newBalance));
                level.playSound(null, player.blockPosition(), ShopsSounds.COIN_COLLECTED.get(), player.getSoundSource(), 0.5f, 1.0f);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.shops.value", value).withStyle(ChatFormatting.BLUE));

        } else {
            list.add(Component.translatable("tooltip.shops.shift").withStyle(ChatFormatting.YELLOW));
        }
    }
}
