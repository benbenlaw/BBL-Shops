package com.benbenlaw.shops;

import com.benbenlaw.shops.block.ShopsBlocks;
import com.benbenlaw.shops.block.entity.ShopsBlockEntities;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.config.StartUpConfig;
import com.benbenlaw.shops.item.ShopsCreativeTab;
import com.benbenlaw.shops.item.ShopsDataComponents;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.network.ShopsNetworking;
import com.benbenlaw.shops.screen.ShopScreen;
import com.benbenlaw.shops.screen.ShopsMenuTypes;
import com.benbenlaw.shops.shop.CombinedShopLoader;
import com.google.gson.Gson;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Shops.MOD_ID)
public class Shops {
    public static final String MOD_ID = "shops";
    public static final Logger LOGGER = LogManager.getLogger();

    public Shops(final IEventBus eventBus, final ModContainer modContainer) {

        modContainer.registerConfig(ModConfig.Type.STARTUP, StartUpConfig.SPEC, "bbl/shops/coin_values.toml");

        ShopsAttachments.ATTACHMENTS.register(eventBus);
        ShopsMenuTypes.MENUS.register(eventBus);
        ShopsItems.ITEMS.register(eventBus);
        ShopsCreativeTab.CREATIVE_MODE_TABS.register(eventBus);
        ShopsBlocks.BLOCKS.register(eventBus);
        ShopsBlockEntities.BLOCK_ENTITIES.register(eventBus);
        ShopsDataComponents.COMPONENTS.register(eventBus);

        eventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(Shops::onAddReloadListener);
        eventBus.addListener(Shops::registerCapabilities);
    }

    @SubscribeEvent
    private static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new CombinedShopLoader(new Gson(), "catalogs"));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        ShopsBlockEntities.registerCapabilities(event);
    }

    public void commonSetup(RegisterPayloadHandlersEvent event) {
        ShopsNetworking.registerNetworking(event);
    }

    @EventBusSubscriber(modid = Shops.MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ShopsMenuTypes.SHOP_MENU.get(), ShopScreen::new);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        }
    }
}
