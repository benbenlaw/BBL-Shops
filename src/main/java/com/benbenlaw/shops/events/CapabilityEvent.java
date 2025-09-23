package com.benbenlaw.shops.events;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.capability.PlayerBalanceData;
import com.benbenlaw.shops.capability.ShopsAttachments;
import com.benbenlaw.shops.item.ShopsDataComponents;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.network.packets.SyncPlayerBalanceToClient;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = Shops.MOD_ID)
public class CapabilityEvent {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide()) return;

        PlayerBalanceData data = player.getData(ShopsAttachments.PLAYER_BALANCE);

        PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPlayerBalanceToClient(data.getBalance()));
    }

}
