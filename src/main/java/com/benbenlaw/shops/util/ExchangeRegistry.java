package com.benbenlaw.shops.util;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ExchangeRegistry {

    public static final Map<ItemStack, Integer> EXCHANGE_RATES = new HashMap<>();

    public static int getValue(ItemStack stack) {
        return EXCHANGE_RATES.entrySet().stream()
                .filter(entry -> ItemStack.isSameItemSameComponents(entry.getKey(), stack))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(0);
    }

    public static int getRequiredAmount(ItemStack stack) {
        for (ItemStack key : EXCHANGE_RATES.keySet()) {
            if (ItemStack.isSameItemSameComponents(key, stack)) {
                return key.getCount();
            }
        }
        return 0;
    }
}