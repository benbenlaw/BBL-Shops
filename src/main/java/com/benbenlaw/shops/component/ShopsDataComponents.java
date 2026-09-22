package com.benbenlaw.shops.component;

import com.benbenlaw.shops.Shops;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

public class ShopsDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Shops.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BoundPlayer>> BOUND_PLAYER =
            DATA_COMPONENTS.registerComponentType("bound_player", builder -> builder
                    .persistent(BoundPlayer.CODEC)
                    .networkSynchronized(BoundPlayer.STREAM_CODEC));

    public record BoundPlayer(UUID uuid, String name) {

        public static final Codec<BoundPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(BoundPlayer::uuid),
                Codec.STRING.fieldOf("name").forGetter(BoundPlayer::name)
        ).apply(instance, BoundPlayer::new));

        public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, BoundPlayer> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, BoundPlayer::uuid,
                ByteBufCodecs.STRING_UTF8, BoundPlayer::name,
                BoundPlayer::new
        );
    }
}
