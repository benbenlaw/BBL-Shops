package com.benbenlaw.shops.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public record ShopEntryRecipe(ItemStackTemplate stack, int buyPrice, int sellPrice, String tier, int order) implements Recipe<RecipeInput> {

    public static final MapCodec<ShopEntryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ItemStackTemplate.CODEC.fieldOf("item").forGetter(ShopEntryRecipe::stack),
                    Codec.INT.fieldOf("buy_price").forGetter(ShopEntryRecipe::buyPrice),
                    Codec.INT.fieldOf("sell_price").forGetter(ShopEntryRecipe::sellPrice),
                    Codec.STRING.optionalFieldOf("tier", "").forGetter(ShopEntryRecipe::tier),
                    Codec.INT.optionalFieldOf("order", 0).forGetter(ShopEntryRecipe::order)
            ).apply(instance, ShopEntryRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopEntryRecipe> STREAM_CODEC = StreamCodec.of(
            ShopEntryRecipe::write, ShopEntryRecipe::read);

    public static final RecipeType<ShopEntryRecipe> TYPE = new RecipeType<>() {};

    public static final RecipeSerializer<ShopEntryRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static ShopEntryRecipe read(RegistryFriendlyByteBuf buffer) {
        ItemStackTemplate stack = ItemStackTemplate.STREAM_CODEC.decode(buffer);
        int buyPrice = buffer.readVarInt();
        int sellPrice = buffer.readVarInt();
        String tier = ByteBufCodecs.STRING_UTF8.decode(buffer);
        int order = buffer.readVarInt();
        return new ShopEntryRecipe(stack, buyPrice, sellPrice, tier, order);
    }

    private static void write(RegistryFriendlyByteBuf buffer, ShopEntryRecipe recipe) {
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.stack);
        buffer.writeVarInt(recipe.buyPrice);
        buffer.writeVarInt(recipe.sellPrice);
        ByteBufCodecs.STRING_UTF8.encode(buffer, recipe.tier);
        buffer.writeVarInt(recipe.order);
    }

    @Override
    public boolean matches(@NotNull RecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    public @NonNull ItemStack assemble(RecipeInput recipeInput) {
        return stack.create().copy();
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return TYPE;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }
}