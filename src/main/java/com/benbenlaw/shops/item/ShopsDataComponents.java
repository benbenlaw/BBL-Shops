package com.benbenlaw.shops.item;

import com.benbenlaw.shops.Shops;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

public class ShopsDataComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Shops.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> PLAYER_UUID =
            COMPONENTS.register("player_uuid", () ->
                    DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> PLAYER_USERNAME =
            COMPONENTS.register("player_username", () ->
                    DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> PINATA_ID =
            COMPONENTS.register("pinata_id", () ->
                    DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> CRATE_ID =
            COMPONENTS.register("crate_id", () ->
                    DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());

}
