package com.benbenlaw.shops.item;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.loaders.CrateLoader;
import com.benbenlaw.shops.loaders.PinataLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ShopsCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Shops.MOD_ID);

    public static final Supplier<CreativeModeTab> SHOPS_TAB = CREATIVE_MODE_TABS.register(Shops.MOD_ID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ShopsItems.COPPER_COIN.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup." + Shops.MOD_ID))
            .displayItems((parameters, output) -> {

                ShopsItems.ITEMS.getEntries().forEach(entry -> {
                    if (entry.get() != ShopsItems.PINATA_FLARE.get() && entry.get() != ShopsItems.CRATE_FLARE.get()) {
                        output.accept(entry.get());
                    }
                });

                PinataLoader.getAllPinatas().keySet().forEach(pinataId -> {
                    ItemStack stack = PinataFlareItem.createPinataFlare(pinataId);
                    output.accept(stack);
                });

                CrateLoader.getAllCrates().keySet().forEach(crateId -> {
                    ItemStack stack = CrateFlareItem.createCrateFlare(crateId);
                    output.accept(stack);
                });

            })
            .build()
    );
}
