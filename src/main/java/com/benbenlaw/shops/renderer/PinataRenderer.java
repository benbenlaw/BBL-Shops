package com.benbenlaw.shops.renderer;

import com.benbenlaw.shops.Shops;
import com.benbenlaw.shops.entity.PinataEntity;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class PinataRenderer extends AbstractHorseRenderer<PinataEntity, HorseModel<PinataEntity>> {
    public PinataRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HorseModel<>(ctx.bakeLayer(ModelLayers.HORSE)), 0.5f); // scale down to look like baby
        this.shadowRadius = 0.3f;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PinataEntity entity) {
        return entity.getTexture();
    }
}
