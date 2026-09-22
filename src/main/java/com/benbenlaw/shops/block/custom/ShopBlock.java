package com.benbenlaw.shops.block.custom;

import com.benbenlaw.core.block.SyncableBlock;
import com.benbenlaw.shops.block.ShopsBlockEntities;
import com.benbenlaw.shops.block.entity.ShopBlockEntity;
import com.benbenlaw.shops.network.packets.OpenShopBlockScreen;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShopBlock extends SyncableBlock {

    public static final MapCodec<ShopBlock> CODEC = simpleCodec(ShopBlock::new);

    public @NotNull MapCodec<ShopBlock> codec() {
        return CODEC;
    }

    public ShopBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof ShopBlockEntity entity)) {
            return InteractionResult.SUCCESS;
        }

        if (!player.isCrouching()) {
            if (!level.isClientSide()) {
                player.openMenu(new SimpleMenuProvider(entity, entity.getDisplayName()), pos);
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            entity.refreshNearbyTraders(level, pos);
            PacketDistributor.sendToPlayer(serverPlayer, new OpenShopBlockScreen(List.copyOf(entity.nearbyTraders())));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ShopBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ShopsBlockEntities.SHOP_BLOCK_ENTITY.get(),
                (thisLevel, thisPos, thisState, thisEntity) -> thisEntity.tick());
    }
}
