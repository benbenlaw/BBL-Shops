package com.benbenlaw.shops.item.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShopEntry {
    private final ItemStack item;
    private final int price;
    private final ItemStack requiredCatalogue; // new

    public ShopEntry(ItemStack item, int price, ItemStack requiredCatalogue) {
        this.item = item;
        this.price = price;
        this.requiredCatalogue = requiredCatalogue;
    }

    public ItemStack getItem() { return item; }
    public int getPrice() { return price; }
    public ItemStack getRequiredCatalogueItem() { return requiredCatalogue; } // new getter
}
