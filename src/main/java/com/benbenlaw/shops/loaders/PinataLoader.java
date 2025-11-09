package com.benbenlaw.shops.loaders;

import com.benbenlaw.shops.util.ChanceResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class PinataLoader extends SimpleJsonResourceReloadListener {

    private static final Map<ResourceLocation, PinataData> PINATAS = new HashMap<>();

    public PinataLoader(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {

        PINATAS.clear();

        jsonMap.forEach((id, json) -> {

            var result = PinataData.CODEC.parse(JsonOps.INSTANCE, json);

            result.resultOrPartial(message -> {
                System.err.println("Error loading pinata " + id + ": " + message);
            }).ifPresent(pinataData -> {
                PINATAS.put(id, pinataData);
            });
        });
    }

    public static List<ChanceResult> getRewardsForPinata(ResourceLocation id) {
        PinataData data = PINATAS.get(id);
        if (data != null) {
            return data.rewards();
        }
        return new ArrayList<>();
    }

    public static Map<ResourceLocation, PinataData> getAllPinatas() {
        return Collections.unmodifiableMap(PINATAS);
    }
}
