package com.benbenlaw.shops.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CrateModel {

    private final ModelPart crate;

    public CrateModel(ModelPart root) {
        this.crate = root.getChild("crate");
    }

    /** Define the cube shape */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        // single 1x1 block cube (16x16x16 for 1:1 Minecraft scale)
        part.addOrReplaceChild("crate",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-8.0F, 0.0F, -8.0F, 16.0F, 16.0F, 16.0F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 16, 16);
    }

    /** Render the cube */
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay) {
        crate.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}