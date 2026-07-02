package com.benbenlaw.shops.recipe;

import com.benbenlaw.shops.Shops;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ShopsRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Shops.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Shops.MOD_ID);


    //Shops
    public static final Supplier<RecipeSerializer<ShopEntryRecipe>> SHOP_SERIALIZER =
            SERIALIZER.register("shop", () -> ShopEntryRecipe.SERIALIZER);
    public static final Supplier<RecipeType<ShopEntryRecipe>> SHOP_TYPE =
            TYPES.register("shop", () -> ShopEntryRecipe.TYPE);



}
