/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model.ears;

import kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;

public class ModelPandaEars extends ModelPartBase {

    ModelRenderer leftEar;
    ModelRenderer rightEar;

    public ModelPandaEars() {
        textureWidth = 32;
        textureHeight = 32;

        leftEar = new ModelRenderer(this, 0, 0);
        leftEar.addBox(-2F, -2F, 0F, 3, 3, 1);
        leftEar.setRotationPoint(-4F, -8F, 0F);
        leftEar.setTextureSize(32, 32);
        leftEar.mirror = true;

        rightEar = new ModelRenderer(this, 0, 4);
        rightEar.addBox(-1F, -2F, 0F, 3, 3, 1);
        rightEar.setRotationPoint(4F, -8F, 0F);
        rightEar.setTextureSize(32, 32);
        rightEar.mirror = true;
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        leftEar.render(0.0625F);
        rightEar.render(0.0625F);
    }
}
