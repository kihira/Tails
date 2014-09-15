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

public class ModelCatTail extends ModelTailBase {

    ModelRenderer tailBase;
    ModelRenderer tail1;
    ModelRenderer tail2;
    ModelRenderer tail3;
    ModelRenderer tail4;
    ModelRenderer tail5;

    public ModelCatTail() {
        textureWidth = 64;
        textureHeight = 32;

        tailBase = new ModelRenderer(this, 0, 0);
        tailBase.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
        tailBase.setRotationPoint(0F, 0F, 0F);
        setRotationDegrees(tailBase, 0F, 0F, 0F);

        tail1 = new ModelRenderer(this, 0, 3);
        tail1.addBox(-0.5F, -0.5F, 0F, 1, 1, 3);
        tail1.setRotationPoint(0F, 0F, 1.75F);
        setRotationDegrees(tail1, 0F, 0F, 0F);

        tail2 = new ModelRenderer(this, 0, 7);
        tail2.addBox(-0.5F, -0.5F, 0F, 1, 1, 6);
        tail2.setRotationPoint(0F, 0F, 2.75F);
        setRotationDegrees(tail2, 0F, 0F, 0F);

        tail3 = new ModelRenderer(this, 0, 14);
        tail3.addBox(-0.5F, -0.5F, 0F, 1, 1, 3);
        tail3.setRotationPoint(0F, 0F, 5.75F);
        setRotationDegrees(tail3, 0F, 0F, 0F);

        tail4 = new ModelRenderer(this, 0, 18);
        tail4.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
        tail4.setRotationPoint(0F, 0F, 2.75F);
        setRotationDegrees(tail4, 0F, 0F, 0F);

        tail5 = new ModelRenderer(this, 0, 21);
        tail5.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
        tail5.setRotationPoint(0F, 0F, 1.75F);
        setRotationDegrees(tail5, 0F, 0F, 0F);

        tail4.addChild(tail5);
        tail3.addChild(tail4);
        tail2.addChild(tail3);
        tail1.addChild(tail2);
        tailBase.addChild(tail1);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float partialTicks, Entity entity) {
        float seed = this.getAnimationTime(6000, entity);

        setRotationRadians(tailBase, Math.toRadians(-30F), Math.cos(seed - 1) / 8F, 0F);
        setRotationRadians(tail1, Math.toRadians(-30F), Math.cos(seed - 2) / 8F, 0F);
        setRotationRadians(tail2, Math.toRadians(-30F), Math.cos(seed - 3) / 8F, 0F);
        setRotationRadians(tail3, Math.toRadians(20F) + (float) Math.cos(seed - 4) / 6F, Math.cos(seed - 4) / 8F, Math.cos(seed - 4) / 8F);
        setRotationRadians(tail4, Math.toRadians(50F) + (float) Math.cos(seed - 5) / 8F, Math.cos(seed - 5) / 8F, Math.cos(seed - 5) / 8F);
        setRotationRadians(tail5, Math.toRadians(50F) + (float) Math.cos(seed - 6) / 6F, Math.cos(seed - 6) / 8F, Math.cos(seed - 6) / 8F);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        setRotationAngles(0, 0, 0, 0, subtype, partialTicks, theEntity);
        tailBase.render(0.0625F);
    }
}
