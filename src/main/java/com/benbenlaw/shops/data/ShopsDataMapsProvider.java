package com.benbenlaw.shops.data;

import com.benbenlaw.shops.datamaps.ShopsDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class ShopsDataMapsProvider extends DataMapProvider {

    public ShopsDataMapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(ShopsDataMaps.TRADER_NAMES).build();
    }
}