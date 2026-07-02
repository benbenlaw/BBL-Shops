package com.benbenlaw.shops.item;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.config.StartUpConfig;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ShopsItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Shops.MOD_ID);

    public static final DeferredItem<Item> COPPER_COIN = ITEMS.registerItem("copper_coin",
            properties -> new CoinItem(properties, StartUpConfig.copperCoinValue.get()));

    public static final DeferredItem<Item> IRON_COIN = ITEMS.registerItem("iron_coin",
            properties -> new CoinItem(properties, StartUpConfig.ironCoinValue.get()));

    public static final DeferredItem<Item> GOLD_COIN = ITEMS.registerItem("gold_coin",
            properties -> new CoinItem(properties, StartUpConfig.goldCoinValue.get()));

}
