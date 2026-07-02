package com.benbenlaw.shops.screen;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class TestShopData {

    public static List<ShopScreen.ShopEntry> get() {
        return List.of(
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.DIAMOND), 100, 60, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.EMERALD), 40, 20, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.GOLD_INGOT), 25, 12, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.IRON_INGOT), 10, 5, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.NETHERITE_INGOT), 500, 300, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.COAL), 2, 1, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.REDSTONE), 3, 1, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.LAPIS_LAZULI), 5, 2, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.QUARTZ), 4, 2, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.AMETHYST_SHARD), 8, 4, ""),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.ENDER_PEARL), 15, 8, "magic"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.BLAZE_ROD), 20, 10, "magic"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.GHAST_TEAR), 50, 25, "magic"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.NETHER_STAR), 1000, 600, "magic"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.TOTEM_OF_UNDYING), 250, 150, "magic"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.ELYTRA), 800, 400, "magic"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.DIAMOND_SWORD), 150, 75, "stone"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.DIAMOND_PICKAXE), 150, 75, "stone"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.SADDLE), 30, 15, "stone"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.NAME_TAG), 12, 6, "stone"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.EXPERIENCE_BOTTLE), 6, 3, "stone"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.SHULKER_SHELL), 60, 30, "stone"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.HEART_OF_THE_SEA), 200, 100, "stone"),
                new ShopScreen.ShopEntry("shops", new ItemStack(Items.DRAGON_EGG), 5000, 2500, "stone")
        );
    }
}