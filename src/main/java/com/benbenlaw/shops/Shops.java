package com.benbenlaw.shops;

import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.config.StartUpConfig;
import com.benbenlaw.shops.datamaps.ShopsDataMaps;
import com.benbenlaw.shops.entity.ShopsEntityTypes;
import com.benbenlaw.shops.item.ShopsCreativeTab;
import com.benbenlaw.shops.item.ShopsItems;
import com.benbenlaw.shops.network.ShopsNetworking;
import com.benbenlaw.shops.recipe.ShopsRecipeTypes;
import com.benbenlaw.shops.sound.ShopsSounds;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Shops.MOD_ID)
public class Shops {
    public static final String MOD_ID = "shops";
    public static final Logger LOGGER = LogManager.getLogger();

    public Shops(final IEventBus eventBus, final ModContainer modContainer) {

        modContainer.registerConfig(ModConfig.Type.STARTUP, StartUpConfig.SPEC, "bbl/shops/coin_values.toml");

        ShopsAttachments.ATTACHMENT_TYPES.register(eventBus);
        ShopsItems.ITEMS.register(eventBus);
        ShopsCreativeTab.CREATIVE_MODE_TABS.register(eventBus);
        ShopsSounds.SOUND_EVENTS.register(eventBus);
        ShopsRecipeTypes.SERIALIZER.register(eventBus);
        ShopsRecipeTypes.TYPES.register(eventBus);
        ShopsEntityTypes.ENTITY_TYPES.register(eventBus);

        eventBus.addListener(this::commonSetup);

    }

    public void commonSetup(RegisterPayloadHandlersEvent event) {
        ShopsNetworking.registerNetworking(event);
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
