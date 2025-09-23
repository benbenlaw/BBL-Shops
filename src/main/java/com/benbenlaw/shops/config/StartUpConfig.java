package com.benbenlaw.shops.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class StartUpConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> copperCoinValue;
    public static final ModConfigSpec.ConfigValue<Integer> ironCoinValue;
    public static final ModConfigSpec.ConfigValue<Integer> goldCoinValue;

    static {

        //Coin Values
        BUILDER.comment("BBL Shops Configs")
                .push("Coin Values");

        copperCoinValue = BUILDER.comment("Value of a Copper Coin in base currency, default 1")
                .defineInRange("copperCoinValue", 1, 1, Integer.MAX_VALUE);

        ironCoinValue = BUILDER.comment("Value of an Iron Coin in base currency, default 10")
                .defineInRange("ironCoinValue", 5, 1, Integer.MAX_VALUE);

        goldCoinValue = BUILDER.comment("Value of a Gold Coin in base currency, default 25")
                .defineInRange("goldCoinValue", 10, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        //Last
        SPEC = BUILDER.build();

    }
}
