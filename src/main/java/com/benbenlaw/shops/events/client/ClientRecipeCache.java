package com.benbenlaw.shops.events.client;

import com.benbenlaw.shops.recipe.ShopEntryRecipe;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientRecipeCache {

    //Shop Recipes
    public static Map<Identifier, ShopEntryRecipe> cachedShopRecipes = new HashMap<>();

    public static void setCachedShopRecipes(Map<Identifier, ShopEntryRecipe> cachedShopRecipes) {
        ClientRecipeCache.cachedShopRecipes = cachedShopRecipes;
    }

    public static Collection<ShopEntryRecipe> getCachedShopRecipes() {
        return cachedShopRecipes.values();
    }

}
