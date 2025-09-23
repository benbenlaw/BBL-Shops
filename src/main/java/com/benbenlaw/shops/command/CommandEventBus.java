package com.benbenlaw.shops.command;

import com.benbenlaw.shops.Shops;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber (modid = Shops.MOD_ID)
public class CommandEventBus {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        BalanceCommand.register(event.getDispatcher());
    }
}
