package com.benbenlaw.shops.block;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.custom.ShopBlock;
import com.benbenlaw.shops.item.ShopsItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class ShopsBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Shops.MOD_ID);

    public static final DeferredBlock<Block> SHOP_BLOCK = registerBlock("shop_block",
            properties -> new ShopBlock(machineProperties(properties)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, block);
        return block;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ShopsItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    private static BlockBehaviour.Properties machineProperties(BlockBehaviour.Properties machineProperties) {
        return machineProperties
                .requiresCorrectToolForDrops()
                .strength(3.5f)
                .noOcclusion();
    }
}
