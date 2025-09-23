package com.benbenlaw.shops.util;

import com.benbenlaw.shops.item.util.ShopEntry;
import com.benbenlaw.shops.item.util.ShopRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopCatalogueLoader extends SimpleJsonResourceReloadListener {

    public ShopCatalogueLoader(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        ShopRegistry.CATALOGUE_ITEMS.clear();
        ShopRegistry.SHOP_ITEMS.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
            JsonObject obj = entry.getValue().getAsJsonObject();

            if (!obj.has("catalogue_item") || !obj.has("items")) continue;

            // Deserialize catalogue ItemStack
            DataResult<ItemStack> catResult =
                    ItemStack.CODEC.parse(JsonOps.INSTANCE, obj.get("catalogue_item"));
            ItemStack catalogueStack = catResult.result().orElse(ItemStack.EMPTY);

            List<ShopEntry> entries = new ArrayList<>();
            for (JsonElement itemElem : obj.getAsJsonArray("items")) {
                JsonObject itemObj = itemElem.getAsJsonObject();
                int price = itemObj.get("price").getAsInt();

                // Deserialize shop ItemStack
                DataResult<ItemStack> result =
                        ItemStack.CODEC.parse(JsonOps.INSTANCE, itemObj.get("stack"));
                ItemStack stack = result.result().orElse(ItemStack.EMPTY);

                entries.add(new ShopEntry(stack, price, catResult.getOrThrow()));
            }

            ShopRegistry.CATALOGUE_ITEMS.put(catalogueStack, entries);
            ShopRegistry.SHOP_ITEMS.addAll(entries);
        }
    }

}
