package com.benbenlaw.shops.data;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.ShopsBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ShopsBlockTagsProvider extends BlockTagsProvider {

    public ShopsBlockTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, Shops.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ShopsBlocks.SHOP_BLOCK.get());
    }
}
