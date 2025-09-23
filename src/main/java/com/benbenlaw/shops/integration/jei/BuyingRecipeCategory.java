package com.benbenlaw.shops.integration.jei;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.integration.jei.dummy.BuyingRecipe;
import com.benbenlaw.shops.integration.jei.dummy.SellingRecipe;
import com.benbenlaw.shops.item.ShopsItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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

import java.util.List;

public class BuyingRecipeCategory implements IRecipeCategory<BuyingRecipe> {

    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "buying_recipe");
    public final static ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "textures/gui/jei_buying.png");
    public final IDrawable background;
    public final IDrawable icon;
    private final IScrollGridWidgetFactory<?> scrollGridWidgetFactory;

    public BuyingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 142, 37);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ShopsItems.GOLD_COIN.get()));
        this.scrollGridWidgetFactory = helper.createScrollGridFactory(5, 2);
        this.scrollGridWidgetFactory.setPosition(32, 0);
    }

    @Override
    public RecipeType<BuyingRecipe> getRecipeType() {
        return JEIShopsPlugin.BUYING_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.shops.buying");
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
    public void setRecipe(IRecipeLayoutBuilder builder, BuyingRecipe buyingRecipe, IFocusGroup iFocusGroup) {

        builder.addSlot(RecipeIngredientRole.CATALYST, 7, 11)
                .addItemStack(buyingRecipe.catalog());

        for (var result : getInputs(buyingRecipe)) {
            int i = buyingRecipe.inputs().indexOf(result);
            builder.addSlotToWidget(RecipeIngredientRole.OUTPUT, this.scrollGridWidgetFactory)
                    .addItemStack(buyingRecipe.inputs().get(i))
                    .addRichTooltipCallback((slotView, tooltip) -> {
                        int price = buyingRecipe.prices().get(i);
                        tooltip.add(Component.translatable("jei.shops.buying_price", price).withStyle(ChatFormatting.GOLD));
                    });
        }
    }

    private List<ItemStack> getInputs(BuyingRecipe recipe) {
        return recipe.inputs();
    }

}
