package com.benbenlaw.shops.network;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.network.packets.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ShopsNetworking {

    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Shops.MOD_ID);

        //To Client From Server
        registrar.playToClient(SyncPlayerBalanceToClient.TYPE, SyncPlayerBalanceToClient.STREAM_CODEC, SyncPlayerBalanceToClient.HANDLER);
        registrar.playToClient(SyncAutoItemToClient.TYPE, SyncAutoItemToClient.STREAM_CODEC, SyncAutoItemToClient.HANDLER);
        registrar.playToClient(SyncShopRegistryToClient.TYPE, SyncShopRegistryToClient.STREAM_CODEC, SyncShopRegistryToClient.HANDLER);

        //To Server From Client
        registrar.playToServer(SyncPurchaseToServer.TYPE, SyncPurchaseToServer.STREAM_CODEC, SyncPurchaseToServer.HANDLER);
        registrar.playToServer(SyncAutoItemToServer.TYPE, SyncAutoItemToServer.STREAM_CODEC, SyncAutoItemToServer.HANDLER);


    }
}
