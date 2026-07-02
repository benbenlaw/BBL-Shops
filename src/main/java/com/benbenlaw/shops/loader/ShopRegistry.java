package com.benbenlaw.shops.loader;

import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;

public class ShopRegistry {

    private static Map<Identifier, ShopJsonEntry> entries = Map.of();

    public static void setEntries(Map<Identifier, ShopJsonEntry> newEntries) {
        entries = newEntries;
    }

    public static Optional<ShopJsonEntry> get(Identifier id) {
        return Optional.ofNullable(entries.get(id));
    }

    public static Map<Identifier, ShopJsonEntry> all() {
        return entries;
    }
}