package com.benbenlaw.shops.sound;

import com.benbenlaw.shops.Shops;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ShopsSounds {


    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Shops.MOD_ID);

    public static final Supplier<SoundEvent> COIN_COLLECTED = registerSoundEvent("coin_collected");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        Identifier id = Shops.identifier(name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
