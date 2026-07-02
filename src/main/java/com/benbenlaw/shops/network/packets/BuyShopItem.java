package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.attachments.PlayerBalanceData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.screen.ShopScreen;
import com.benbenlaw.shops.screen.TestShopData;
import com.benbenlaw.shops.sound.ShopsSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.Optional;

public record BuyShopItem(Identifier itemId) implements CustomPacketPayload {

    public static final Type<BuyShopItem> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Shops.MOD_ID, "buy_shop_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BuyShopItem> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BuyShopItem::itemId,
            BuyShopItem::new
    );

    public static final IPayloadHandler<BuyShopItem> HANDLER = (packet, context) -> {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) return;

        Optional<ShopScreen.ShopEntry> maybeEntry = TestShopData.get().stream()
                .filter(entry -> BuiltInRegistries.ITEM.getKey(entry.stack().getItem()).equals(packet.itemId()))
                .findFirst();

        if (maybeEntry.isEmpty()) return; // unknown/unlisted item id, ignore silently

        ShopScreen.ShopEntry entry = maybeEntry.get();
        PlayerBalanceData data = serverPlayer.getData(ShopsAttachments.PLAYER_BALANCE.get());

        if (!entry.tier().isEmpty() && !data.hasStage(entry.tier())) {
            serverPlayer.sendSystemMessage(Component.literal("You haven't unlocked this item yet."));
            return;
        }

        if (data.getBalance() < entry.buyPrice()) {
            serverPlayer.sendSystemMessage(Component.literal("You don't have enough balance."));
            return;
        }

        PlayerBalanceData updated = data.subtractBalance(entry.buyPrice());
        serverPlayer.setData(ShopsAttachments.PLAYER_BALANCE.get(), updated);

        serverPlayer.connection.send(new ClientboundSoundPacket(
                Holder.direct(SoundEvents.ITEM_PICKUP),
                SoundSource.PLAYERS,
                serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                1.0f, 1.0f,
                serverPlayer.level().getRandom().nextLong()
        ));

        serverPlayer.connection.send(new ClientboundSoundPacket(
                Holder.direct(ShopsSounds.COIN_COLLECTED.get()),
                SoundSource.PLAYERS,
                serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                1.0f, 1.0f,
                serverPlayer.level().getRandom().nextLong()
        ));

        ItemStack stack = entry.stack().copy();
        if (!serverPlayer.getInventory().add(stack)) {
            serverPlayer.drop(stack, false);
        }

        PacketDistributor.sendToPlayer(serverPlayer, new SyncPlayerBalanceToClient(updated.getBalance()));
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}