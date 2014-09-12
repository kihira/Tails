/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelFoxTail extends ModelTailBase {
    private ModelRenderer tailBase;
    private ModelRenderer tail1;
    private ModelRenderer tail2;
    private ModelRenderer tail3;
    private ModelRenderer tail4;
    private ModelRenderer tail5;

    public ModelFoxTail() {
        this.tailBase = new ModelRenderer(this);
        this.tailBase.addBox(-1, -1, 0, 2, 2, 3);
        this.tailBase.setRotationPoint(0, 0, 0);
        this.setRotationDegrees(this.tailBase, -15F, 0, 0);

        this.tail1 = new ModelRenderer(this, 10, 0);
        this.tail1.addBox(-1.5F, -1.5F, 0, 3, 3, 2);
        this.tail1.setRotationPoint(0, 0, 1.5F);
        this.setRotationDegrees(this.tail1, -15F, 0, 0);

        this.tail2 = new ModelRenderer(this, 0, 5);
        this.tail2.addBox(-2, -2, 0, 4, 4, 4);
        this.tail2.setRotationPoint(0, 0, 1.5F);
        this.setRotationDegrees(this.tail2, -15F, 0, 0);

        this.tail3 = new ModelRenderer(this, 0, 13);
        this.tail3.addBox(-2.5F, -2.5F, 0, 5, 5, 8);
        this.tail3.setRotationPoint(0, 0, 3F);
        this.setRotationDegrees(this.tail3, -25F, 0, 0);

        this.tail4 = new ModelRenderer(this, 0, 26);
        this.tail4.addBox(-2, -2, 0, 4, 4, 2);
        this.tail4.setRotationPoint(0, 0, 7.4F);
        this.setRotationDegrees(this.tail4, 15F, 0, 0);

        this.tail5 = new ModelRenderer(this, 12, 26);
        this.tail5.addBox(-1.5F, -1.5F, 0, 3, 3, 2);
        this.tail5.setRotationPoint(0, 0, 1.4F);
        this.setRotationDegrees(this.tail5, 15F, 0, 0);

        this.tail4.addChild(this.tail5);
        this.tail3.addChild(this.tail4);
        this.tail2.addChild(this.tail3);
        this.tail1.addChild(this.tail2);
        this.tailBase.addChild(this.tail1);
    }

    public void setRotationAngles(int subtype, float seed, float yOffset, float xOffset, float xAngle, float yAngle, float partialTicks, Entity entity) {
        double xAngleOffset = 0;
        double yAngleOffset = 0;
        double zAngleOffset = 0;
        if (entity instanceof EntityPlayer) {
            double[] angles = getMotionAngles((EntityPlayer) entity, partialTicks);
            xAngleOffset = angles[0];
            yAngleOffset = angles[1];
            zAngleOffset = angles[2];

            switch (subtype) {
                case 0:
                case 1:
                    xAngleOffset = MathHelper.clamp_double(xAngleOffset * 0.7D, -1D, 0.45D);
                    break;
                //Nine tails
                case 2:
                    xAngleOffset *= 0.25D;
                    break;
            }
        }

        this.setRotationRadians(this.tailBase, xAngle + (Math.cos(seed + xOffset) / 15F) + xAngleOffset, (-zAngleOffset / 2F) + yAngle + (Math.cos(seed + yOffset) / 8F) + yAngleOffset, -zAngleOffset / 8F);
        this.setRotationRadians(this.tail1, -0.2617993877991494 + xAngleOffset + Math.abs(zAngleOffset), Math.cos(seed - 1 + yOffset) / 8F, -zAngleOffset / 8F);
        this.setRotationRadians(this.tail2, -0.2617993877991494 + (xAngleOffset / 2F), Math.cos(seed - 1.5F + yOffset) / 8F, -zAngleOffset / 8F);
        this.setRotationRadians(this.tail3, -0.4363323129985824 + (xAngleOffset / 2F), Math.cos(seed - 2 + yOffset) / 20F, -zAngleOffset / 20F);
        this.setRotationRadians(this.tail4, 0.2617993877991494 - (xAngleOffset / 2F), Math.cos(seed - 3 + yOffset) / 8F, 0F);
        this.setRotationRadians(this.tail5, 0.2617993877991494 - (xAngleOffset / 2.5F), Math.cos(seed - 4 + yOffset) / 8F, 0F);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        float seed = getAnimationTime(4000F, theEntity);
        this.setRotationAngles(0, seed, 0, 0, 0, 0, partialTicks, theEntity);

        if (subtype == 0) {
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
        }
        else if (subtype == 1) {
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            GL11.glRotatef(40F, 0F, 1F, 0F);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, 1F, 0, 0, 0, partialTicks, theEntity);
            GL11.glRotatef(-80F, 0F, 1F, 0F);
            this.tailBase.render(0.0625F);
        }
        else if (subtype == 2) {
            seed = getAnimationTime(6500F, theEntity);

            this.setRotationAngles(2, seed, -1.5F, 2.5F, 0F, 0, partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, seed, -1.3F, 1.6F, 0, (float) Math.toRadians(30F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, seed, -1.1F, 0.7F, 0, (float) Math.toRadians(-30F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, seed, -1.2F, 2.6F, (float) Math.toRadians(20F), (float) Math.toRadians(-15F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, seed, -0.9F, 1.1F, (float) Math.toRadians(20F), (float) Math.toRadians(15F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, seed, -0.8F, 2F, (float) Math.toRadians(20F), (float) Math.toRadians(45F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, seed, -1.25F, 0.6F, (float) Math.toRadians(20F), (float) Math.toRadians(-45F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, seed, -1.4F, 0.9F, (float) Math.toRadians(45F), (float) Math.toRadians(15F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, seed, -1.1F, 1.6F, (float) Math.toRadians(45F), (float) Math.toRadians(-15F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);
        }
    }
}