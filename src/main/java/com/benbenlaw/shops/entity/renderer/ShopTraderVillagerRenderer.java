package com.benbenlaw.shops.entity.renderer;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.attachments.ShopsAttachments;
import com.benbenlaw.shops.entity.ShopTraderMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.Villager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShopTraderVillagerRenderer extends LivingEntityRenderer<ShopTraderMob, ShopTraderRenderState, ShopTraderModel> {

    private static final Identifier VILLAGER_BASE_LOCATION = Shops.identifier("textures/entity/trader/trader.png");
    private static final CustomHeadLayer.Transforms CUSTOM_HEAD_TRANSFORMS = new CustomHeadLayer.Transforms(-0.1171875F, -0.07421875F, 1.0F);

    private static final Map<String, Identifier> TEXTURE_CACHE = new ConcurrentHashMap<>();

    public ShopTraderVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new ShopTraderModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache(), CUSTOM_HEAD_TRANSFORMS));
        this.addLayer(new TiltedCrossedArmsItemLayer<>(this));
    }

    @Override
    public Identifier getTextureLocation(ShopTraderRenderState state) {
        if (state.traderName == null || state.traderName.isEmpty()) {
            return VILLAGER_BASE_LOCATION;
        }
        return TEXTURE_CACHE.computeIfAbsent(state.traderName, this::resolveTextureFor);
    }

    private Identifier resolveTextureFor(String traderName) {
        String safeName = traderName.toLowerCase().replace(' ', '_');
        Identifier candidate = Shops.identifier("textures/entity/trader/" + safeName + ".png");

        if (Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent()) {
            return candidate;
        }
        return VILLAGER_BASE_LOCATION;
    }

    @Override
    public ShopTraderRenderState createRenderState() {
        return new ShopTraderRenderState();
    }

    @Override
    public void extractRenderState(ShopTraderMob entity, ShopTraderRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        net.minecraft.client.renderer.entity.state.HoldingEntityRenderState.extractHoldingEntityRenderState(entity, state, this.itemModelResolver);
        state.isUnhappy = false;
        state.villagerData = Villager.createDefaultVillagerData();
        state.traderName = entity.getData(ShopsAttachments.SHOP_TRADER_DATA).traderName().orElse(null);
    }
}