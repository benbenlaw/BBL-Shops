package com.benbenlaw.shops.loaders;

import com.benbenlaw.shops.util.ChanceResult;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record PinataData(int mainColor, ResourceLocation lootTable, ResourceLocation model) {

    public static int parseColor(String colorStr) {
        if (colorStr.startsWith("#")) {
            return (int) Long.parseLong(colorStr.substring(1), 16);
        }
        return Integer.parseInt(colorStr);
    }

    public static final Codec<Integer> COLOR_CODEC = Codec.STRING.xmap(PinataData::parseColor,
            color -> String.format("#%08X", color));

    public static final Codec<PinataData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            COLOR_CODEC.fieldOf("main_color").forGetter(PinataData::mainColor),
            ResourceLocation.CODEC.fieldOf("loot_table").forGetter(PinataData::lootTable),
            ResourceLocation.CODEC.fieldOf("model").forGetter(PinataData::model)
    ).apply(inst, PinataData::new));
    }

