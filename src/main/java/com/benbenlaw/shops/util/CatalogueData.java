package com.benbenlaw.shops.util;

import com.benbenlaw.shops.item.util.ShopEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CatalogueData {
    private final ResourceLocation id;
    private final List<ShopEntry> items;

    private CatalogueData(ResourceLocation id, List<ShopEntry> items) {
        this.id = id;
        this.items = items;
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<ShopEntry> getItems() {
        return items;
    }
}
