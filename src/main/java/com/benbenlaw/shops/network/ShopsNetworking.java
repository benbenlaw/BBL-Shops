package com.benbenlaw.shops.network;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.network.packets.BuyShopItem;
import com.benbenlaw.shops.network.packets.OpenShopTraderScreen;
import com.benbenlaw.shops.network.packets.SellShopItem;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ShopsNetworking {

    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Shops.MOD_ID);

        //To Client From Server
        registrar.playToClient(SyncPlayerBalanceToClient.TYPE, SyncPlayerBalanceToClient.STREAM_CODEC, SyncPlayerBalanceToClient.HANDLER);
        registrar.playToClient(OpenShopTraderScreen.TYPE, OpenShopTraderScreen.STREAM_CODEC, OpenShopTraderScreen.HANDLER);

        //To Server From Client
        registrar.playToServer(BuyShopItem.TYPE, BuyShopItem.STREAM_CODEC, BuyShopItem.HANDLER);
        registrar.playToServer(SellShopItem.TYPE, SellShopItem.STREAM_CODEC, SellShopItem.HANDLER);
    }
}
