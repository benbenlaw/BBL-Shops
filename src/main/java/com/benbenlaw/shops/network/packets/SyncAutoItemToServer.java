package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.entity.ShopBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record SyncAutoItemToServer(BlockPos pos, ItemStack stack) implements CustomPacketPayload {

    public static final Type<SyncAutoItemToServer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "sync_auto_item_to_server"));

    public static final IPayloadHandler<SyncAutoItemToServer> HANDLER = (packet, context) -> {

        Level level = context.player().level();
        ShopBlockEntity shop = (ShopBlockEntity) level.getBlockEntity(packet.pos);

        if (shop != null) {
            shop.setAutoProduced(packet.stack);
        }
        assert shop != null;
        shop.setChanged();
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAutoItemToServer> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncAutoItemToServer::pos,
            ItemStack.OPTIONAL_STREAM_CODEC, SyncAutoItemToServer::stack,
            SyncAutoItemToServer::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
