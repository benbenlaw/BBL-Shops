package com.benbenlaw.shops.entity;

import com.benbenlaw.shops.attachments.ShopTraderData;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.datamaps.ShopsDataMaps;
import com.benbenlaw.shops.entity.goal.FollowCoinHolderGoal;
import com.benbenlaw.shops.entity.goal.ReturnToJobSiteGoal;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public class ShopTraderMob extends PathfinderMob {

    private static final int JOB_SITE_SEARCH_RADIUS = 8;
    private static final double TETHER_RADIUS = 6.0D;
    private static final double MAX_FOLLOW_DISTANCE_FROM_JOB_SITE = 8.0D;
    private static final double FOLLOW_RADIUS = 8.0D;
    private static final double FOLLOW_STOP_DISTANCE = 2.0D;
    private static final double FOLLOW_SPEED = 0.5D;
    private static final double WANDER_SPEED = 0.4D;

    public ShopTraderMob(EntityType<? extends ShopTraderMob> type, Level level) {
        super(type, level);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setPersistenceRequired();
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override
    public void checkDespawn() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FollowCoinHolderGoal(this));
        this.goalSelector.addGoal(2, new ReturnToJobSiteGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, WANDER_SPEED));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        getOrFindJobSite().ifPresent(this::syncHeldItem);
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

        if (player.isCrouching()) return InteractionResult.PASS;

        Optional<BlockPos> jobSite = getOrFindJobSite();
        if (jobSite.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("This trader hasn't got a shop set up yet."));
            return InteractionResult.SUCCESS;
        }

        Identifier traderId = BuiltInRegistries.BLOCK.getKey(this.level().getBlockState(jobSite.get()).getBlock());
        String traderName = this.getData(ShopsAttachments.SHOP_TRADER_DATA).traderName().orElse("");
        PacketDistributor.sendToPlayer(serverPlayer, new OpenShopTraderScreen(traderId, traderName));
        return InteractionResult.SUCCESS;
    }

    public Optional<BlockPos> getOrFindJobSite() {
        ShopTraderData data = this.getData(ShopsAttachments.SHOP_TRADER_DATA);
        if (data.jobSite().isPresent()) {
            BlockPos bound = data.jobSite().get();
            boolean blockStillValid = this.level().getBlockState(bound).is(ShopsTags.Blocks.SHOP_TRADER_BLOCKS);
            boolean claimedByAnother = ShopJobSiteClaims.isClaimedByAnother(this.level(), bound, this.getUUID());

            if (blockStillValid && !claimedByAnother) {
                ShopJobSiteClaims.claim(this.level(), bound, this.getUUID());
                return Optional.of(bound);
            }

            ShopJobSiteClaims.release(this.level(), bound, this.getUUID());
            this.setData(ShopsAttachments.SHOP_TRADER_DATA, ShopTraderData.EMPTY);
            this.setCustomName(null);
            this.setCustomNameVisible(false);
        }

        BlockPos origin = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-JOB_SITE_SEARCH_RADIUS, -JOB_SITE_SEARCH_RADIUS, -JOB_SITE_SEARCH_RADIUS),
                origin.offset(JOB_SITE_SEARCH_RADIUS, JOB_SITE_SEARCH_RADIUS, JOB_SITE_SEARCH_RADIUS))) {
            if (this.level().getBlockState(pos).is(ShopsTags.Blocks.SHOP_TRADER_BLOCKS)
                    && !ShopJobSiteClaims.isClaimedByAnother(this.level(), pos, this.getUUID())) {

                BlockPos bound = pos.immutable();
                Block block = this.level().getBlockState(bound).getBlock();
                String name = pickTraderName(block);

                ShopJobSiteClaims.claim(this.level(), bound, this.getUUID());
                this.setData(ShopsAttachments.SHOP_TRADER_DATA, new ShopTraderData(Optional.of(bound), Optional.ofNullable(name)));

                if (name != null) {
                    this.setCustomName(Component.literal(name));
                    this.setCustomNameVisible(true);
                }

                return Optional.of(bound);
            }
        }

        return Optional.empty();
    }

    @Override
    public void remove(RemovalReason reason) {
        ShopTraderData data = this.getData(ShopsAttachments.SHOP_TRADER_DATA);
        data.jobSite().ifPresent(pos -> ShopJobSiteClaims.release(this.level(), pos, this.getUUID()));
        super.remove(reason);
    }

    private String pickTraderName(Block block) {
        List<String> names = block.builtInRegistryHolder().getData(ShopsDataMaps.TRADER_NAMES);
        if (names == null || names.isEmpty()) {
            return null;
        }
        return names.get(this.random.nextInt(names.size()));
    }

    public boolean isHoldingCoin(Player player) {
        return player.getMainHandItem().getItem() instanceof CoinItem || player.getOffhandItem().getItem() instanceof CoinItem;
    }

    public double followRadius() {
        return FOLLOW_RADIUS;
    }

    public double followStopDistance() {
        return FOLLOW_STOP_DISTANCE;
    }

    public double followSpeed() {
        return FOLLOW_SPEED;
    }

    public double maxFollowDistanceFromJobSite() {
        return MAX_FOLLOW_DISTANCE_FROM_JOB_SITE;
    }

    public double tetherRadius() {
        return TETHER_RADIUS;
    }
}