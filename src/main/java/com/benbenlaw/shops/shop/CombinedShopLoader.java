package com.benbenlaw.shops.shop;

import com.benbenlaw.shops.network.packets.SyncShopRegistryToClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombinedShopLoader extends SimpleJsonResourceReloadListener {

    public CombinedShopLoader(Gson gson, String directory) {
        super(gson, directory);
    }

    private static final Map<ResourceLocation, ShopCatalog> LOADED_SHOPS = new HashMap<>();


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {

        ShopRegistry.clear();
        LOADED_SHOPS.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
            DataResult<ShopCatalog> result = ShopCatalog.CODEC.parse(JsonOps.INSTANCE, entry.getValue());

            result.result().ifPresent(shopCatalog -> {
                LOADED_SHOPS.put(entry.getKey(), shopCatalog);

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

    public static void sendShopsPacketToPlayer(ServerPlayer player) {
        List<ShopEntry> allEntries = new ArrayList<>();

        for (ShopCatalog catalog : LOADED_SHOPS.values()) {
            for (ShopEntry item : catalog.getEntries()) {
                // preserve the catalog reference
                allEntries.add(new ShopEntry(
                        item.getItem(),
                        item.getPrice(),
                        item.getMode(),
                        catalog.getCatalogItem()
                ));
            }
        }

        PacketDistributor.sendToPlayer(player, new SyncShopRegistryToClient(allEntries));
    }


    private void loadEntry(JsonElement element) {
        DataResult<ShopEntry> result = ShopEntry.CODEC.parse(JsonOps.INSTANCE, element);
        result.result().ifPresent(ShopRegistry::register);
    }
}
