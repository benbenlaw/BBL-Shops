package com.benbenlaw.shops.attachments;

import com.benbenlaw.shops.Shops;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ShopsAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Shops.MOD_ID);

    public static final Supplier<AttachmentType<PlayerBalanceData>> PLAYER_BALANCE =
            ATTACHMENT_TYPES.register("player_balance",
                    () -> AttachmentType.builder(() -> new PlayerBalanceData(0, new String[0]))
                            .serialize(PlayerBalanceData.CODEC.fieldOf("player_balance"))
                            .sync(PlayerBalanceData.STREAM_CODEC)
                            .build());

    public static final Supplier<AttachmentType<ShopTraderData>> SHOP_TRADER_DATA =
            ATTACHMENT_TYPES.register("shop_trader_data",
                    () -> AttachmentType.builder(() -> ShopTraderData.EMPTY)
                            .serialize(ShopTraderData.CODEC.fieldOf("shop_trader_data"))
                            .sync(ShopTraderData.STREAM_CODEC)
                            .build());
}
