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

package kihira.tails.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

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
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        float seed = (entity.hashCode() + System.nanoTime() / 100000000F) / 5F;

        this.tailBase.rotateAngleY = MathHelper.cos(seed) / 8F;
        this.tail1.rotateAngleY = MathHelper.cos(seed - 1) / 8F;
        this.tail2.rotateAngleY = MathHelper.cos(seed - 1.5F) / 8F;
        this.tail3.rotateAngleY = MathHelper.cos(seed - 2F) / 20F;
        this.tail4.rotateAngleY = MathHelper.cos(seed - 3) / 8F;
        this.tail5.rotateAngleY = MathHelper.cos(seed - 4) / 8F;
    }

    @Override
    public void render(EntityPlayer thePlayer, int subtype) {
        this.setRotationAngles(0, 0, 0, 0, 0, 0, thePlayer);

        this.tailBase.render(0.0625F);
    }
}