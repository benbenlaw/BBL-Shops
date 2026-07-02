package com.benbenlaw.shops.loader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public record ShopJsonEntry(Item item, int buyPrice, int sellPrice, String tier) {

    public static final Codec<ShopJsonEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ShopJsonEntry::item),
            Codec.INT.fieldOf("buy_price").forGetter(ShopJsonEntry::buyPrice),
            Codec.INT.fieldOf("sell_price").forGetter(ShopJsonEntry::sellPrice),
            Codec.STRING.optionalFieldOf("tier", "").forGetter(ShopJsonEntry::tier)
    ).apply(instance, ShopJsonEntry::new));
}