/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package com.akashiro.tails.client.model.ears;

import com.akashiro.tails.client.model.ModelPartBase;
import com.akashiro.tails.client.model.LegacyModelRenderer;
import net.minecraft.world.entity.LivingEntity;

public class ModelPandaEars extends ModelPartBase {

    final LegacyModelRenderer leftEar;
    final LegacyModelRenderer rightEar;

    public ModelPandaEars() {
        textureWidth = 32;
        textureHeight = 32;

        leftEar = new LegacyModelRenderer(this, 0, 0);
        leftEar.addBox(-2F, -2F, 0F, 3, 3, 1);
        leftEar.setRotationPoint(-4F, -8F, 0F);
        leftEar.setTextureSize(32, 32);
        leftEar.mirror = true;

        rightEar = new LegacyModelRenderer(this, 0, 4);
        rightEar.addBox(-1F, -2F, 0F, 3, 3, 1);
        rightEar.setRotationPoint(4F, -8F, 0F);
        rightEar.setTextureSize(32, 32);
        rightEar.mirror = true;
    }

        public void render(LivingEntity theEntity, int subtype, float partialTicks) {
        leftEar.render(0.0625F);
        rightEar.render(0.0625F);
    }
}
