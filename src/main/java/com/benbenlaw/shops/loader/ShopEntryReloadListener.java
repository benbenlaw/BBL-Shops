package com.benbenlaw.shops.loader;

import com.benbenlaw.shops.Shops;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class ShopEntryReloadListener extends SimpleJsonResourceReloadListener<ShopJsonEntry> {

    private static Map<Identifier, ShopJsonEntry> loadedEntries = Map.of();

    public ShopEntryReloadListener() {
        super(ShopJsonEntry.CODEC, FileToIdConverter.json("shop_entries"));
    }

    @Override
    protected void apply(Map<Identifier, ShopJsonEntry> identifierShopJsonEntryMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        loadedEntries = Map.copyOf(identifierShopJsonEntryMap);
        Shops.LOGGER.info("Loaded {} shop entries", loadedEntries.size());
        ShopRegistry.setEntries(loadedEntries);
    }

}