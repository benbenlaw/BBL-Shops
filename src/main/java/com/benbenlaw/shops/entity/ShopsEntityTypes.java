package com.benbenlaw.shops.entity;

import com.benbenlaw.shops.Shops;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ShopsEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Shops.MOD_ID);

    public static final ResourceKey<EntityType<?>> SHOP_TRADER_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Shops.MOD_ID, "shop_trader"));

    public static final Supplier<EntityType<ShopTraderMob>> SHOP_TRADER =
            ENTITY_TYPES.register("shop_trader", () -> EntityType.Builder.of(ShopTraderMob::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .build(SHOP_TRADER_KEY));
}