package com.benbenlaw.shops.events.client;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.item.CoinItem;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.recipe.ShopsRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Shops.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {


    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof CoinItem coinItem) {
            addShiftTooltip(stack, event, ShopsItems.COPPER_COIN.get(), "tooltip.utility.crook", String.valueOf(coinItem.getValue()));
        }
    }

    public static void addShiftTooltip(ItemStack stack, ItemTooltipEvent event, Item item, String tooltipText, String... additionalInfo) {
        if (stack.is(item)) {
            boolean alreadyAdded = event.getToolTip().stream().anyMatch((c) -> c.getString().equals(Component.translatable("tooltip.shops.shift").getString()));
            if (Minecraft.getInstance().hasShiftDown()) {
                event.getToolTip().add(Component.translatable(tooltipText, (Object[])additionalInfo).withStyle(ChatFormatting.BLUE));
            } else if (!alreadyAdded) {
                event.getToolTip().add(Component.translatable("tooltip.shops.shift").withStyle(ChatFormatting.YELLOW));
            }

        }
    }
}
