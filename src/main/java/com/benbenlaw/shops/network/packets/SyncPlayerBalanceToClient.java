package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.capability.ShopsAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record SyncPlayerBalanceToClient(int balance) implements CustomPacketPayload {

    public static final Type<SyncPlayerBalanceToClient> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "sync_player_balance_to_client"));

    public static final IPayloadHandler<SyncPlayerBalanceToClient> HANDLER = (packet, context) -> {
        Player player = context.player();

        player.getData(ShopsAttachments.PLAYER_BALANCE).setBalance(packet.balance);
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerBalanceToClient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncPlayerBalanceToClient::balance,
            SyncPlayerBalanceToClient::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
