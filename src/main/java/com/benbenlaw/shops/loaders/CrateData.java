package com.benbenlaw.shops.loaders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record CrateData(ResourceLocation lootTable) {

    public static int parseColor(String colorStr) {
        if (colorStr.startsWith("#")) {
            return (int) Long.parseLong(colorStr.substring(1), 16);
        }
        return Integer.parseInt(colorStr);
    }

    public static final Codec<Integer> COLOR_CODEC = Codec.STRING.xmap(CrateData::parseColor,
            color -> String.format("#%08X", color));

    public static final Codec<CrateData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("loot_table").forGetter(CrateData::lootTable)
    ).apply(inst, CrateData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrateData> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, CrateData::lootTable,
            CrateData::new
    );
}

