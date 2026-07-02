package com.benbenlaw.shops.client;

import java.util.List;

public class ClientShopRegistry {

    private static List<ClientShopEntry> entries = List.of();

    public static void setEntries(List<ClientShopEntry> newEntries) {
        entries = newEntries;
    }

    public static List<ClientShopEntry> all() {
        return entries;
    }

    public static void clear() {
        entries = List.of();
    }
}