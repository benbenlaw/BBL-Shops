package com.benbenlaw.shops.item.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class ShopRegistry {

    public static final Map<ItemStack, List<ShopEntry>> CATALOGUE_ITEMS = new HashMap<>();
    public static final List<ShopEntry> SHOP_ITEMS = new ArrayList<>();

    public static ShopEntry getShopItem(Item item) {

        return SHOP_ITEMS.stream()
                .filter(shopItem -> shopItem.getItem().getItem().equals(item))
                .findFirst()
                .orElse(null);
    }

    public static List<ShopEntry> getCatalogueItems(ItemStack catalogueStack) {
        return CATALOGUE_ITEMS.entrySet().stream()
                .filter(entry -> ItemStack.isSameItemSameComponents(entry.getKey(), catalogueStack))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(Collections.emptyList());
    }

}