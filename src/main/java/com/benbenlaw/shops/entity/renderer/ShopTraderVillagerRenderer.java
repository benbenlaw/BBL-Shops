package com.benbenlaw.shops.entity.renderer;

import com.benbenlaw.shops.Shops;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.npc.BabyVillagerModel;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.Villager;

public class ShopTraderVillagerRenderer extends AgeableMobRenderer<Villager, VillagerRenderState, VillagerModel> {

    private static final Identifier VILLAGER_BASE_LOCATION = Shops.identifier("textures/entity/trader/trader.png");
    private static final Identifier VILLAGER_BABY_LOCATION = Identifier.withDefaultNamespace("textures/entity/villager/villager_baby.png");
    private static final CustomHeadLayer.Transforms CUSTOM_HEAD_TRANSFORMS = new CustomHeadLayer.Transforms(-0.1171875F, -0.07421875F, 1.0F);

    public ShopTraderVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), new BabyVillagerModel(context.bakeLayer(ModelLayers.VILLAGER_BABY)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache(), CUSTOM_HEAD_TRANSFORMS));
        //this.addLayer(new VillagerProfessionLayer<>(this, context.getResourceManager(), "villager", new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER_NO_HAT)), new BabyVillagerModel(context.bakeLayer(ModelLayers.VILLAGER_BABY_NO_HAT))));
        this.addLayer(new TiltedCrossedArmsItemLayer<>(this));
    }

    @Override
    public Identifier getTextureLocation(VillagerRenderState state) {
        return state.isBaby ? VILLAGER_BABY_LOCATION : VILLAGER_BASE_LOCATION;
    }

    @Override
    protected float getShadowRadius(VillagerRenderState state) {
        float radius = super.getShadowRadius(state);
        return state.isBaby ? radius * 0.5F : radius;
    }

    @Override
    public VillagerRenderState createRenderState() {
        return new VillagerRenderState();
    }

    @Override
    public void extractRenderState(Villager entity, VillagerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        net.minecraft.client.renderer.entity.state.HoldingEntityRenderState.extractHoldingEntityRenderState(entity, state, this.itemModelResolver);
        state.isUnhappy = entity.getUnhappyCounter() > 0;
        state.villagerData = entity.getVillagerData();
    }
}