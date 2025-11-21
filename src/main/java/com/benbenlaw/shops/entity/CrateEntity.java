package com.benbenlaw.shops.entity;

import com.benbenlaw.shops.loaders.CrateData;
import com.benbenlaw.shops.loaders.CrateLoader;
import com.benbenlaw.shops.loaders.PinataData;
import com.benbenlaw.shops.loaders.PinataLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class CrateEntity extends Entity {

    private static final EntityDataAccessor<String> DATA_CRATE_TYPE =
            SynchedEntityData.defineId(CrateEntity.class, EntityDataSerializers.STRING);

    private ResourceLocation crateType;
    int groundTimer = 0;

    public CrateEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.onGround()) {
            groundTimer++;

            if (groundTimer >= 15) {
                this.breakCrate();
                level().playSound(null, this.blockPosition(), SoundEvents.DRAGON_FIREBALL_EXPLODE, this.getSoundSource(), 0.5f, 1.0f);
            }

        }
    }

    private void breakCrate() {
        if (!this.level().isClientSide()) {
            //this.spawnAtLocation(new ItemStack(Items.OAK_PLANKS, 3));
            dropLootTable((ServerLevel) this.level());
            this.discard();
        }
    }

    public void setCrateType(ResourceLocation type) {
        this.crateType = type;
        this.entityData.set(DATA_CRATE_TYPE, type.toString());
    }

    private void dropLootTable(ServerLevel level) {

        CrateData data = CrateLoader.getCrate(crateType);

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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_CRATE_TYPE, "");
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {

        if (tag.contains("CrateType")) {
            crateType = ResourceLocation.parse(tag.getString("CrateType"));
            this.entityData.set(DATA_CRATE_TYPE, crateType.toString());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        if (crateType != null) {
            tag.putString("CrateType", crateType.toString());
        }
    }
}
