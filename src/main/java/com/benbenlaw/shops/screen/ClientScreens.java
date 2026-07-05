package com.benbenlaw.shops.screen;

import com.benbenlaw.shops.events.client.ClientRecipeCache;
import com.benbenlaw.shops.recipe.ShopEntryRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientScreens {

    public static void openShopScreen() {
        Minecraft.getInstance().setScreen(new ShopScreen(Component.translatable("menu.shops.shop"), ClientRecipeCache.cachedShopRecipes));
    }

    public static void openShopTraderScreen(Identifier traderId, String traderName) {
        Map<Identifier, ShopEntryRecipe> filtered = new HashMap<>();
        for (Map.Entry<Identifier, ShopEntryRecipe> entry : ClientRecipeCache.cachedShopRecipes.entrySet()) {
            if (entry.getValue().trader().equals(Optional.of(traderId))) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }

        Component title = (traderName == null || traderName.isEmpty())
                ? Component.translatable("menu.shops.shop")
                : Component.literal(traderName);

        Minecraft.getInstance().setScreen(new ShopScreen(title, filtered, traderId));
    }
}