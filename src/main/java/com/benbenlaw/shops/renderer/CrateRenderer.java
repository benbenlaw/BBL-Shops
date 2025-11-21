package com.benbenlaw.shops.renderer;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.entity.CrateEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class CrateRenderer extends EntityRenderer<CrateEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Shops.MOD_ID, "textures/entity/crate/crate.png");

    private final CrateModel model;

    public CrateRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new CrateModel(ctx.bakeLayer(CrateModelLayers.CRATE));
        this.shadowRadius = 0.25f;
    }

    @Override
    public void render(CrateEntity crate, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0.0, 0.0, 0.0); // center cube on entity

        model.render(poseStack,
                buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),
                packedLight,
                OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        super.render(crate, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CrateEntity crate) {
        return TEXTURE;
    }
}
