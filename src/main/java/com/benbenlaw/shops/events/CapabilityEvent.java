package com.benbenlaw.shops.events;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.capability.PlayerBalanceData;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.item.ShopsDataComponents;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.network.packets.SyncShopRegistryToClient;
import com.benbenlaw.shops.shop.CombinedShopLoader;
import com.benbenlaw.shops.shop.ShopEntry;
import com.benbenlaw.shops.shop.ShopRegistry;
import com.mojang.authlib.GameProfile;
import mezz.jei.api.JeiPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = Shops.MOD_ID)
public class CapabilityEvent {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide()) {
            PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(data.getBalance()));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();

            int count = oldPlayer.getData(ShopsAttachments.PLAYER_BALANCE).getBalance();
            newPlayer.getData(ShopsAttachments.PLAYER_BALANCE).setBalance(count);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide()) {
            PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(data.getBalance()));
        }
    }

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {

        GameProfile profile = event.getPlayerList().getServer().getSingleplayerProfile();
        assert event.getPlayer() != null;
        if (event.getPlayer().getGameProfile() != profile) {
            CombinedShopLoader.sendShopsPacketToPlayer(event.getPlayer());
        }
    }

}
