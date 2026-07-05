package com.benbenlaw.shops.events.client;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.entity.ShopsEntityTypes;
import com.benbenlaw.shops.entity.renderer.ShopTraderVillagerRenderer;
import com.benbenlaw.shops.item.CoinItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Shops.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {


    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof CoinItem coinItem) {
            addShiftTooltip(stack, event, coinItem, "tooltip.shops.value", String.valueOf(coinItem.getValue()));
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

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ShopsEntityTypes.SHOP_TRADER_VILLAGER.get(), ShopTraderVillagerRenderer::new);
    }
}
