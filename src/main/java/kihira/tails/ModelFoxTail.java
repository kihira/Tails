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

package kihira.tails;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ModelFoxTail extends ModelBase {
    ModelRenderer tailBase;
    ModelRenderer tail2;
    ModelRenderer tail3;
    ModelRenderer tail4;
    ModelRenderer tail5;
    ModelRenderer tail6;

    public ModelFoxTail() {
        tailBase = new ModelRenderer(this);
        tailBase.addBox(-1, -1, 0, 2, 2, 3);
        tailBase.setRotationPoint(0, 0, 0);
        setRotationDegrees(tailBase, -15, 0, 0);

        tail2 = new ModelRenderer(this, 10, 0);
        tail2.addBox(-1.5F, -1.5F, 0, 3, 3, 2);
        tail2.setRotationPoint(0, 0, 1.5F);
        setRotationDegrees(tail2, -15, 0, 0);
        tailBase.addChild(tail2);

        tail3 = new ModelRenderer(this, 0, 5);
        tail3.addBox(-2, -2, 0, 4, 4, 4);
        tail3.setRotationPoint(0, 0, 1.5F);
        setRotationDegrees(tail3, -15, 0, 0);
        tail2.addChild(tail3);

        tail4 = new ModelRenderer(this, 0, 13);
        tail4.addBox(-2.5F, -2.5F, 0, 5, 5, 8);
        tail4.setRotationPoint(0, 0, 3F);
        setRotationDegrees(tail4, -25, 0, 0);
        tail3.addChild(tail4);

        tail5 = new ModelRenderer(this, 0, 26);
        tail5.addBox(-2, -2, 0, 4, 4, 2);
        tail5.setRotationPoint(0, 0, 7.5F);
        setRotationDegrees(tail5, 15, 0, 0);
        tail4.addChild(tail5);

        tail6 = new ModelRenderer(this, 12, 26);
        tail6.addBox(-1.5F, -1.5F, 0, 3, 3, 2);
        tail6.setRotationPoint(0, 0, 1.5F);
        setRotationDegrees(tail6, 15, 0, 0);
        tail5.addChild(tail6);
    }

    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entity) {
        float angle = MathHelper.cos((entity.hashCode() + System.nanoTime() / 100000000F) / 5F) / 3.5F;
        //float angle = MathHelper.cos(p_78087_7_.ticksExisted / 10F) / 3.5F;
        this.tailBase.rotateAngleY = angle / 8F;
        this.tail2.rotateAngleY = angle / 7F;
        this.tail3.rotateAngleY = angle / 6F;
        this.tail4.rotateAngleY = angle / 5.5F;
        this.tail5.rotateAngleY = angle / 4.5F;
        this.tail6.rotateAngleY = angle / 3.5F;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    private void setRotationDegrees(ModelRenderer model, float x, float y, float z) {
        setRotation(model, (float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
    }

    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float mult) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            double d0 = player.posX - player.prevPosX;
            double d2 = player.posY - player.prevPosY;
            double d3 = d0 * d0 + d2 * d2;

            setRotationDegrees(this.tailBase, -15F, 0F, 0F);
            setRotationDegrees(this.tail2, -15F, 0F, 0F);
            setRotationDegrees(this.tail3, -15F, 0F, 0F);
            setRotationDegrees(this.tail4, -15F, 0F, 0F);
            setRotationDegrees(this.tail5, 15F, 0F, 0F);
            setRotationDegrees(this.tail6, 15F, 0F, 0F);
        }

        super.render(entity, par2, par3, par4, par5, par6, mult);
        this.setRotationAngles(par2, par3, par4, par5, par6, mult, entity);

        tailBase.render(mult);
    }
}