package com.benbenlaw.shops.item;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.config.StartUpConfig;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ShopsItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Shops.MOD_ID);

    public static final DeferredItem<Item> PLAYER_BALANCE_CARD = ITEMS.register("player_balance_card",
            () -> new PlayerBalanceCard(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> COPPER_COIN = ITEMS.register("copper_coin",
            () -> new CoinItem(new Item.Properties(), StartUpConfig.copperCoinValue.get()));

    public static final DeferredItem<Item> IRON_COIN = ITEMS.register("iron_coin",
            () -> new CoinItem(new Item.Properties(), StartUpConfig.ironCoinValue.get()));

    public static final DeferredItem<Item> GOLD_COIN = ITEMS.register("gold_coin",
            () -> new CoinItem(new Item.Properties(), StartUpConfig.goldCoinValue.get()));





}
