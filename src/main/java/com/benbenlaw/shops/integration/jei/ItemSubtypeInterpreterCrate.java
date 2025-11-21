package com.benbenlaw.shops.integration.jei;

import com.benbenlaw.shops.item.ShopsDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemSubtypeInterpreterCrate implements ISubtypeInterpreter<ItemStack> {
    @Override
    public @Nullable Object getSubtypeData(ItemStack stack, UidContext uidContext) {
        return stack.getOrDefault(ShopsDataComponents.CRATE_ID, "");
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext uidContext) {
        return stack.getOrDefault(ShopsDataComponents.CRATE_ID, "").toString();
    }
}
