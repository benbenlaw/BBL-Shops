package com.benbenlaw.shops.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ShopTraderData(Optional<BlockPos> jobSite, Optional<String> traderName) {

    public static final ShopTraderData EMPTY = new ShopTraderData(Optional.empty(), Optional.empty());

    public static final Codec<ShopTraderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("job_site").forGetter(ShopTraderData::jobSite),
            Codec.STRING.optionalFieldOf("trader_name").forGetter(ShopTraderData::traderName)
    ).apply(instance, ShopTraderData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopTraderData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), ShopTraderData::jobSite,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), ShopTraderData::traderName,
            ShopTraderData::new
    );
}