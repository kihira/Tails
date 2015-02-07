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

public class ModelFoxEars extends ModelPartBase {
    //TODO we can cut down on the amount of models a little
    final ModelRenderer LeftEarInnerSmall;
    final ModelRenderer LeftEarInnerBig;
    final ModelRenderer RightEarInnerSmall;
    final ModelRenderer RightEarInnerBig;
    final ModelRenderer LeftEarInnerEdge;
    final ModelRenderer RightEarInnerEdge;
    final ModelRenderer LeftEarMiddleEdge;
    final ModelRenderer RightEarMiddleEdge;
    final ModelRenderer LeftEarTopEdge;
    final ModelRenderer RightEarTopEdge;
    final ModelRenderer LeftEarOuterEdge;
    final ModelRenderer RightEarOuterEdge;
    final ModelRenderer LeftEarBottomEdge;
    final ModelRenderer RightEarBottomEdge;
    final ModelRenderer LeftEarBackBig;
    final ModelRenderer RightEarBackBig;
    final ModelRenderer LeftEarBackSmall;
    final ModelRenderer RightEarBackSmall;

    public ModelFoxEars() {
        textureWidth = 16;
        textureHeight = 32;

        LeftEarInnerSmall = new ModelRenderer(this, 0, 16);
        LeftEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
        LeftEarInnerSmall.setRotationPoint(4F, -11F, 1F);
        LeftEarInnerSmall.mirror = true;

        LeftEarInnerBig = new ModelRenderer(this, 4, 16);
        LeftEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
        LeftEarInnerBig.setRotationPoint(3F, -10F, 1F);
        LeftEarInnerBig.mirror = true;

        RightEarInnerSmall = new ModelRenderer(this, 0, 19);
        RightEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
        RightEarInnerSmall.setRotationPoint(-5F, -11F, 1F);

        RightEarInnerBig = new ModelRenderer(this, 4, 19);
        RightEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
        RightEarInnerBig.setRotationPoint(-5F, -10F, 1F);

        LeftEarInnerEdge = new ModelRenderer(this, 0, 0);
        LeftEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        LeftEarInnerEdge.setRotationPoint(2F, -10F, 1F);
        LeftEarInnerEdge.mirror = true;

        RightEarInnerEdge = new ModelRenderer(this, 0, 4);
        RightEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        RightEarInnerEdge.setRotationPoint(-3F, -10F, 1F);

        LeftEarMiddleEdge = new ModelRenderer(this, 4, 0);
        LeftEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        LeftEarMiddleEdge.setRotationPoint(3F, -11F, 1F);
        LeftEarMiddleEdge.mirror = true;

        RightEarMiddleEdge = new ModelRenderer(this, 4, 2);
        RightEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        RightEarMiddleEdge.setRotationPoint(-4F, -11F, 1F);

        LeftEarTopEdge = new ModelRenderer(this, 4, 4);
        LeftEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        LeftEarTopEdge.setRotationPoint(4F, -12F, 1F);
        LeftEarTopEdge.mirror = true;

        RightEarTopEdge = new ModelRenderer(this, 4, 6);
        RightEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        RightEarTopEdge.setRotationPoint(-5F, -12F, 1F);

        LeftEarOuterEdge = new ModelRenderer(this, 0, 8);
        LeftEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        LeftEarOuterEdge.setRotationPoint(5F, -11F, 1F);
        LeftEarOuterEdge.mirror = true;

        RightEarOuterEdge = new ModelRenderer(this, 0, 12);
        RightEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        RightEarOuterEdge.setRotationPoint(-6F, -11F, 1F);

        LeftEarBottomEdge = new ModelRenderer(this, 10, 14);
        LeftEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
        LeftEarBottomEdge.setRotationPoint(3F, -8F, 1F);
        LeftEarBottomEdge.mirror = true;

        RightEarBottomEdge = new ModelRenderer(this, 10, 12);
        RightEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
        RightEarBottomEdge.setRotationPoint(-5F, -8F, 1F);

        LeftEarBackBig = new ModelRenderer(this, 4, 8);
        LeftEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
        LeftEarBackBig.setRotationPoint(4F, -11F, 2F);
        LeftEarBackBig.mirror = true;

        RightEarBackBig = new ModelRenderer(this, 4, 12);
        RightEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
        RightEarBackBig.setRotationPoint(-5F, -11F, 2F);

        LeftEarBackSmall = new ModelRenderer(this, 8, 0);
        LeftEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
        LeftEarBackSmall.setRotationPoint(3F, -10F, 2F);
        LeftEarBackSmall.mirror = true;

        RightEarBackSmall = new ModelRenderer(this, 8, 3);
        RightEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
        RightEarBackSmall.setRotationPoint(-4F, -10F, 2F);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        LeftEarInnerSmall.render(0.0625F);
        LeftEarInnerBig.render(0.0625F);
        RightEarInnerSmall.render(0.0625F);
        RightEarInnerBig.render(0.0625F);
        LeftEarInnerEdge.render(0.0625F);
        RightEarInnerEdge.render(0.0625F);
        LeftEarMiddleEdge.render(0.0625F);
        RightEarMiddleEdge.render(0.0625F);
        LeftEarTopEdge.render(0.0625F);
        RightEarTopEdge.render(0.0625F);
        LeftEarOuterEdge.render(0.0625F);
        RightEarOuterEdge.render(0.0625F);
        LeftEarBottomEdge.render(0.0625F);
        RightEarBottomEdge.render(0.0625F);
        LeftEarBackBig.render(0.0625F);
        RightEarBackBig.render(0.0625F);
        LeftEarBackSmall.render(0.0625F);
        RightEarBackSmall.render(0.0625F);
    }
}
