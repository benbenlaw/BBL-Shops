package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.screen.ClientScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.List;

public record OpenShopBlockSelectorScreen(BlockPos pos, List<Identifier> autoBuyTargets,
                                           List<Identifier> sellWhitelist, List<Identifier> nearbyTraders) implements CustomPacketPayload {

    public static final Type<OpenShopBlockSelectorScreen> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Shops.MOD_ID, "open_shop_block_selector_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenShopBlockSelectorScreen> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenShopBlockSelectorScreen::pos,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), OpenShopBlockSelectorScreen::autoBuyTargets,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), OpenShopBlockSelectorScreen::sellWhitelist,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), OpenShopBlockSelectorScreen::nearbyTraders,
            OpenShopBlockSelectorScreen::new
    );

    public static final IPayloadHandler<OpenShopBlockSelectorScreen> HANDLER = (packet, context) ->
            ClientScreens.openShopBlockSelector(packet.pos(), packet.autoBuyTargets(), packet.sellWhitelist(), packet.nearbyTraders());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
