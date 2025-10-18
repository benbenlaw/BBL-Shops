package com.benbenlaw.shops.shop;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ShopRegistry {

    public static final List<ShopEntry> SHOP_ENTRIES = new ArrayList<>();


    public static void clear() {
        SHOP_ENTRIES.clear();
    }

    public static void register(ShopEntry entry) {
        SHOP_ENTRIES.add(entry);
    }

    public static List<ShopEntry> getAllEntries() {
        return Collections.unmodifiableList(SHOP_ENTRIES);
    }

    public static List<ShopEntry> getByMode(ShopEntry.ShopMode mode) {
        List<ShopEntry> filtered = new ArrayList<>();
        for (ShopEntry entry : SHOP_ENTRIES) {
            if (entry.getMode() == mode) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    public static List<ShopEntry> getByCatalog(ItemStack catalogItem) {
        List<ShopEntry> filtered = new ArrayList<>();
        for (ShopEntry entry : SHOP_ENTRIES) {
            if (ItemStack.isSameItemSameComponents(entry.getRequiredCatalogItem(), catalogItem)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    public static ShopEntry getByItem(Item item) {
        for (ShopEntry entry : SHOP_ENTRIES) {
            if (ItemStack.isSameItemSameComponents(entry.getItem(), new ItemStack(item))) {
                return entry;
            }
        }
        return null;
    }

    public static int getPrice(ItemStack stack, ShopEntry.ShopMode mode) {
        for (ShopEntry entry : SHOP_ENTRIES) {
            if (ItemStack.isSameItemSameComponents(entry.getItem(), stack) && entry.getMode() == mode) {
                return entry.getPrice();
            }
        }
        return -1;
    }

    private static boolean synced = false;

    public static boolean isSynced() {
        return synced;
    }

    public static void setSynced(boolean value) {
        synced = value;
    }
}