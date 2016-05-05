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
import org.lwjgl.opengl.GL11;

public class ModelFoxEars extends ModelPartBase {
    private final ModelRenderer leftEarInnerSmall;
    private final ModelRenderer leftEarInnerBig;
    private final ModelRenderer rightEarInnerSmall;
    private final ModelRenderer rightEarInnerBig;
    private final ModelRenderer leftEarInnerEdge;
    private final ModelRenderer rightEarInnerEdge;
    private final ModelRenderer leftEarMiddleEdge;
    private final ModelRenderer rightEarMiddleEdge;
    private final ModelRenderer leftEarTopEdge;
    private final ModelRenderer rightEarTopEdge;
    private final ModelRenderer leftEarOuterEdge;
    private final ModelRenderer rightEarOuterEdge;
    private final ModelRenderer leftEarBottomEdge;
    private final ModelRenderer rightEarBottomEdge;
    private final ModelRenderer leftEarBackBig;
    private final ModelRenderer rightEarBackBig;
    private final ModelRenderer leftEarBackSmall;
    private final ModelRenderer lightEarBackSmall;

    public ModelFoxEars() {
        textureWidth = 16;
        textureHeight = 32;

        leftEarInnerSmall = new ModelRenderer(this, 0, 16);
        leftEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
        leftEarInnerSmall.setRotationPoint(4F, -11F, 1F);
        leftEarInnerSmall.mirror = true;

        leftEarInnerBig = new ModelRenderer(this, 4, 16);
        leftEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
        leftEarInnerBig.setRotationPoint(3F, -10F, 1F);
        leftEarInnerBig.mirror = true;

        rightEarInnerSmall = new ModelRenderer(this, 0, 19);
        rightEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
        rightEarInnerSmall.setRotationPoint(-5F, -11F, 1F);

        rightEarInnerBig = new ModelRenderer(this, 4, 19);
        rightEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
        rightEarInnerBig.setRotationPoint(-5F, -10F, 1F);

        leftEarInnerEdge = new ModelRenderer(this, 0, 0);
        leftEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        leftEarInnerEdge.setRotationPoint(2F, -10F, 1F);
        leftEarInnerEdge.mirror = true;

        rightEarInnerEdge = new ModelRenderer(this, 0, 4);
        rightEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        rightEarInnerEdge.setRotationPoint(-3F, -10F, 1F);

        leftEarMiddleEdge = new ModelRenderer(this, 4, 0);
        leftEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        leftEarMiddleEdge.setRotationPoint(3F, -11F, 1F);
        leftEarMiddleEdge.mirror = true;

        rightEarMiddleEdge = new ModelRenderer(this, 4, 2);
        rightEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        rightEarMiddleEdge.setRotationPoint(-4F, -11F, 1F);

        leftEarTopEdge = new ModelRenderer(this, 4, 4);
        leftEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        leftEarTopEdge.setRotationPoint(4F, -12F, 1F);
        leftEarTopEdge.mirror = true;

        rightEarTopEdge = new ModelRenderer(this, 4, 6);
        rightEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
        rightEarTopEdge.setRotationPoint(-5F, -12F, 1F);

        leftEarOuterEdge = new ModelRenderer(this, 0, 8);
        leftEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        leftEarOuterEdge.setRotationPoint(5F, -11F, 1F);
        leftEarOuterEdge.mirror = true;

        rightEarOuterEdge = new ModelRenderer(this, 0, 12);
        rightEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
        rightEarOuterEdge.setRotationPoint(-6F, -11F, 1F);

        leftEarBottomEdge = new ModelRenderer(this, 10, 14);
        leftEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
        leftEarBottomEdge.setRotationPoint(3F, -8F, 1F);
        leftEarBottomEdge.mirror = true;

        rightEarBottomEdge = new ModelRenderer(this, 10, 12);
        rightEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
        rightEarBottomEdge.setRotationPoint(-5F, -8F, 1F);

        leftEarBackBig = new ModelRenderer(this, 4, 8);
        leftEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
        leftEarBackBig.setRotationPoint(4F, -11F, 2F);
        leftEarBackBig.mirror = true;

        rightEarBackBig = new ModelRenderer(this, 4, 12);
        rightEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
        rightEarBackBig.setRotationPoint(-5F, -11F, 2F);

        leftEarBackSmall = new ModelRenderer(this, 8, 0);
        leftEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
        leftEarBackSmall.setRotationPoint(3F, -10F, 2F);
        leftEarBackSmall.mirror = true;

        lightEarBackSmall = new ModelRenderer(this, 8, 3);
        lightEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
        lightEarBackSmall.setRotationPoint(-4F, -10F, 2F);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        GL11.glPushMatrix();
        if (subtype == 1) {
            GL11.glTranslatef(0f, 0f, -0.0625f);
            GL11.glTranslatef(-0.4375f, 0f, 0f);
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
            GL11.glTranslatef(0.875f, 0f, 0f);
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
            GL11.glTranslatef(-0.4375f, 0f, 0f);
            GL11.glTranslatef(0f, 0f, 0.0625f);
        }
        GL11.glPopMatrix();
    }
}
