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

public record BuyShopItem(Identifier entryId) implements CustomPacketPayload {

    public static final Type<BuyShopItem> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Shops.MOD_ID, "buy_shop_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BuyShopItem> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BuyShopItem::entryId,
            BuyShopItem::new
    );

    public static final IPayloadHandler<BuyShopItem> HANDLER = (packet, context) -> {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) return;

        Optional<RecipeHolder<?>> maybeHolder = Optional.ofNullable(serverPlayer.level().recipeAccess().recipeMap()
                .byKey(ResourceKey.create(Registries.RECIPE, packet.entryId())));

        if (maybeHolder.isEmpty() || !(maybeHolder.get().value() instanceof ShopEntryRecipe entry)) return;

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