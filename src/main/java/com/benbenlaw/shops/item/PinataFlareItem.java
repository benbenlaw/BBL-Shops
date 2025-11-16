package com.benbenlaw.shops.item;

import com.benbenlaw.shops.entity.PinataEntity;
import com.benbenlaw.shops.entity.ShopsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class PinataFlareItem extends Item {

    public PinataFlareItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        BlockPos pos = context.getClickedPos();
        Vec3 exactPos = context.getClickLocation();
        Vec3 hitVec = exactPos.subtract(Vec3.atLowerCornerOf(pos));
        Direction direction = context.getClickedFace();
        Level level = context.getLevel();
        PinataEntity pinata = new PinataEntity(ShopsEntities.PINATA.get(), level);
        ResourceLocation pinataType = context.getItemInHand().get(ShopsDataComponents.PINATA_ID);


        if (direction == Direction.UP) {
            Vec3 spawnPos = exactPos.add(0, 50, 0);
            Vec3 playerPos = context.getPlayer().position().add(0, context.getPlayer().getEyeHeight(), 0);
            Vec3 lookVec = playerPos.subtract(spawnPos);

            float yaw = (float) (Math.atan2(lookVec.z, lookVec.x) * (180 / Math.PI)) - 90f;

            // Set absolute position and rotation
            pinata.absMoveTo(spawnPos.x, spawnPos.y, spawnPos.z, yaw, 0.0f);

            // Also set body/head rotation to make horse model face player
            pinata.yBodyRot = yaw;
            pinata.yHeadRot = yaw;
            pinata.yBodyRot = yaw;

            assert pinataType != null;
            pinata.setPinataType(pinataType);

            level.addFreshEntity(pinata);
            level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH, pinata.getSoundSource(), 1.0f, 1.0f);

            if (!context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }
        }



        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {

        ResourceLocation pinataType = stack.get(ShopsDataComponents.PINATA_ID);

        assert pinataType != null;
        components.add(Component.literal("Pinata Type: " + pinataType));
    }

    public static ItemStack createPinataFlare(ResourceLocation path) {
        ItemStack stack = new ItemStack(ShopsItems.PINATA_FLARE.get());
        stack.set(ShopsDataComponents.PINATA_ID, path);
        return stack;
    }
}
