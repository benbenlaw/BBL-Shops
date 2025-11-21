package com.benbenlaw.shops.events;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.capability.PlayerBalanceData;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.loaders.CombinedShopLoader;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import com.benbenlaw.shops.util.TickScheduler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Shops.MOD_ID)
public class TickEvent {

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!level.isClientSide()) {
            TickScheduler.tick(level);
        }
    }
}
