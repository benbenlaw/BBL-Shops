package com.benbenlaw.shops.network;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.network.packets.BuyShopItem;
import com.benbenlaw.shops.network.packets.OpenShopBlockScreen;
import com.benbenlaw.shops.network.packets.OpenShopBlockSelectorScreen;
import com.benbenlaw.shops.network.packets.OpenShopTraderScreen;
import com.benbenlaw.shops.network.packets.SellShopItem;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.network.packets.ToggleShopBlockAutoBuy;
import com.benbenlaw.shops.network.packets.ToggleShopBlockSellItem;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ShopsNetworking {

    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Shops.MOD_ID);

        //To Client From Server
        registrar.playToClient(SyncPlayerBalanceToClient.TYPE, SyncPlayerBalanceToClient.STREAM_CODEC, SyncPlayerBalanceToClient.HANDLER);
        registrar.playToClient(OpenShopTraderScreen.TYPE, OpenShopTraderScreen.STREAM_CODEC, OpenShopTraderScreen.HANDLER);
        registrar.playToClient(OpenShopBlockScreen.TYPE, OpenShopBlockScreen.STREAM_CODEC, OpenShopBlockScreen.HANDLER);
        registrar.playToClient(OpenShopBlockSelectorScreen.TYPE, OpenShopBlockSelectorScreen.STREAM_CODEC, OpenShopBlockSelectorScreen.HANDLER);

        //To Server From Client
        registrar.playToServer(BuyShopItem.TYPE, BuyShopItem.STREAM_CODEC, BuyShopItem.HANDLER);
        registrar.playToServer(SellShopItem.TYPE, SellShopItem.STREAM_CODEC, SellShopItem.HANDLER);
        registrar.playToServer(ToggleShopBlockAutoBuy.TYPE, ToggleShopBlockAutoBuy.STREAM_CODEC, ToggleShopBlockAutoBuy.HANDLER);
        registrar.playToServer(ToggleShopBlockSellItem.TYPE, ToggleShopBlockSellItem.STREAM_CODEC, ToggleShopBlockSellItem.HANDLER);
    }
}
