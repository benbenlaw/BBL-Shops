package com.benbenlaw.shops.item;

import com.benbenlaw.shops.entity.PinataEntity;
import com.benbenlaw.shops.entity.ShopsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class PinataFlareItem extends Item {

    public PinataFlareItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        Level level = context.getLevel();
        PinataEntity pinata = new PinataEntity(ShopsEntities.PINATA.get(), level);
        ResourceLocation pinataType = context.getItemInHand().get(ShopsDataComponents.PINATA_ID);


        if (direction == Direction.UP) {
            pinata.absMoveTo(pos.getCenter().x, pos.getY() + 50, pos.getCenter().z, 0.0f, 0.0f);
            pinata.setPinataType(pinataType);
            level.addFreshEntity(Objects.requireNonNull(pinata));
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
