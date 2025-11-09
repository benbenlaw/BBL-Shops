package com.benbenlaw.shops.events;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.entity.ShopsEntities;
import net.minecraft.world.entity.animal.horse.Horse;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = Shops.MOD_ID)
public class AttributeEvent {

    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ShopsEntities.PINATA.get(), Horse.createBaseHorseAttributes().build());
    }


}
