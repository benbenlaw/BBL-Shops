package com.benbenlaw.shops.data;

import com.benbenlaw.core.util.ColorUtils;
import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.ShopsBlocks;
import com.benbenlaw.shops.item.ShopsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ShopsRecipeProvider extends RecipeProvider {

    public ShopsRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput) {
            return new ShopsRecipeProvider(provider, recipeOutput);
        }

        @Override
        public @NotNull String getName() {
            return Shops.MOD_ID + " Recipes";
        }
    }

    @Override
    protected void buildRecipes() {

        //Shop Block
        shaped(RecipeCategory.MISC, ShopsBlocks.SHOP_BLOCK.get())
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', Tags.Items.STONES)
                .define('B', ShopsItems.COPPER_COIN.get())
                .group("shops")
                .unlockedBy("has_item", has(ShopsItems.COPPER_COIN))
                .save(output);

        //Player Balance Card
        shaped(RecipeCategory.MISC, ShopsItems.PLAYER_BALANCE_CARD.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.PAPER)
                .define('B', ShopsItems.COPPER_COIN.get())
                .group("shops")
                .unlockedBy("has_item", has(ShopsItems.COPPER_COIN))
                .save(output);

    }
}
