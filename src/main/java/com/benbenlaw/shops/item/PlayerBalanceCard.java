package com.benbenlaw.shops.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class PlayerBalanceCard extends Item {

    public PlayerBalanceCard(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof PlayerBalanceCard) {
            stack.set(ShopsDataComponents.PLAYER_UUID.get(), player.getStringUUID());
            stack.set(ShopsDataComponents.PLAYER_USERNAME.get(), String.valueOf(player.getGameProfile().getName()));
        }
        return InteractionResultHolder.success(stack);
    }

    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            if (stack.has(ShopsDataComponents.PLAYER_UUID.get())) {
                String playerName = stack.has(ShopsDataComponents.PLAYER_USERNAME.get()) ? Objects.requireNonNull(stack.get(ShopsDataComponents.PLAYER_USERNAME.get())) : "Error";
                list.add(Component.translatable("tooltip.shops.bound_to", playerName).withStyle(ChatFormatting.BLUE));
            } else {
                list.add(Component.translatable("tooltip.shops.how_to").withStyle(ChatFormatting.BLUE));

            }
        } else {
            list.add(Component.translatable("tooltip.shops.shift").withStyle(ChatFormatting.YELLOW));
        }


    }
}
