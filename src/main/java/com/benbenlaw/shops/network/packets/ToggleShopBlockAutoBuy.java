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

public record ToggleShopBlockAutoBuy(BlockPos pos, Identifier recipeId) implements CustomPacketPayload {

    public static final Type<ToggleShopBlockAutoBuy> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Shops.MOD_ID, "toggle_shop_block_auto_buy"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleShopBlockAutoBuy> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ToggleShopBlockAutoBuy::pos,
            Identifier.STREAM_CODEC, ToggleShopBlockAutoBuy::recipeId,
            ToggleShopBlockAutoBuy::new
    );

    public static final IPayloadHandler<ToggleShopBlockAutoBuy> HANDLER = (packet, context) -> {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
        if (serverPlayer.level().getBlockEntity(packet.pos()) instanceof ShopBlockEntity entity) {
            entity.toggleAutoBuyTarget(packet.recipeId(), serverPlayer);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
