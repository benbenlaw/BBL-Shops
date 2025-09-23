package com.benbenlaw.shops.integration.jei;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.integration.jei.dummy.SellingRecipe;
import com.benbenlaw.shops.item.ShopsItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.gui.widgets.IScrollGridWidgetFactory;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SellingRecipeCategory implements IRecipeCategory<SellingRecipe> {

    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "selling_recipe");
    public final static ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "textures/gui/jei_selling.png");
    public final IDrawable background;
    public final IDrawable icon;
    private final IScrollGridWidgetFactory<?> scrollGridWidgetFactory;

    public SellingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 142, 37);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ShopsItems.GOLD_COIN.get()));
        this.scrollGridWidgetFactory = helper.createScrollGridFactory(7, 2);
        this.scrollGridWidgetFactory.setPosition(0, 0);
    }

    @Override
    public RecipeType<SellingRecipe> getRecipeType() {
        return JEIShopsPlugin.SELLING_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.shops.selling");
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SellingRecipe sellingRecipe, IFocusGroup iFocusGroup) {

        for (var result : getInputs(sellingRecipe)) {
            int i = sellingRecipe.inputs().indexOf(result);
            builder.addSlotToWidget(RecipeIngredientRole.OUTPUT, this.scrollGridWidgetFactory)
                    .addItemStack(sellingRecipe.inputs().get(i))
                    .addRichTooltipCallback((slotView, tooltip) -> {
                        int price = sellingRecipe.prices().get(i);
                        tooltip.add(Component.translatable("jei.shops.selling_price", price).withStyle(ChatFormatting.GOLD));
                    });
        }

    }

    private static List<ItemStack> getInputs(SellingRecipe sellingRecipe) {
        return sellingRecipe.inputs();
    }
}
