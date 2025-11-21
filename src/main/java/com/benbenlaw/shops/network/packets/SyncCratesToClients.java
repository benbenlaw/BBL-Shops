package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.loaders.CrateData;
import com.benbenlaw.shops.loaders.CrateLoader;
import com.benbenlaw.shops.loaders.PinataLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.List;

public record SyncCratesToClients(List<ResourceLocation> ids, List<CrateData> entries) implements CustomPacketPayload {

    public static final Type<SyncCratesToClients> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "sync_crates_to_clients"));

    public static final IPayloadHandler<SyncCratesToClients> HANDLER = (packet, context) -> {

        CrateLoader.clear();

        for (int i = 0; i < packet.ids().size(); i++) {
            CrateLoader.register(packet.ids().get(i), packet.entries.get(i));
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCratesToClients> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncCratesToClients::ids,
            CrateData.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncCratesToClients::entries,
            SyncCratesToClients::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

