package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.screen.ClientScreens;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.List;

public record OpenShopBlockScreen(List<Identifier> nearbyTraders) implements CustomPacketPayload {

    public static final Type<OpenShopBlockScreen> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Shops.MOD_ID, "open_shop_block_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenShopBlockScreen> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), OpenShopBlockScreen::nearbyTraders,
            OpenShopBlockScreen::new
    );

    public static final IPayloadHandler<OpenShopBlockScreen> HANDLER = (packet, context) ->
            ClientScreens.openShopBlockScreen(packet.nearbyTraders());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
