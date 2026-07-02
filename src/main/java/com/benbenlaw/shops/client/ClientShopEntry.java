package com.benbenlaw.shops.client;

import net.minecraft.world.item.Item;

public record ClientShopEntry(String namespace, Item item, int buyPrice, int sellPrice, String tier) {}
