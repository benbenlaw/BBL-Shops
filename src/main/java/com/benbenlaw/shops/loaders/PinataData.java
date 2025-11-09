package com.benbenlaw.shops.loaders;

import com.benbenlaw.shops.util.ChanceResult;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record PinataData(List<ChanceResult> rewards) {

    public static final Codec<PinataData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ChanceResult.CODEC.listOf().fieldOf("rewards").forGetter(PinataData::rewards)
    ).apply(inst, PinataData::new));

}
