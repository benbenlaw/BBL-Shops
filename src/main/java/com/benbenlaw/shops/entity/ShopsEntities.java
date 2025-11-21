package com.benbenlaw.shops.entity;

import com.benbenlaw.shops.Shops;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ShopsEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Shops.MOD_ID);

    public static final Supplier<EntityType<PinataEntity>> PINATA =
            ENTITIES.register("pinata", () -> EntityType.Builder.of(PinataEntity::new, MobCategory.CREATURE)
                    .sized(0.7f, 0.8F)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .canSpawnFarFromPlayer()
                    .build(Shops.MOD_ID + ":pinata"));

    public static final Supplier<EntityType<CrateEntity>> CRATE =
            ENTITIES.register("crate", () -> EntityType.Builder.of(CrateEntity::new, MobCategory.MISC)
                    .sized(1f, 1F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(Shops.MOD_ID + ":crate"));

}
