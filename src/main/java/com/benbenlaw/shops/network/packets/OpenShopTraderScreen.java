package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.screen.ClientScreens;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record OpenShopTraderScreen(Identifier traderId, String traderName) implements CustomPacketPayload {

    public static final Type<OpenShopTraderScreen> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Shops.MOD_ID, "open_shop_trader_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenShopTraderScreen> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, OpenShopTraderScreen::traderId,
            ByteBufCodecs.STRING_UTF8, OpenShopTraderScreen::traderName,
            OpenShopTraderScreen::new
    );

    public static final IPayloadHandler<OpenShopTraderScreen> HANDLER = (packet, context) ->
            ClientScreens.openShopTraderScreen(packet.traderId(), packet.traderName());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}