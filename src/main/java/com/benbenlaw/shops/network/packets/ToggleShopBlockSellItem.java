package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.block.entity.ShopBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record ToggleShopBlockSellItem(BlockPos pos, Identifier recipeId) implements CustomPacketPayload {

    public static final Type<ToggleShopBlockSellItem> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Shops.MOD_ID, "toggle_shop_block_sell_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleShopBlockSellItem> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ToggleShopBlockSellItem::pos,
            Identifier.STREAM_CODEC, ToggleShopBlockSellItem::recipeId,
            ToggleShopBlockSellItem::new
    );

    public static final IPayloadHandler<ToggleShopBlockSellItem> HANDLER = (packet, context) -> {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
        if (serverPlayer.level().getBlockEntity(packet.pos()) instanceof ShopBlockEntity entity) {
            entity.toggleSellWhitelist(packet.recipeId(), serverPlayer);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
