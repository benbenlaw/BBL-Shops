package com.benbenlaw.shops.shop;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class CombinedShopLoader extends SimpleJsonResourceReloadListener {

    public CombinedShopLoader(Gson gson, String directory) {
        super(gson, directory);
    }


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {

        ShopRegistry.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
            DataResult<ShopCatalog> result = ShopCatalog.CODEC.parse(JsonOps.INSTANCE, entry.getValue());

            result.result().ifPresent(shopCatalog -> {
                for (ShopEntry item : shopCatalog.getEntries()) {
                    ShopRegistry.register(new ShopEntry(
                            item.getItem(),
                            item.getPrice(),
                            item.getMode(),
                            shopCatalog.getCatalogItem()
                    ));
                }
            });

            System.out.println("Loaded shop catalog: " + entry.getKey() + " with result: " + result);
        }
    }

    private void loadEntry(JsonElement element) {
        DataResult<ShopEntry> result = ShopEntry.CODEC.parse(JsonOps.INSTANCE, element);
        result.result().ifPresent(ShopRegistry::register);
    }
}
