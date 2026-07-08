package com.benbenlaw.shops.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class ShopJobSiteClaims {

    private static final Map<ResourceKey<Level>, Map<BlockPos, UUID>> CLAIMS = new HashMap<>();

    private ShopJobSiteClaims() {}

    public static boolean isClaimedByAnother(Level level, BlockPos pos, UUID selfId) {
        Map<BlockPos, UUID> levelClaims = CLAIMS.get(level.dimension());
        if (levelClaims == null) return false;
        UUID owner = levelClaims.get(pos);
        return owner != null && !owner.equals(selfId);
    }

    public static void claim(Level level, BlockPos pos, UUID ownerId) {
        CLAIMS.computeIfAbsent(level.dimension(), k -> new HashMap<>()).put(pos, ownerId);
    }

    public static void release(Level level, BlockPos pos, UUID ownerId) {
        Map<BlockPos, UUID> levelClaims = CLAIMS.get(level.dimension());
        if (levelClaims != null) {
            levelClaims.remove(pos, ownerId);
        }
    }
}
