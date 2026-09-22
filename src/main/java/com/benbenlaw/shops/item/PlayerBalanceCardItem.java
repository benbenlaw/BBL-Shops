package com.benbenlaw.shops.item;

import com.benbenlaw.shops.component.ShopsDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class PlayerBalanceCardItem extends Item {

    public PlayerBalanceCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = player.getItemInHand(hand);
        ShopsDataComponents.BoundPlayer existing = stack.get(ShopsDataComponents.BOUND_PLAYER.get());

        if (existing != null) {
            player.sendSystemMessage(Component.translatable("tooltip.shops.bound_to", existing.name()));
            return InteractionResult.SUCCESS;
        }

        stack.set(ShopsDataComponents.BOUND_PLAYER.get(),
                new ShopsDataComponents.BoundPlayer(player.getUUID(), player.getGameProfile().name()));
        player.sendSystemMessage(Component.translatable("message.shops.card_bound_self"));
        return InteractionResult.SUCCESS;
    }
}
