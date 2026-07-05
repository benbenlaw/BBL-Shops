package com.benbenlaw.shops.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;

public class TiltedCrossedArmsItemLayer<S extends HoldingEntityRenderState, M extends EntityModel<S> & VillagerLikeModel<S>> extends CrossedArmsItemLayer<S, M> {

    private static final float TILT_DEGREES = 35.0F;
    private static final float PIVOT_OFFSET = -0.2F;

    public TiltedCrossedArmsItemLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    protected void applyTranslation(S state, PoseStack poseStack) {
        super.applyTranslation(state, poseStack);
        poseStack.translate(0.0F, 0.0F, PIVOT_OFFSET);
        poseStack.mulPose(Axis.XP.rotationDegrees(TILT_DEGREES));
        poseStack.translate(0.0F, 0.0F, -PIVOT_OFFSET);
    }
}