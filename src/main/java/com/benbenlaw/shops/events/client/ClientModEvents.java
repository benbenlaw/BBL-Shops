package com.benbenlaw.shops.events.client;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.entity.ShopsEntities;
import com.benbenlaw.shops.renderer.PinataRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Shops.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ShopsEntities.PINATA.get(), PinataRenderer::new);
    }

}
