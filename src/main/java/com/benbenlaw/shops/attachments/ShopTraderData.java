package com.benbenlaw.shops.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ShopTraderData(Optional<BlockPos> jobSite) {

    public static final ShopTraderData EMPTY = new ShopTraderData(Optional.empty());

    public static final Codec<ShopTraderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("job_site").forGetter(ShopTraderData::jobSite)
    ).apply(instance, ShopTraderData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopTraderData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), ShopTraderData::jobSite,
            ShopTraderData::new
    );

    public ShopTraderData withJobSite(BlockPos pos) {
        return new ShopTraderData(Optional.of(pos));
    }
}