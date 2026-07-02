package com.benbenlaw.shops.screen;

import com.benbenlaw.shops.events.client.ClientRecipeCache;
import com.benbenlaw.shops.recipe.ShopEntryRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class ClientScreens {

    public static void openShopScreen() {

        //TEST
        //Minecraft.getInstance().setScreen(new ShopScreen(Component.literal("Shop"), TestShopData.get()));

        //Actual
        Minecraft.getInstance().setScreen(new ShopScreen(Component.translatable("menu.shops.shop"), ClientRecipeCache.cachedShopRecipes));

    }
}