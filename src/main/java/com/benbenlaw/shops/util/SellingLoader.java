package com.benbenlaw.shops.util;

import com.benbenlaw.shops.item.util.ShopEntry;
import com.benbenlaw.shops.item.util.ShopRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellingLoader extends SimpleJsonResourceReloadListener {

    public SellingLoader(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        ExchangeRegistry.EXCHANGE_RATES.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
            JsonElement root = entry.getValue();

            if (root.isJsonArray()) {
                for (JsonElement element : root.getAsJsonArray()) {
                    JsonObject obj = element.getAsJsonObject();

                    if (!obj.has("input") || !obj.has("price")) continue;

                    DataResult<ItemStack> stackResult = ItemStack.CODEC.parse(JsonOps.INSTANCE, obj.get("input"));
                    ItemStack stack = stackResult.result().orElse(ItemStack.EMPTY);

                    int price = obj.get("price").getAsInt();

                    ExchangeRegistry.EXCHANGE_RATES.put(stack, price);
                }
            } else if (root.isJsonObject()) {
                JsonObject obj = root.getAsJsonObject();

                if (!obj.has("input") || !obj.has("price")) continue;

                DataResult<ItemStack> stackResult = ItemStack.CODEC.parse(JsonOps.INSTANCE, obj.get("input"));
                ItemStack stack = stackResult.result().orElse(ItemStack.EMPTY);

                int price = obj.get("price").getAsInt();

                ExchangeRegistry.EXCHANGE_RATES.put(stack, price);
            }
        }
    }

}
