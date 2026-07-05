package com.benbenlaw.shops.datamaps;

import com.benbenlaw.shops.Shops;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.List;

public class ShopsDataMaps {

    public static final DataMapType<Block, List<String>> TRADER_NAMES = DataMapType.builder(
            Shops.identifier("trader_names"), Registries.BLOCK, Codec.STRING.listOf()).synced(Codec.STRING.listOf(), true).build();

}
