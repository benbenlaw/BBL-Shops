package com.benbenlaw.shops.network.packets;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.attachments.PlayerBalanceData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.recipe.ShopEntryRecipe;
import com.benbenlaw.shops.sound.ShopsSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.Optional;

public record BuyShopItem(Identifier entryId, int quantity) implements CustomPacketPayload {

    private static final int MAX_QUANTITY = 1000;

    public static final Type<BuyShopItem> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Shops.MOD_ID, "buy_shop_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BuyShopItem> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BuyShopItem::entryId,
            ByteBufCodecs.VAR_INT, BuyShopItem::quantity,
            BuyShopItem::new
    );

    public static final IPayloadHandler<BuyShopItem> HANDLER = (packet, context) -> {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) return;

        int requestedQuantity = Mth_clamp(packet.quantity(), 1, MAX_QUANTITY);

        Optional<RecipeHolder<?>> maybeHolder = Optional.ofNullable(serverPlayer.level().recipeAccess().recipeMap()
                .byKey(ResourceKey.create(Registries.RECIPE, packet.entryId())));

        if (maybeHolder.isEmpty() || !(maybeHolder.get().value() instanceof ShopEntryRecipe entry)) return;

        PlayerBalanceData data = serverPlayer.getData(ShopsAttachments.PLAYER_BALANCE.get());

        if (!entry.tier().isEmpty() && !data.hasStage(entry.tier())) {
            serverPlayer.sendSystemMessage(Component.translatable("message.shops.not_unlocked"));
            return;
        }

        if (entry.buyPrice() <= 0) return;

        int affordable = data.getBalance() / entry.buyPrice();
        int actualQuantity = Math.min(requestedQuantity, affordable);

        if (actualQuantity <= 0) {
            serverPlayer.sendSystemMessage(Component.translatable("message.shops.not_enough"));
            return;
        }

        int totalCost = entry.buyPrice() * actualQuantity;
        PlayerBalanceData updated = data.subtractBalance(totalCost);

        String unlockStage = entry.unlocksTierWhenBought();
        if (!unlockStage.isEmpty() && !updated.hasStage(unlockStage)) {
            updated = updated.addStage(unlockStage);
        }

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

        ItemStack stack = entry.stack().create().copy();
        stack.setCount(stack.getCount() * actualQuantity);

        if (!serverPlayer.getInventory().add(stack)) {
            serverPlayer.drop(stack, false);
        }

        PacketDistributor.sendToPlayer(serverPlayer, new SyncPlayerBalanceToClient(updated.getBalance()));
    };

    private static int Mth_clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}