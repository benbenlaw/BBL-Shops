package com.benbenlaw.shops.client;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ClientShopEntry(Identifier entryId, String namespace, ItemStack stack, int buyPrice, int sellPrice, String tier) {}
