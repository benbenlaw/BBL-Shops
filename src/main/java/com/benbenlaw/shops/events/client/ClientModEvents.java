package com.benbenlaw.shops.events.client;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.entity.ShopsEntities;
import com.benbenlaw.shops.item.ShopsDataComponents;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.loaders.PinataData;
import com.benbenlaw.shops.loaders.PinataLoader;
import com.benbenlaw.shops.renderer.CrateModel;
import com.benbenlaw.shops.renderer.CrateModelLayers;
import com.benbenlaw.shops.renderer.CrateRenderer;
import com.benbenlaw.shops.renderer.PinataRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = Shops.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ShopsEntities.PINATA.get(), PinataRenderer::new);
        event.registerEntityRenderer(ShopsEntities.CRATE.get(), CrateRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CrateModelLayers.CRATE, CrateModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {

        event.register(
                (stack, tintIndex) -> {
                    int fallback = 0xAAAAAA;

                    ResourceLocation typeId = stack.get(ShopsDataComponents.PINATA_ID);
                    int overlayColor = fallback;

                    if (typeId != null) {
                        PinataData data = PinataLoader.getPinata(typeId);
                        if (data != null) {
                            overlayColor = data.mainColor();
                        }
                    }

                    // Base
                    if (tintIndex == 0) {
                        return 0xFFFFFFFF;
                    }

                    // Pattern
                    if (tintIndex == 1) {
                        return (0xFF << 24) | (overlayColor & 0xFFFFFF);
                    }

                    return 0xFFFFFFFF; // Default for unexpected indexes
                },

                ShopsItems.PINATA_FLARE.get()
        );

    }

}
