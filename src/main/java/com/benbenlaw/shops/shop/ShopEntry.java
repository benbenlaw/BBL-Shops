package com.benbenlaw.shops.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

public class ShopEntry {

    public enum ShopMode {
        PLAYER_BUYS,
        PLAYER_SELLS
    }

    private final ItemStack item;
    private final int price;
    private final ShopMode mode;
    private final ItemStack requiredCatalog; // new

    public ShopEntry(ItemStack item, int price, ShopMode mode, ItemStack requiredCatalogue) {
        this.item = item;
        this.price = price;
        this.mode = mode;
        this.requiredCatalog = requiredCatalogue;
    }

    public ItemStack getItem() { return item; }
    public int getPrice() { return price; }
    public ShopMode getMode() { return mode; }
    public ItemStack getRequiredCatalogItem() { return requiredCatalog; } // new getter

    public static final Codec<ShopMode> MODE_CODEC = Codec.STRING.xmap(s -> ShopMode.valueOf(s.toUpperCase(Locale.ROOT)), ShopMode::name);

    public static final Codec<ShopEntry> CODEC = RecordCodecBuilder.create(shopEntryInstance -> shopEntryInstance.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(ShopEntry::getItem),
            Codec.INT.fieldOf("price").forGetter(ShopEntry::getPrice),
            MODE_CODEC.fieldOf("mode").forGetter(ShopEntry::getMode)
    ).apply(shopEntryInstance, (item, price, mode) -> new ShopEntry(item, price, mode, ItemStack.EMPTY)));


    public static final StreamCodec<RegistryFriendlyByteBuf, ShopEntry> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, ShopEntry::getItem,
            ByteBufCodecs.INT, ShopEntry::getPrice,
            ByteBufCodecs.STRING_UTF8, entry -> entry.getMode().name(),
            ItemStack.STREAM_CODEC, ShopEntry::getRequiredCatalogItem,
            (item, price, modeString, catalog) -> new ShopEntry(item, price, ShopMode.valueOf(modeString.toUpperCase()), catalog)
    );




}


