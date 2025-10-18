package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.shop.ShopEntry;
import com.benbenlaw.shops.shop.ShopRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.List;

public record SyncShopRegistryToClient(List<ShopEntry> entries) implements CustomPacketPayload {

    public static final Type<SyncShopRegistryToClient> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "sync_registry_to_client"));

    public static final IPayloadHandler<SyncShopRegistryToClient> HANDLER = (packet, context) -> {

        ShopRegistry.clear();

        for (ShopEntry em : packet.entries()) {
            ShopRegistry.register(em);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncShopRegistryToClient> STREAM_CODEC = StreamCodec.composite(
            ShopEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncShopRegistryToClient::entries,
            SyncShopRegistryToClient::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

