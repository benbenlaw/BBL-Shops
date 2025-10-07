package com.benbenlaw.shops.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShopCatalog {

    private final ItemStack catalogItem;
    private final List<ShopEntry> entries;

    public ShopCatalog(ItemStack catalogItem, List<ShopEntry> entries) {
        this.catalogItem = catalogItem;
        this.entries = entries;
    }

    public ItemStack getCatalogItem() {
        return catalogItem;
    }

    public List<ShopEntry> getEntries() {
        return entries;
    }

    public static final Codec<ShopCatalog> CODEC = RecordCodecBuilder.create(shopCatalogInstance -> shopCatalogInstance.group(
            ItemStack.CODEC.fieldOf("catalog").forGetter(ShopCatalog::getCatalogItem),
            ShopEntry.CODEC.listOf().fieldOf("entries").forGetter(ShopCatalog::getEntries
    )).apply(shopCatalogInstance, ShopCatalog::new));

}
