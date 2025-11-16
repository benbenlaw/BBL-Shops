package com.benbenlaw.shops.entity;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.loaders.PinataData;
import com.benbenlaw.shops.loaders.PinataLoader;
import com.benbenlaw.shops.util.ChanceResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PinataEntity extends AbstractHorse {

    private static final EntityDataAccessor<String> DATA_PINATA_TYPE =
            SynchedEntityData.defineId(PinataEntity.class, EntityDataSerializers.STRING);

    private ResourceLocation pinataType;
    private int hitCount = 0;

    public PinataEntity(EntityType<? extends PinataEntity> type, Level level) {
        super(type, level);
        this.noPhysics = false;
        this.setHealth(10f);
    }

    @Override
    public void tick() {
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
        if (level.isClientSide) return false;

        if (source.getEntity() instanceof Player player) {

            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(Tags.Items.RODS)) {

                hitCount++;

                int requiredHits = this.getRequiredHits();
                ServerLevel serverLevel = (ServerLevel) level;

                if (hitCount >= requiredHits) {
                    dropLootTable(serverLevel);
                    this.playSound(SoundEvents.FIREWORK_ROCKET_BLAST);
                    explodeFirework();
                    this.discard();
                } else {
                    this.playSound(Objects.requireNonNull(this.getHurtSound(source)), 1.0f, 1.0f);
                }

                serverLevel.sendParticles(ParticleTypes.FIREWORK, this.getX(), this.getY() + 1.0, this.getZ(),
                        25, 0.2, 0.2, 0.2, 0.1);

                return true;
            }
            player.sendSystemMessage(Component.translatable("entity.shops.pinata_hit").withStyle(ChatFormatting.ITALIC));
        }

        return false;
    }

    // Store a random hit threshold for this pinata
    private int requiredHits = -1;

    private int getRequiredHits() {
        if (requiredHits == -1) {
            requiredHits = 3 + this.random.nextInt(5); // 3 to 7 inclusive
        }
        return requiredHits;
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
            this.entityData.set(DATA_PINATA_TYPE, pinataType.toString());
        }
    }

    @Override
    protected boolean canRide(Entity p_20339_) {
        return false;
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
        this.pinataType = type;
        this.entityData.set(DATA_PINATA_TYPE, type.toString());
    }

    public ResourceLocation getPinataType() {
        String typeStr = this.entityData.get(DATA_PINATA_TYPE);
        if (!typeStr.isEmpty()) {
            return ResourceLocation.tryParse(typeStr);
        }
        return pinataType; // fallback
    }


    private void dropLootTable(ServerLevel level) {

        PinataData data = PinataLoader.getPinata(pinataType);

        if (data != null) {
            ResourceLocation lootTable = data.lootTable();
            LootTable table = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTable));

            LootParams lootParams = (new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, this.position())
                    .withParameter(LootContextParams.THIS_ENTITY, this)
                    .create(LootContextParamSets.GIFT));

            List<ItemStack> loot = table.getRandomItems(lootParams);
            for (ItemStack stack : loot) {
                this.spawnAtLocation(stack);
            }

        }
    }

    public ResourceLocation getTexture() {
        PinataData data = PinataLoader.getPinata(getPinataType());
        if (data != null) {
            ResourceLocation model = data.model();
            // convert model to texture path
            return ResourceLocation.fromNamespaceAndPath(model.getNamespace(), "textures/entity/pinata/" + model.getPath() + ".png");
        }
        return ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "textures/entity/pinata/default.png");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PINATA_TYPE, "");
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {

        if (!level().isClientSide() && hand == InteractionHand.MAIN_HAND) {

            player.sendSystemMessage(Component.translatable("entity.shops.pinata_interact").withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

}