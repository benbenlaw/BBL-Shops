package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.client.ClientShopEntry;
import com.benbenlaw.shops.client.ClientShopRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.List;

public record SyncShopEntriesToClient(List<Entry> entries) implements CustomPacketPayload {

    public static final Type<SyncShopEntriesToClient> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Shops.MOD_ID, "sync_shop_entries_to_client"));

    public static final IPayloadHandler<SyncShopEntriesToClient> HANDLER = (packet, context) -> {
        var entries = packet.entries().stream()
                .map(entry -> new ClientShopEntry(
                        entry.entryId().getNamespace(),
                        BuiltInRegistries.ITEM.getValue(entry.itemId()),
                        entry.buyPrice(),
                        entry.sellPrice(),
                        entry.tier()
                ))
                .toList();

        ClientShopRegistry.setEntries(entries);
    };

    public record Entry(Identifier entryId, Identifier itemId, int buyPrice, int sellPrice, String tier) {

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, Entry::entryId,
                Identifier.STREAM_CODEC, Entry::itemId,
                ByteBufCodecs.VAR_INT, Entry::buyPrice,
                ByteBufCodecs.VAR_INT, Entry::sellPrice,
                ByteBufCodecs.STRING_UTF8, Entry::tier,
                Entry::new
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncShopEntriesToClient> STREAM_CODEC = StreamCodec.composite(
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncShopEntriesToClient::entries,
            SyncShopEntriesToClient::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}