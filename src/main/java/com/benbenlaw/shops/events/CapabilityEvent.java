package com.benbenlaw.shops.events;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.capability.PlayerBalanceData;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.loaders.CrateData;
import com.benbenlaw.shops.loaders.CrateLoader;
import com.benbenlaw.shops.loaders.PinataLoader;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.loaders.CombinedShopLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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

        if (event.getPlayer() == null) {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                CombinedShopLoader.sendShopsPacketToPlayer(player);
                CrateLoader.sendLoaderInformation(player);
                PinataLoader.sendLoaderInformation(player);
            }
        } else {
            CombinedShopLoader.sendShopsPacketToPlayer(event.getPlayer());
            CrateLoader.sendLoaderInformation(event.getPlayer());
            PinataLoader.sendLoaderInformation(event.getPlayer());
        }
    }

}
