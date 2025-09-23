package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.capability.ShopsAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record SyncPurchaseToServer(int balance, ItemStack stack) implements CustomPacketPayload {

    public static final Type<SyncPurchaseToServer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "sync_purchase_to_server"));

    public static final IPayloadHandler<SyncPurchaseToServer> HANDLER = (packet, context) -> {
        Player player = context.player();

        player.getData(ShopsAttachments.PLAYER_BALANCE).setBalance(packet.balance);
        player.getInventory().add(packet.stack);

    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPurchaseToServer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncPurchaseToServer::balance,
            ItemStack.OPTIONAL_STREAM_CODEC, SyncPurchaseToServer::stack,
            SyncPurchaseToServer::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
