package com.benbenlaw.shops.item;

import com.benbenlaw.shops.entity.CrateEntity;
import com.benbenlaw.shops.entity.ShopsEntities;
import com.benbenlaw.shops.loaders.CrateData;
import com.benbenlaw.shops.loaders.CrateLoader;
import com.benbenlaw.shops.util.TickScheduler;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CrateFlareItem extends Item {

    public CrateFlareItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        BlockPos pos = context.getClickedPos();
        Vec3 exactPos = context.getClickLocation();
        Vec3 hitVec = exactPos.subtract(Vec3.atLowerCornerOf(pos));
        Direction direction = context.getClickedFace();
        Level level = context.getLevel();
        CrateEntity crate = new CrateEntity(ShopsEntities.CRATE.get(), level);
        ResourceLocation crateType = context.getItemInHand().get(ShopsDataComponents.CRATE_ID);
        CrateData data = CrateLoader.getCrate(crateType);


        if (direction == Direction.UP) {

                //Launch "Flare"
                createFirework(level, DyeColor.GRAY.getTextureDiffuseColor(), exactPos);

                if (!context.getPlayer().isCreative()) {
                    context.getItemInHand().shrink(1);
                };

                //Schedule Crate
                TickScheduler.schedule(level, 60 , () -> {
                    Vec3 spawnPos = exactPos.add(0, 50, 0);
                    Vec3 playerPos = context.getPlayer().position().add(0, context.getPlayer().getEyeHeight(), 0);
                    Vec3 lookVec = playerPos.subtract(spawnPos);

                    float yaw = (float) (Math.atan2(lookVec.z, lookVec.x) * (180 / Math.PI)) - 90f;

                    // Set absolute position and rotation
                    crate.absMoveTo(spawnPos.x, spawnPos.y, spawnPos.z, yaw, 0.0f);

                    assert crateType != null;
                    crate.setCrateType(crateType);

                    level.addFreshEntity(crate);
                    level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH, crate.getSoundSource(), 1.0f, 1.0f);
                });
            }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {

        ResourceLocation crateType = stack.get(ShopsDataComponents.CRATE_ID);

        assert crateType != null;

        String path = crateType.getPath();
        components.add(Component.translatable("item.shops.crate_flare." + path));
    }

    public void createFirework(Level level, int color, Vec3 position) {

        FireworkExplosion explosion = new FireworkExplosion(
                FireworkExplosion.Shape.LARGE_BALL, IntList.of(color), IntList.of(color),true, true);

        Fireworks fireworks = new Fireworks(3, List.of(explosion));
        ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
        rocket.set(DataComponents.FIREWORKS, fireworks);

        FireworkRocketEntity entity = new FireworkRocketEntity(level, position.x, position.y, position.z, rocket);
        level.addFreshEntity(entity);
    }

    public static ItemStack createCrateFlare(ResourceLocation path) {
        ItemStack stack = new ItemStack(ShopsItems.CRATE_FLARE.get());
        stack.set(ShopsDataComponents.CRATE_ID, path);
        return stack;
    }
}
