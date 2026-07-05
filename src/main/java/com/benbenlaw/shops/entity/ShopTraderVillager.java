package com.benbenlaw.shops.entity;

import com.benbenlaw.shops.attachments.ShopTraderData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.item.CoinItem;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.network.packets.OpenShopTraderScreen;
import com.benbenlaw.shops.util.ShopsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class ShopTraderVillager extends Villager {

    private static final int JOB_SITE_SEARCH_RADIUS = 8;
    private static final double TETHER_RADIUS = 6.0D;
    private static final double TETHER_RADIUS_SQR = TETHER_RADIUS * TETHER_RADIUS;
    private static final int TETHER_CHECK_INTERVAL = 40; // 2 seconds

    private static final double FOLLOW_RADIUS = 8.0D;
    private static final double FOLLOW_STOP_DISTANCE = 2.0D;
    private static final double FOLLOW_STOP_DISTANCE_SQR = FOLLOW_STOP_DISTANCE * FOLLOW_STOP_DISTANCE;
    private static final double MAX_FOLLOW_DISTANCE_FROM_JOB_SITE = 8.0D;
    private static final double MAX_FOLLOW_DISTANCE_FROM_JOB_SITE_SQR = MAX_FOLLOW_DISTANCE_FROM_JOB_SITE * MAX_FOLLOW_DISTANCE_FROM_JOB_SITE;
    private static final int FOLLOW_RECOMPUTE_INTERVAL = 10;
    private static final double FOLLOW_SPEED = 0.5D;

    public ShopTraderVillager(EntityType<? extends Villager> type, Level level) {
        super(type, level, VillagerType.PLAINS);
        // Held item is just a visual marker for the bound block, not a real drop.
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);

        getOrFindJobSite().ifPresent(pos -> {
            syncHeldItem(pos);

            Player coinHolder = findNearbyCoinHolder(pos);
            if (coinHolder != null) {
                followPlayer(coinHolder);
            } else if (this.tickCount % TETHER_CHECK_INTERVAL == 0) {
                tetherToJobSite(pos);
            }
        });
    }

    private Player findNearbyCoinHolder(BlockPos jobSite) {
        Player nearest = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), FOLLOW_RADIUS, false);
        if (nearest == null || !isHoldingCoin(nearest)) {
            return null;
        }
        if (jobSite.distSqr(nearest.blockPosition()) > MAX_FOLLOW_DISTANCE_FROM_JOB_SITE_SQR) {
            return null;
        }
        return nearest;
    }

    private boolean isHoldingCoin(Player player) {
        return player.getMainHandItem().getItem() instanceof CoinItem || player.getOffhandItem().getItem() instanceof CoinItem;
    }

    private void followPlayer(Player player) {
        if (this.distanceToSqr(player) <= FOLLOW_STOP_DISTANCE_SQR) {
            this.getNavigation().stop();
            return;
        }

        this.getNavigation().moveTo(player, FOLLOW_SPEED);
    }

    private void tetherToJobSite(BlockPos jobSite) {
        if (this.distanceToSqr(jobSite.getX() + 0.5, jobSite.getY(), jobSite.getZ() + 0.5) > TETHER_RADIUS_SQR
                && this.getNavigation().isDone()) {
            this.getNavigation().moveTo(jobSite.getX() + 0.5, jobSite.getY(), jobSite.getZ() + 0.5, 0.5D);
        }
    }

    private void syncHeldItem(BlockPos jobSite) {
        Block block = this.level().getBlockState(jobSite).getBlock();
        ItemStack current = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (current.isEmpty() || !current.is(block.asItem())) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(block));
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        Optional<BlockPos> jobSite = getOrFindJobSite();
        if (jobSite.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("This trader hasn't got a shop set up yet."));
            return InteractionResult.SUCCESS;
        }

        Identifier traderId = BuiltInRegistries.BLOCK.getKey(this.level().getBlockState(jobSite.get()).getBlock());
        PacketDistributor.sendToPlayer(serverPlayer, new OpenShopTraderScreen(traderId));
        return InteractionResult.SUCCESS;
    }

    private Optional<BlockPos> getOrFindJobSite() {
        ShopTraderData data = this.getData(ShopsAttachments.SHOP_TRADER_DATA);
        if (data.jobSite().isPresent()) {
            BlockPos bound = data.jobSite().get();
            if (this.level().getBlockState(bound).is(ShopsTags.Blocks.SHOP_TRADER_BLOCKS)) {
                return Optional.of(bound);
            }
            this.setData(ShopsAttachments.SHOP_TRADER_DATA, new ShopTraderData(Optional.empty()));
        }

        BlockPos origin = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-JOB_SITE_SEARCH_RADIUS, -JOB_SITE_SEARCH_RADIUS, -JOB_SITE_SEARCH_RADIUS),
                origin.offset(JOB_SITE_SEARCH_RADIUS, JOB_SITE_SEARCH_RADIUS, JOB_SITE_SEARCH_RADIUS))) {
            if (this.level().getBlockState(pos).is(ShopsTags.Blocks.SHOP_TRADER_BLOCKS)) {
                BlockPos bound = pos.immutable();
                this.setData(ShopsAttachments.SHOP_TRADER_DATA, new ShopTraderData(Optional.of(bound)));
                return Optional.of(bound);
            }
        }

        return Optional.empty();
    }
}