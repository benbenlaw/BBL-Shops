package com.benbenlaw.shops.integration.jei;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.ShopsBlocks;
import com.benbenlaw.shops.integration.jei.dummy.BuyingRecipe;
import com.benbenlaw.shops.integration.jei.dummy.SellingRecipe;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.shop.ShopEntry;
import com.benbenlaw.shops.shop.ShopRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
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
        Map<Item, List<ShopEntry>> buyGroups = new HashMap<>();
        Map<Item, List<ShopEntry>> sellGroups = new HashMap<>();

        for (ShopEntry entry : ShopRegistry.getAllEntries()) {
            ItemStack catalogueStack = entry.getRequiredCatalogItem().copy();
            Item catalogueItem = catalogueStack.getItem();

            if (entry.getMode() == ShopEntry.ShopMode.PLAYER_SELLS) {
                sellGroups.computeIfAbsent(catalogueItem, k -> new ArrayList<>()).add(entry);
            } else if (entry.getMode() == ShopEntry.ShopMode.PLAYER_BUYS) {
                buyGroups.computeIfAbsent(catalogueItem, k -> new ArrayList<>()).add(entry);
            }
        }

        List<BuyingRecipe> buyingRecipes = new ArrayList<>();
        List<SellingRecipe> sellingRecipes = new ArrayList<>();

        for (Map.Entry<Item, List<ShopEntry>> e : buyGroups.entrySet()) {
            Item catalogueItem = e.getKey();
            ItemStack catalogueStack = new ItemStack(catalogueItem); // JEI icon

            List<ItemStack> inputs = new ArrayList<>();
            List<Integer> prices = new ArrayList<>();

            for (ShopEntry entry : e.getValue()) {
                inputs.add(entry.getItem().copy());
                prices.add(entry.getPrice());
            }

            buyingRecipes.add(new BuyingRecipe(catalogueStack, inputs, prices));
        }

        for (Map.Entry<Item, List<ShopEntry>> e : sellGroups.entrySet()) {
            Item catalogueItem = e.getKey();
            ItemStack catalogueStack = new ItemStack(catalogueItem); // JEI icon

            List<ItemStack> inputs = new ArrayList<>();
            List<Integer> prices = new ArrayList<>();

            for (ShopEntry entry : e.getValue()) {
                inputs.add(entry.getItem().copy());
                prices.add(entry.getPrice());
            }

            sellingRecipes.add(new SellingRecipe(catalogueStack, inputs, prices));
        }

        registration.addRecipes(BUYING_RECIPE_TYPE, buyingRecipes);
        registration.addRecipes(SELLING_RECIPE_TYPE, sellingRecipes);
    }
}
