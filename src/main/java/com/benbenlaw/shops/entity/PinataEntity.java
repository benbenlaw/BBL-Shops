package com.benbenlaw.shops.entity;

import com.benbenlaw.shops.loaders.PinataData;
import com.benbenlaw.shops.loaders.PinataLoader;
import com.benbenlaw.shops.util.ChanceResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PinataEntity extends AbstractHorse {

    private ResourceLocation pinataType;
    private int hitCount = 0;

    private static final EntityDataAccessor<String> PINATA_TYPE =
            SynchedEntityData.defineId(PinataEntity.class, EntityDataSerializers.STRING);

    public PinataEntity(EntityType<? extends PinataEntity> type, Level level) {
        super(type, level);
        this.noPhysics = false;
        this.setHealth(10f);
    }

    @Override
    public void tick() {



        //if (pinataType == null) {
        //    this.discard();
        //    return;
        //}

        super.tick();
        this.fallDistance = 0;
        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        this.xo = this.getX();
        this.yo = this.getY();
    }

    @Override
    public void registerGoals() {
        // No AI
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        Level level = this.level();
        if (level.isClientSide) {
            return false;
        }

        if (source.getEntity() instanceof Player player) {

            ServerLevel serverLevel = (ServerLevel) level;
            serverLevel.sendParticles(ParticleTypes.FIREWORK, this.getX(), this.getY() + 1.0, this.getZ(),
                    25, 0.2, 0.2, 0.2, 0.1);

            var rewards = PinataLoader.getRewardsForPinata(pinataType);

            if (hitCount < rewards.size()) {

                ChanceResult chanceResult = rewards.get(hitCount);

                if (this.random.nextFloat() <= chanceResult.chance()) {
                    this.spawnAtLocation(chanceResult.stack().copy());
                }

                hitCount++;

                if (hitCount >= rewards.size()) {
                    this.playSound(SoundEvents.FIREWORK_ROCKET_BLAST);
                    explodeFirework();
                    this.discard();
                }

                return true;
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void knockback(double strength, double x, double z) {
        // stop knockback
    }

    @Override
    public Pose getPose() {
        return Pose.STANDING;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FIREWORK_ROCKET_BLAST;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.FIREWORK_ROCKET_BLAST;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public boolean canEatGrass() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (pinataType != null) {
            tag.putString("PinataType", pinataType.toString());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("PinataType")) {
            pinataType = ResourceLocation.parse(tag.getString("PinataType"));
        }
    }


    private void explodeFirework() {
        int count = 1 + this.random.nextInt(5);
        IntArrayList colors = new IntArrayList();
        for (int i = 0; i < count; i++) {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            colors.add((r << 16) | (g << 8) | b);
        }

        int fadeCount = this.random.nextInt(4);
        IntArrayList fadeColors = new IntArrayList();
        for (int i = 0; i < fadeCount; i++) {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            fadeColors.add((r << 16) | (g << 8) | b);
        }

        FireworkExplosion.Shape shape = FireworkExplosion.Shape.values()[random.nextInt(FireworkExplosion.Shape.values().length)];
        FireworkExplosion explosion = new FireworkExplosion(shape, colors, fadeColors, true, true);
        Fireworks fireworks = new Fireworks(0, List.of(explosion));

        ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
        rocket.set(DataComponents.FIREWORKS, fireworks);

        FireworkRocketEntity entity = new FireworkRocketEntity(level(), getX(), getY() + 1, getZ(), rocket);
        level().addFreshEntity(entity);
    }

    public void setPinataType(ResourceLocation type) {
        this.entityData.set(PINATA_TYPE, type.toString());
        this.pinataType = type;
    }

    public ResourceLocation getPinataType() {
        if (pinataType == null) {
            pinataType = ResourceLocation.parse(this.entityData.get(PINATA_TYPE));
        }
        return pinataType;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PINATA_TYPE, "shops:test");
    }
}