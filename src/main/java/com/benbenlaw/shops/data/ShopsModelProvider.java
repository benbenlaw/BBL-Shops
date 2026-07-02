package com.benbenlaw.shops.data;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.item.ShopsItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class ShopsModelProvider extends ModelProvider {

    public ShopsModelProvider(PackOutput output) {
        super(output, Shops.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        itemModels.generateFlatItem(ShopsItems.COPPER_COIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ShopsItems.IRON_COIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ShopsItems.GOLD_COIN.get(), ModelTemplates.FLAT_ITEM);

    }

    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
        return ShopsItems.ITEMS.getEntries().stream();
    }
}