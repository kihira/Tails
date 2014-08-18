/*
 * Copyright (C) 2014  Kihira
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package kihira.tails.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
        this.tail4.setRotationPoint(0, 0, 7.5F);
        this.setRotationDegrees(this.tail4, 15F, 0, 0);

        this.tail5 = new ModelRenderer(this, 12, 26);
        this.tail5.addBox(-1.5F, -1.5F, 0, 3, 3, 2);
        this.tail5.setRotationPoint(0, 0, 1.5F);
        this.setRotationDegrees(this.tail5, 15F, 0, 0);

        this.tail4.addChild(this.tail5);
        this.tail3.addChild(this.tail4);
        this.tail2.addChild(this.tail3);
        this.tail1.addChild(this.tail2);
        this.tailBase.addChild(this.tail1);
    }

    @Override
    public void setRotationAngles(float seedVar, float seedModifierX, float par3, float par4, float par5, float par6, Entity entity) {
        float seed = (seedVar + entity.hashCode() + System.nanoTime() / 100000000F) / 5F;

        this.setRotationRadians(this.tailBase, MathHelper.cos((seed * seedModifierX) / 10F) / 15F, MathHelper.cos(seed) / (8F * par3), 0F);
        this.setRotationRadians(this.tail1, (float) Math.toRadians(-15F), MathHelper.cos(seed - 1) / (8F * par3), 0F);
        this.setRotationRadians(this.tail2, (float) Math.toRadians(-15F), MathHelper.cos(seed - 1.5F) / (8F * par3), 0F);
        this.setRotationRadians(this.tail3, (float) Math.toRadians(-25F), MathHelper.cos(seed - 2F) / (20F * par3), 0F);
        this.setRotationRadians(this.tail4, (float) Math.toRadians(15F), MathHelper.cos(seed - 3) / (8F * par3), 0F);
        this.setRotationRadians(this.tail5, (float) Math.toRadians(15F), MathHelper.cos(seed - 4) / (8F * par3), 0F);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype) {
        this.setRotationAngles(0, 0, 1F, 0, 0, 0, theEntity);

        if (subtype == 0) {
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
        }
        else if (subtype == 1) {
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            GL11.glRotatef(40F, 0F, 1F, 0F);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(34, 0, 1F, 0, 0, 0, theEntity);
            GL11.glRotatef(-80F, 0F, 1F, 0F);
            this.tailBase.render(0.0625F);
        }
        else if (subtype == 2) {
            //TODO adjust rotateAngleX/Z on the tail
            this.setRotationAngles(0, 4, 2F, 0, 0, 0, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(7256, 2, 2F, 0, 0, 0, theEntity);
            GL11.glRotatef(30F, 0F, 1F, 0F);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2735, 5, 2F, 0, 0, 0, theEntity);
            GL11.glRotatef(-60F, 0F, 1F, 0F);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(7254, 3, 2F, 0, 0, 0, theEntity);
            GL11.glRotatef(-15F, 0F, 1F, 0F);
            GL11.glRotatef(20F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
            GL11.glRotatef(-20F, 1F, 0F, 0F);

            this.setRotationAngles(75272, 6, 2F, 0, 0, 0, theEntity);
            GL11.glRotatef(30F, 0F, 1F, 0F);
            GL11.glRotatef(20F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
            GL11.glRotatef(-20F, 1F, 0F, 0F);

            this.setRotationAngles(5435, 4, 2F, 0, 0, 0, theEntity);
            GL11.glRotatef(30F, 0F, 1F, 0F);
            GL11.glRotatef(20F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
            GL11.glRotatef(-20F, 1F, 0F, 0F);

            this.setRotationAngles(846, 6, 2F, 0, 0, 0, theEntity);
            GL11.glRotatef(30F, 0F, 1F, 0F);
            GL11.glRotatef(20F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
            GL11.glRotatef(-20F, 1F, 0F, 0F);

            this.setRotationAngles(378, 3, 2F, 0, 0, 0, theEntity);
            GL11.glRotatef(-30F, 0F, 1F, 0F);
            GL11.glRotatef(46F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
            GL11.glRotatef(-46F, 1F, 0F, 0F);

            this.setRotationAngles(8638, 2, 2F, 0, 0, 0, theEntity);
            GL11.glRotatef(-30F, 0F, 1F, 0F);
            GL11.glRotatef(45F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
        }
    }
}