package com.benbenlaw.shops.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public record ChanceResult(ItemStack stack, float chance) {
    public static final ChanceResult EMPTY;
    public static final Codec<ChanceResult> CODEC;

    public ItemStack rollOutput(RandomSource rand) {
        int outputAmount = this.stack.getCount();

        for(int roll = 0; roll < this.stack.getCount(); ++roll) {
            if (rand.nextFloat() > this.chance) {
                --outputAmount;
            }
        }

        if (outputAmount == 0) {
            return ItemStack.EMPTY;
        } else {
            ItemStack out = this.stack.copy();
            out.setCount(outputAmount);
            return out;
        }
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        ItemStack.STREAM_CODEC.encode(buffer, this.stack());
        buffer.writeFloat(this.chance());
    }

    public static ChanceResult read(RegistryFriendlyByteBuf buffer) {
        return new ChanceResult((ItemStack)ItemStack.STREAM_CODEC.decode(buffer), buffer.readFloat());
    }

    static {
        EMPTY = new ChanceResult(ItemStack.EMPTY, 1.0F);
        CODEC = RecordCodecBuilder.create((inst) -> inst.group(ItemStack.CODEC.fieldOf("item").forGetter(ChanceResult::stack), Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(ChanceResult::chance)).apply(inst, ChanceResult::new));
    }
}
