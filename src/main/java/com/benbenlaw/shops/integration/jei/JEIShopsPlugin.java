package com.benbenlaw.shops.integration.jei;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.events.client.ClientRecipeCache;
import com.benbenlaw.shops.item.ShopsItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIShopsPlugin implements IModPlugin {

    @Override
    public @NotNull Identifier getPluginUid() {
        return Shops.identifier("jei_plugin");
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(ShopsRecipeCategory.RECIPE_TYPE, new ItemStack(ShopsItems.COPPER_COIN.get()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ShopsRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(ShopsRecipeCategory.RECIPE_TYPE, ClientRecipeCache.getCachedShopRecipes().stream().toList());
    }
}
