package com.benbenlaw.shops.screen;

import com.benbenlaw.shops.client.ClientShopRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientScreens {

    public static void openShopScreen() {

        //TEST
        //Minecraft.getInstance().setScreen(new ShopScreen(Component.literal("Shop"), TestShopData.get()));

        //Actual
        List<ShopScreen.ShopEntry> entries = ClientShopRegistry.all().stream()
                .map(clientEntry -> new ShopScreen.ShopEntry(
                        clientEntry.namespace(),
                        new ItemStack(clientEntry.item()),
                        clientEntry.buyPrice(),
                        clientEntry.sellPrice(),
                        clientEntry.tier()
                ))
                .toList();

        Minecraft.getInstance().setScreen(new ShopScreen(Component.translatable("menu.shops.shop"), entries));

    }

}