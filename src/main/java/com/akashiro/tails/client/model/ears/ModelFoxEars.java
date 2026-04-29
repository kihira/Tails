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


public class ModelFoxEars extends ModelPartBase {
    private final LegacyModelRenderer leftEarInnerSmall;
    private final LegacyModelRenderer leftEarInnerBig;
    private final LegacyModelRenderer rightEarInnerSmall;
    private final LegacyModelRenderer rightEarInnerBig;
    private final LegacyModelRenderer leftEarInnerEdge;
    private final LegacyModelRenderer rightEarInnerEdge;
    private final LegacyModelRenderer leftEarMiddleEdge;
    private final LegacyModelRenderer rightEarMiddleEdge;
    private final LegacyModelRenderer leftEarTopEdge;
    private final LegacyModelRenderer rightEarTopEdge;
    private final LegacyModelRenderer leftEarOuterEdge;
    private final LegacyModelRenderer rightEarOuterEdge;
    private final LegacyModelRenderer leftEarBottomEdge;
    private final LegacyModelRenderer rightEarBottomEdge;
    private final LegacyModelRenderer leftEarBackBig;
    private final LegacyModelRenderer rightEarBackBig;
    private final LegacyModelRenderer leftEarBackSmall;
    private final LegacyModelRenderer lightEarBackSmall;

    public ModelFoxEars() {
        textureWidth = 16;
        textureHeight = 32;

        leftEarInnerSmall = new LegacyModelRenderer(this, 0, 16);
        leftEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
        leftEarInnerSmall.setRotationPoint(4F, -11F, 1F);
        leftEarInnerSmall.mirror = true;

        leftEarInnerBig = new LegacyModelRenderer(this, 4, 16);
        leftEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
        leftEarInnerBig.setRotationPoint(3F, -10F, 1F);
        leftEarInnerBig.mirror = true;

        rightEarInnerSmall = new LegacyModelRenderer(this, 0, 19);
        rightEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
        rightEarInnerSmall.setRotationPoint(-5F, -11F, 1F);

        rightEarInnerBig = new LegacyModelRenderer(this, 4, 19);
        rightEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
        rightEarInnerBig.setRotationPoint(-5F, -10F, 1F);

        leftEarInnerEdge = new LegacyModelRenderer(this, 0, 0);
        leftEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        leftEarInnerEdge.setRotationPoint(2F, -10F, 1F);
        leftEarInnerEdge.mirror = true;

        rightEarInnerEdge = new LegacyModelRenderer(this, 0, 4);
        rightEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        rightEarInnerEdge.setRotationPoint(-3F, -10F, 1F);

        leftEarMiddleEdge = new LegacyModelRenderer(this, 4, 0);
        leftEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        leftEarMiddleEdge.setRotationPoint(3F, -11F, 1F);
        leftEarMiddleEdge.mirror = true;

        rightEarMiddleEdge = new LegacyModelRenderer(this, 4, 2);
        rightEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        rightEarMiddleEdge.setRotationPoint(-4F, -11F, 1F);

        leftEarTopEdge = new LegacyModelRenderer(this, 4, 4);
        leftEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        leftEarTopEdge.setRotationPoint(4F, -12F, 1F);
        leftEarTopEdge.mirror = true;

        rightEarTopEdge = new LegacyModelRenderer(this, 4, 6);
        rightEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        rightEarTopEdge.setRotationPoint(-5F, -12F, 1F);

        leftEarOuterEdge = new LegacyModelRenderer(this, 0, 8);
        leftEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        leftEarOuterEdge.setRotationPoint(5F, -11F, 1F);
        leftEarOuterEdge.mirror = true;

        rightEarOuterEdge = new LegacyModelRenderer(this, 0, 12);
        rightEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        rightEarOuterEdge.setRotationPoint(-6F, -11F, 1F);

        leftEarBottomEdge = new LegacyModelRenderer(this, 10, 14);
        leftEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
        leftEarBottomEdge.setRotationPoint(3F, -8F, 1F);
        leftEarBottomEdge.mirror = true;

        rightEarBottomEdge = new LegacyModelRenderer(this, 10, 12);
        rightEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
        rightEarBottomEdge.setRotationPoint(-5F, -8F, 1F);

        leftEarBackBig = new LegacyModelRenderer(this, 4, 8);
        leftEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
        leftEarBackBig.setRotationPoint(4F, -11F, 2F);
        leftEarBackBig.mirror = true;

        rightEarBackBig = new LegacyModelRenderer(this, 4, 12);
        rightEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
        rightEarBackBig.setRotationPoint(-5F, -11F, 2F);

        leftEarBackSmall = new LegacyModelRenderer(this, 8, 0);
        leftEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
        leftEarBackSmall.setRotationPoint(3F, -10F, 2F);
        leftEarBackSmall.mirror = true;

        lightEarBackSmall = new LegacyModelRenderer(this, 8, 3);
        lightEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
        lightEarBackSmall.setRotationPoint(-4F, -10F, 2F);
    }

        public void render(LivingEntity theEntity, int subtype, float partialTicks) {
        pushMatrix();
        if (subtype == 1) {
            translate(0f, 0f, -0.0625f);
            translate(-0.4375f, 0f, 0f);
        }
        leftEarInnerSmall.render(0.0625F);
        leftEarInnerBig.render(0.0625F);
        leftEarInnerEdge.render(0.0625F);
        leftEarMiddleEdge.render(0.0625F);
        leftEarTopEdge.render(0.0625F);
        leftEarOuterEdge.render(0.0625F);
        leftEarBottomEdge.render(0.0625F);
        leftEarBackBig.render(0.0625F);
        leftEarBackSmall.render(0.0625F);
        if (subtype == 1) {
            translate(0.875f, 0f, 0f);
        }
        rightEarInnerSmall.render(0.0625F);
        rightEarInnerBig.render(0.0625F);
        rightEarInnerEdge.render(0.0625F);
        rightEarMiddleEdge.render(0.0625F);
        rightEarTopEdge.render(0.0625F);
        rightEarOuterEdge.render(0.0625F);
        rightEarBottomEdge.render(0.0625F);
        rightEarBackBig.render(0.0625F);
        lightEarBackSmall.render(0.0625F);
        if (subtype == 1) {
            translate(-0.4375f, 0f, 0f);
            translate(0f, 0f, 0.0625f);
        }
        popMatrix();
    }
}
