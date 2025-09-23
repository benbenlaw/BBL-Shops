package com.benbenlaw.shops.integration.jei.dummy;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record BuyingRecipe(ItemStack catalog, List<ItemStack> inputs, List<Integer> prices) {
}
