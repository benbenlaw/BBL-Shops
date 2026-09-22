package com.benbenlaw.shops.events.client;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.ShopsBlocks;
import com.benbenlaw.shops.component.ShopsDataComponents;
import com.benbenlaw.shops.entity.ShopsEntityTypes;
import com.benbenlaw.shops.entity.renderer.ShopTraderVillagerRenderer;
import com.benbenlaw.shops.item.CoinItem;
import com.benbenlaw.shops.item.PlayerBalanceCardItem;
import com.benbenlaw.shops.screen.ShopsBlockScreen;
import com.benbenlaw.shops.screen.ShopsMenuTypes;
import com.benbenlaw.shops.screen.ShopScreen;
import com.benbenlaw.shops.util.KeyBinds;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Shops.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {


    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof CoinItem coinItem) {
            addShiftTooltip(stack, event, coinItem, "tooltip.shops.value", String.valueOf(coinItem.getValue()));
        }

        if (stack.getItem() instanceof PlayerBalanceCardItem card) {
            ShopsDataComponents.BoundPlayer bound = stack.get(ShopsDataComponents.BOUND_PLAYER.get());

            if (bound != null) {
                addShiftTooltip(stack, event, card, "tooltip.shops.bound_to", String.valueOf(bound.name()));
            }
            else {
                addShiftTooltip(stack, event, card, "tooltip.shops.how_to");
            }
        }

        if (stack.is(ShopsBlocks.SHOP_BLOCK.get().asItem())) {
            addShiftTooltip(stack, event, stack.getItem(), "tooltip.shops.shop_block_info");
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
        event.registerEntityRenderer(ShopsEntityTypes.SHOP_TRADER.get(), ShopTraderVillagerRenderer::new);
    }

    @SubscribeEvent
    public static void onClientPress(InputEvent.Key event) {
        if (event.getAction() != InputConstants.PRESS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Screen currentScreen = mc.screen;

        boolean isUnbound = KeyBinds.SHOP_OPEN_HOTKEY.isUnbound();
        boolean shopKey = event.getKey() == KeyBinds.SHOP_OPEN_HOTKEY.getKey().getValue();

        if (shopKey && !isUnbound) {
            if (currentScreen instanceof ShopScreen) {
                currentScreen.onClose();
            } else {
                mc.setScreen(new ShopScreen(Component.translatable("menu.shops.shop"), ClientRecipeCache.cachedShopRecipes));
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(RegisterKeyMappingsEvent event) {
        event.register(KeyBinds.SHOP_OPEN_HOTKEY);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ShopsMenuTypes.SHOP_MENU.get(), ShopsBlockScreen::new);
    }

}
