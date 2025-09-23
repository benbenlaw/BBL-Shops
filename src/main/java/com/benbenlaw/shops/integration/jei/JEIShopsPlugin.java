package com.benbenlaw.shops.integration.jei;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.ShopsBlocks;
import com.benbenlaw.shops.integration.jei.dummy.BuyingRecipe;
import com.benbenlaw.shops.integration.jei.dummy.SellingRecipe;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.item.util.ShopEntry;
import com.benbenlaw.shops.item.util.ShopRegistry;
import com.benbenlaw.shops.util.ExchangeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class JEIShopsPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "jei_plugin");
    }

    public static final RecipeType<SellingRecipe> SELLING_RECIPE_TYPE =
            new RecipeType<>(SellingRecipeCategory.UID, SellingRecipe.class);

    public static final RecipeType<BuyingRecipe> BUYING_RECIPE_TYPE =
            new RecipeType<>(BuyingRecipeCategory.UID, BuyingRecipe.class);



    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ShopsBlocks.SHOP.get(), SELLING_RECIPE_TYPE);
        registration.addRecipeCatalyst(ShopsItems.COPPER_COIN.get(), SELLING_RECIPE_TYPE);
        registration.addRecipeCatalyst(ShopsItems.IRON_COIN.get(), SELLING_RECIPE_TYPE);
        registration.addRecipeCatalyst(ShopsItems.GOLD_COIN.get(), SELLING_RECIPE_TYPE);
        registration.addRecipeCatalyst(ShopsItems.PLAYER_BALANCE_CARD.get(), SELLING_RECIPE_TYPE);

        registration.addRecipeCatalyst(ShopsBlocks.SHOP.get(), BUYING_RECIPE_TYPE);
        registration.addRecipeCatalyst(ShopsItems.COPPER_COIN.get(), BUYING_RECIPE_TYPE);
        registration.addRecipeCatalyst(ShopsItems.IRON_COIN.get(), BUYING_RECIPE_TYPE);
        registration.addRecipeCatalyst(ShopsItems.GOLD_COIN.get(), BUYING_RECIPE_TYPE);
        registration.addRecipeCatalyst(ShopsItems.PLAYER_BALANCE_CARD.get(), BUYING_RECIPE_TYPE);

    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new SellingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BuyingRecipeCategory(guiHelper));
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<ItemStack> allStacks = new ArrayList<>();
        List<Integer> allPrices = new ArrayList<>();

        for (Map.Entry<ItemStack, Integer> entry : ExchangeRegistry.EXCHANGE_RATES.entrySet()) {
            ItemStack stack = entry.getKey().copy();
            stack.setCount(entry.getKey().getCount()); // keep the original count
            allStacks.add(stack);
            allPrices.add(entry.getValue()); // store the price separately
        }

        SellingRecipe combinedRecipe = new SellingRecipe(allStacks, allPrices);
        registration.addRecipes(SELLING_RECIPE_TYPE, List.of(combinedRecipe));

        List<BuyingRecipe> recipes = new ArrayList<>();

        for (Map.Entry<ItemStack, List<ShopEntry>> entry : ShopRegistry.CATALOGUE_ITEMS.entrySet()) {
            ItemStack catalogueStack = entry.getKey().copy();
            List<ShopEntry> shopEntries = entry.getValue();

            List<ItemStack> inputs = new ArrayList<>();
            List<Integer> prices = new ArrayList<>();

            for (ShopEntry shopEntry : shopEntries) {
                inputs.add(shopEntry.getItem().copy());
                prices.add(shopEntry.getPrice());
            }

            BuyingRecipe recipe = new BuyingRecipe(catalogueStack, inputs, prices);
            recipes.add(recipe);
        }

        registration.addRecipes(BUYING_RECIPE_TYPE, recipes);
    }
}
