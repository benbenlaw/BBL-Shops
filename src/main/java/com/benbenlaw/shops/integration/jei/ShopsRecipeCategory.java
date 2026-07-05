package com.benbenlaw.shops.integration.jei;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.events.client.ClientRecipeCache;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.recipe.ShopEntryRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ShopsRecipeCategory implements IRecipeCategory<ShopEntryRecipe> {

    public static final Identifier TEXTURE = Shops.identifier("textures/gui/shop_jei.png");
    public static final IRecipeType<ShopEntryRecipe> RECIPE_TYPE = IRecipeType.create(Shops.identifier("shop"), ShopEntryRecipe.class);

    private final int width = 85;
    private final int height = 20;
    private final IDrawable icon;

    @Override
    public @Nullable Identifier getIdentifier(ShopEntryRecipe recipe) {
        return ClientRecipeCache.getCachedShopRecipes().stream()
                .filter(r -> r.equals(recipe))
                .findFirst()
                .map(r -> {
                    // Find the corresponding ID in the cache map
                    for (Map.Entry<Identifier, ShopEntryRecipe> entry : ClientRecipeCache.cachedShopRecipes.entrySet()) {
                        if (entry.getValue().equals(recipe)) {
                            return entry.getKey();
                        }
                    }
                    return null;
                })
                .orElse(null);
    }

    public ShopsRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ShopsItems.COPPER_COIN.get()));
    }

    @Override
    public @NotNull IRecipeType<ShopEntryRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.shops.shop");
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ShopEntryRecipe recipe, @NotNull IFocusGroup focuses) {

        if (recipe.sellPrice() > 0) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).add(recipe.stack());

            builder.addSlot(RecipeIngredientRole.INPUT, 48, 2).add(new ItemStack(ShopsItems.COPPER_COIN.get())).addRichTooltipCallback(
                    (ingredients, tooltip) -> {
                        tooltip.clear();
                        tooltip.add(Component.translatable("jei.shops.selling_price", recipe.sellPrice()));

                        if (!recipe.tier().isEmpty()) {
                            tooltip.add(Component.translatable("jei.shops.tier", recipe.tier()));
                        }

                        if (recipe.trader().isPresent()) {
                            Block block = BuiltInRegistries.BLOCK.getValue(recipe.trader().get());
                            tooltip.add(Component.translatable("jei.shops.trader", block.getName()));
                        }

                    }
            );

        }

        if (recipe.buyPrice() > 0) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 2, 2).add(recipe.stack());

            builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 2).add(new ItemStack(ShopsItems.COPPER_COIN.get())).addRichTooltipCallback(
                    (ingredients, tooltip) -> {
                        tooltip.clear();
                        tooltip.add(Component.translatable("jei.shops.buying_price", recipe.buyPrice()));

                        if (!recipe.tier().isEmpty()) {
                            tooltip.add(Component.translatable("jei.shops.tier", recipe.tier()));
                        }

                        if (recipe.trader().isPresent()) {
                            Block block = BuiltInRegistries.BLOCK.getValue(recipe.trader().get());
                            tooltip.add(Component.translatable("jei.shops.trader", block.getName()));
                        }
                    }
            );
        }
    }


    @Override
    public void draw(ShopEntryRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor GuiGraphicsExtractor, double mouseX, double mouseY) {
        GuiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, width, height, width, height);
    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, ShopEntryRecipe recipe, @NotNull IFocusGroup focuses) {
        //builder.addAnimatedRecipeArrow(200).setPosition(41, 1);
    }
}
