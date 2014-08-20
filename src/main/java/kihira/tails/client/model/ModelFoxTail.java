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
    public void setRotationAngles(float seed, float yOffset, float xOffset, float xAngle, float yAngle, float partialTicks, Entity entity) {
        this.setRotationRadians(this.tailBase, xAngle + (Math.cos(seed + xOffset) / 15F), yAngle + (Math.cos(seed + yOffset) / 8F), 0F);
        this.setRotationRadians(this.tail1, -0.2617993877991494, Math.cos(seed - 1 + yOffset) / 8F, 0F);
        this.setRotationRadians(this.tail2, -0.2617993877991494, Math.cos(seed - 1.5F + yOffset) / 8F, 0F);
        this.setRotationRadians(this.tail3, -0.4363323129985824, Math.cos(seed - 2 + yOffset) / 20F, 0F);
        this.setRotationRadians(this.tail4, 0.2617993877991494, Math.cos(seed - 3 + yOffset) / 8F, 0F);
        this.setRotationRadians(this.tail5, 0.2617993877991494, Math.cos(seed - 4 + yOffset) / 8F, 0F);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        float seed = getAnimationTime(4000F, theEntity);

        this.setRotationAngles(seed, 0, 0, 0, 0, partialTicks, theEntity);

        if (subtype == 0) {
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
        }
        else if (subtype == 1) {
            GL11.glRotatef(-20F, 1F, 0F, 0F);
            GL11.glRotatef(40F, 0F, 1F, 0F);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, 1F, 0, 0, 0, 0, theEntity);
            GL11.glRotatef(-80F, 0F, 1F, 0F);
            this.tailBase.render(0.0625F);
        }
        else if (subtype == 2) {
            seed = getAnimationTime(6500F, theEntity);

            this.setRotationAngles(seed, -1.5F, 2.5F, 0F, 0, 0, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, -1.3F, 1.6F, 0, (float) Math.toRadians(30F), 0, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, -1.1F, 0.7F, 0, (float) Math.toRadians(-30F), 0, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, -1.2F, 2.6F, (float) Math.toRadians(20F), (float) Math.toRadians(-15F), 0, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, -0.9F, 1.1F, (float) Math.toRadians(20F), (float) Math.toRadians(15F), 0, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, -0.8F, 2F, (float) Math.toRadians(20F), (float) Math.toRadians(45F), 0, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, -1.25F, 0.6F, (float) Math.toRadians(20F), (float) Math.toRadians(-45F), 0, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, -1.4F, 0.9F, (float) Math.toRadians(45F), (float) Math.toRadians(15F), 0, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(seed, -1.1F, 1.6F, (float) Math.toRadians(45F), (float) Math.toRadians(-15F), 0, theEntity);
            this.tailBase.render(0.0625F);
        }
    }
}