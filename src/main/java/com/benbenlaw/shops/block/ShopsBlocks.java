package com.benbenlaw.shops.block;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.item.ShopsItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ShopsBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Shops.MOD_ID);

    public static final DeferredBlock<ShopBlock> SHOP = registerBlock("shop",
            () -> new ShopBlock(Block.Properties.of().strength(2.0f).noOcclusion()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ShopsItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
