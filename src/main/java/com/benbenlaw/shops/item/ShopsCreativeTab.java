package com.benbenlaw.shops.item;

import com.benbenlaw.shops.Shops;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ShopsCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Shops.MOD_ID);

    public static final Supplier<CreativeModeTab> SHOPS_TAB = CREATIVE_MODE_TABS.register(Shops.MOD_ID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ShopsItems.COPPER_COIN.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup." + Shops.MOD_ID))
            .displayItems(ShopsItems.ITEMS.getEntries()).build());
}
