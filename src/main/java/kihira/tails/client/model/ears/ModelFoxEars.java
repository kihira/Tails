/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model.ears;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelFoxEars extends ModelEarBase {
    ModelRenderer LeftEarInnerSmall;
    ModelRenderer LeftEarInnerBig;
    ModelRenderer RightEarInnerSmall;
    ModelRenderer RightEarInnerBig;
    ModelRenderer LeftEarInnerEdge;
    ModelRenderer RightEarInnerEdge;
    ModelRenderer LeftEarMiddleEdge;
    ModelRenderer RightEarMiddleEdge;
    ModelRenderer LeftEarTopEdge;
    ModelRenderer RightEarTopEdge;
    ModelRenderer LeftEarOuterEdge;
    ModelRenderer RightEarOuterEdge;
    ModelRenderer LeftEarBottomEdge;
    ModelRenderer RightEarBottomEdge;
    ModelRenderer LeftEarBackBig;
    ModelRenderer RightEarBackBig;
    ModelRenderer LeftEarBackSmall;
    ModelRenderer RightEarBackSmall;

    public ModelFoxEars() {
        textureWidth = 16;
        textureHeight = 32;

        LeftEarInnerSmall = new ModelRenderer(this, 0, 16);
        LeftEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
        LeftEarInnerSmall.setRotationPoint(4F, 14F, 1F);
        LeftEarInnerSmall.setTextureSize(16, 32);
        LeftEarInnerSmall.mirror = true;
        setRotation(LeftEarInnerSmall, 0F, 0F, 0F);

        LeftEarInnerBig = new ModelRenderer(this, 4, 16);
        LeftEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
        LeftEarInnerBig.setRotationPoint(3F, 15F, 1F);
        LeftEarInnerBig.setTextureSize(16, 32);
        LeftEarInnerBig.mirror = true;
        setRotation(LeftEarInnerBig, 0F, 0F, 0F);

        RightEarInnerSmall = new ModelRenderer(this, 0, 19);
        RightEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
        RightEarInnerSmall.setRotationPoint(-5F, 14F, 1F);
        RightEarInnerSmall.setTextureSize(16, 32);
        setRotation(RightEarInnerSmall, 0F, 0F, 0F);
        RightEarInnerSmall.mirror = false;

        RightEarInnerBig = new ModelRenderer(this, 4, 19);
        RightEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
        RightEarInnerBig.setRotationPoint(-5F, 15F, 1F);
        RightEarInnerBig.setTextureSize(16, 32);
        setRotation(RightEarInnerBig, 0F, 0F, 0F);
        RightEarInnerBig.mirror = false;

        LeftEarInnerEdge = new ModelRenderer(this, 0, 0);
        LeftEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        LeftEarInnerEdge.setRotationPoint(2F, 15F, 1F);
        LeftEarInnerEdge.setTextureSize(16, 32);
        LeftEarInnerEdge.mirror = true;
        setRotation(LeftEarInnerEdge, 0F, 0F, 0F);

        RightEarInnerEdge = new ModelRenderer(this, 0, 4);
        RightEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        RightEarInnerEdge.setRotationPoint(-3F, 15F, 1F);
        RightEarInnerEdge.setTextureSize(16, 32);
        setRotation(RightEarInnerEdge, 0F, 0F, 0F);
        RightEarInnerEdge.mirror = false;

        LeftEarMiddleEdge = new ModelRenderer(this, 4, 0);
        LeftEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        LeftEarMiddleEdge.setRotationPoint(3F, 14F, 1F);
        LeftEarMiddleEdge.setTextureSize(16, 32);
        LeftEarMiddleEdge.mirror = true;
        setRotation(LeftEarMiddleEdge, 0F, 0F, 0F);

        RightEarMiddleEdge = new ModelRenderer(this, 4, 2);
        RightEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        RightEarMiddleEdge.setRotationPoint(-4F, 14F, 1F);
        RightEarMiddleEdge.setTextureSize(16, 32);
        setRotation(RightEarMiddleEdge, 0F, 0F, 0F);
        RightEarMiddleEdge.mirror = false;

        LeftEarTopEdge = new ModelRenderer(this, 4, 4);
        LeftEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        LeftEarTopEdge.setRotationPoint(4F, 13F, 1F);
        LeftEarTopEdge.setTextureSize(16, 32);
        LeftEarTopEdge.mirror = true;
        setRotation(LeftEarTopEdge, 0F, 0F, 0F);

        RightEarTopEdge = new ModelRenderer(this, 4, 6);
        RightEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        RightEarTopEdge.setRotationPoint(-5F, 13F, 1F);
        RightEarTopEdge.setTextureSize(16, 32);
        setRotation(RightEarTopEdge, 0F, 0F, 0F);
        RightEarTopEdge.mirror = false;

        LeftEarOuterEdge = new ModelRenderer(this, 0, 8);
        LeftEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        LeftEarOuterEdge.setRotationPoint(5F, 14F, 1F);
        LeftEarOuterEdge.setTextureSize(16, 32);
        LeftEarOuterEdge.mirror = true;
        setRotation(LeftEarOuterEdge, 0F, 0F, 0F);

        RightEarOuterEdge = new ModelRenderer(this, 0, 12);
        RightEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        RightEarOuterEdge.setRotationPoint(-6F, 14F, 1F);
        RightEarOuterEdge.setTextureSize(16, 32);
        setRotation(RightEarOuterEdge, 0F, 0F, 0F);
        RightEarOuterEdge.mirror = false;

        LeftEarBottomEdge = new ModelRenderer(this, 10, 14);
        LeftEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
        LeftEarBottomEdge.setRotationPoint(3F, 17F, 1F);
        LeftEarBottomEdge.setTextureSize(16, 32);
        LeftEarBottomEdge.mirror = true;
        setRotation(LeftEarBottomEdge, 0F, 0F, 0F);

        RightEarBottomEdge = new ModelRenderer(this, 10, 12);
        RightEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
        RightEarBottomEdge.setRotationPoint(-5F, 17F, 1F);
        RightEarBottomEdge.setTextureSize(16, 32);
        setRotation(RightEarBottomEdge, 0F, 0F, 0F);
        RightEarBottomEdge.mirror = false;

        LeftEarBackBig = new ModelRenderer(this, 4, 8);
        LeftEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
        LeftEarBackBig.setRotationPoint(4F, 14F, 2F);
        LeftEarBackBig.setTextureSize(16, 32);
        LeftEarBackBig.mirror = true;
        setRotation(LeftEarBackBig, 0F, 0F, 0F);

        RightEarBackBig = new ModelRenderer(this, 4, 12);
        RightEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
        RightEarBackBig.setRotationPoint(-5F, 14F, 2F);
        RightEarBackBig.setTextureSize(16, 32);
        RightEarBackBig.mirror = true;
        setRotation(RightEarBackBig, 0F, 0F, 0F);
        RightEarBackBig.mirror = false;

        LeftEarBackSmall = new ModelRenderer(this, 8, 0);
        LeftEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
        LeftEarBackSmall.setRotationPoint(3F, 15F, 2F);
        LeftEarBackSmall.setTextureSize(16, 32);
        LeftEarBackSmall.mirror = true;
        setRotation(LeftEarBackSmall, 0F, 0F, 0F);

        RightEarBackSmall = new ModelRenderer(this, 8, 3);
        RightEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
        RightEarBackSmall.setRotationPoint(-4F, 15F, 2F);
        RightEarBackSmall.setTextureSize(16, 32);
        RightEarBackSmall.mirror = true;
        setRotation(RightEarBackSmall, 0F, 0F, 0F);
        RightEarBackSmall.mirror = false;
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
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

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
