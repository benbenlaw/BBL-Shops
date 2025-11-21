package com.benbenlaw.shops.loaders;

import com.benbenlaw.shops.network.packets.SyncCratesToClients;
import com.benbenlaw.shops.network.packets.SyncPinatasToClients;
import com.benbenlaw.shops.network.packets.SyncShopRegistryToClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class CrateLoader extends SimpleJsonResourceReloadListener {

    private static final Map<ResourceLocation, CrateData> CRATES = new HashMap<>();

    public CrateLoader(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {

        CRATES.clear();

        jsonMap.forEach((id, json) -> {

            var result = CrateData.CODEC.parse(JsonOps.INSTANCE, json);

            result.resultOrPartial(message -> {
                System.err.println("Error loading pinata " + id + ": " + message);
            }).ifPresent(pinataData -> {
                CRATES.put(id, pinataData);
            });
        });
    }

    public static CrateData getCrate(ResourceLocation id) {
        return CRATES.get(id);
    }


    public static Map<ResourceLocation, CrateData> getAllCrates() {
        return Collections.unmodifiableMap(CRATES);
    }

    public static void sendLoaderInformation(ServerPlayer player) {
        List<CrateData> allEntries = new ArrayList<>();
        List<ResourceLocation> allIds = new ArrayList<>();

        for (var entry : CRATES.entrySet()) {
            allIds.add(entry.getKey());
            allEntries.add(entry.getValue());
        }

        PacketDistributor.sendToPlayer(player, new SyncCratesToClients(allIds, allEntries));
    }

    public static void clear() {
        CRATES.clear();
    }

    public static void register(ResourceLocation id, CrateData crateData) {
        CRATES.put(id, crateData);
    }

}
