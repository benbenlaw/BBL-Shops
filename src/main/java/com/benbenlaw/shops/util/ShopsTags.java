package com.benbenlaw.shops.util;

import com.benbenlaw.shops.Shops;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ShopsTags {

    public static class Blocks {

        public static final TagKey<Block> SHOP_TRADER_BLOCKS = BlockTags.create(Shops.identifier("shop_trader_blocks"));

    }
}
