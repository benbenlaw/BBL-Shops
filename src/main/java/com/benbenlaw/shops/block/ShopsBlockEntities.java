package com.benbenlaw.shops.block;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.entity.ShopBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ShopsBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Shops.MOD_ID);

    public static final Supplier<BlockEntityType<ShopBlockEntity>> SHOP_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("shop_block_entity", () ->
                    new BlockEntityType<>(ShopBlockEntity::new, ShopsBlocks.SHOP_BLOCK.get()));
}
