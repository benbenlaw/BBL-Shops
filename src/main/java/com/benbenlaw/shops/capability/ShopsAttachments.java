package com.benbenlaw.shops.capability;

import com.benbenlaw.shops.Shops;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ShopsAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Shops.MOD_ID);

    public static final Supplier<AttachmentType<PlayerBalanceData>> PLAYER_BALANCE = ATTACHMENTS.register("player_balance",
            () -> AttachmentType.serializable(PlayerBalanceData::new).build());
}
