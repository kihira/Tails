/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model.tail;

import kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class ModelDevilTail extends ModelPartBase {

    final ModelRenderer tailBase;
    final ModelRenderer tail1;
    final ModelRenderer tail2;
    final ModelRenderer tail3;
    final ModelRenderer tail4;
    final ModelRenderer tail5;
    final ModelRenderer tailTip;

    public ModelDevilTail() {
        tailBase = new ModelRenderer(this, 0, 0);
        tailBase.addBox(-1F, -1F, 0F, 2, 2, 2);
        tailBase.setRotationPoint(0F, 0F, 0F);
        this.setRotationDegrees(this.tailBase, -30F, 0F, 0F);

        tail1 = new ModelRenderer(this, 0, 4);
        tail1.addBox(-0.5F, -0.5F, 0F, 1, 1, 4);
        tail1.setRotationPoint(0F, 0F, 1.8F);
        this.setRotationDegrees(this.tail1, -30F, 0F, 0F);

        tail2 = new ModelRenderer(this, 0, 9);
        tail2.addBox(-0.5F, -0.5F, 0F, 1, 1, 5);
        tail2.setRotationPoint(0F, 0F, 3.8F);
        this.setRotationDegrees(this.tail2, -30F, 0F, 0F);

        tail3 = new ModelRenderer(this, 0, 15);
        tail3.addBox(-0.5F, -0.5F, 0F, 1, 1, 3);
        tail3.setRotationPoint(0F, 0F, 4.8F);
        this.setRotationDegrees(this.tail3, 20F, 0F, 0F);

        tail4 = new ModelRenderer(this, 0, 19);
        tail4.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
        tail4.setRotationPoint(0F, 0F, 2.6F);
        this.setRotationDegrees(this.tail4, 50F, 0F, 0F);

        tail5 = new ModelRenderer(this, 0, 22);
        tail5.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
        tail5.setRotationPoint(0F, 0F, 1.7F);
        this.setRotationDegrees(this.tail5, 50F, 0F, 0F);

        tailTip = new ModelRenderer(this, 12, 0);
        tailTip.addBox(-2.5F, 0F, 0F, 5, 5, 0);
        tailTip.setRotationPoint(0F, 0F, 1.8F);
        this.setRotationDegrees(this.tailTip, 120F, 0F, 0F);

        this.tail5.addChild(this.tailTip);
        this.tail4.addChild(this.tail5);
        this.tail3.addChild(this.tail4);
        this.tail2.addChild(this.tail3);
        this.tail1.addChild(this.tail2);
        this.tailBase.addChild(this.tail1);
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float partialTicks, Entity entity) {
        float seed = getAnimationTime(6000, entity);
        float xseed = getAnimationTime(12000, entity);
        double xAngleOffset = 0;
        double yAngleMultiplier = 1; //Used to suppress sway when running
        if (!entity.isRiding()) {
            if (entity instanceof EntityPlayer) {
                double[] angles = getMotionAngles((EntityPlayer) entity, partialTicks);

                xAngleOffset = MathHelper.clamp_double(angles[0] / 3.5F, -1F, 0.275D);
                yAngleMultiplier = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running
            }
        }
        //Mounted
        else {
            xAngleOffset = Math.toRadians(13F);
            yAngleMultiplier = 0.25F;
        }

        setRotationRadians(tailBase, Math.toRadians(-30F) + xAngleOffset * 2F, Math.cos(seed - 1) / 8F * yAngleMultiplier, 0F);
        setRotationRadians(tail1, Math.toRadians(-30F) + xAngleOffset * 2F, Math.cos(seed - 2) / 8F * yAngleMultiplier, 0F);
        setRotationRadians(tail2, Math.toRadians(-30F) + xAngleOffset * 2F, Math.cos(seed - 3) / 8F * yAngleMultiplier, 0F);
        setRotationRadians(tail3, Math.toRadians(20F) - (xAngleOffset * 2F) + (Math.cos(xseed - 4) / 6F * yAngleMultiplier), Math.cos(seed - 4) / 8F * yAngleMultiplier, Math.cos(xseed - 4) / 8F * yAngleMultiplier);
        setRotationRadians(tail4, Math.toRadians(50F) - (xAngleOffset * 3F) + (Math.cos(xseed - 5) / 8F * yAngleMultiplier), Math.cos(seed - 5) / 8F * yAngleMultiplier, Math.cos(xseed - 5) / 8F * yAngleMultiplier);
        setRotationRadians(tail5, Math.toRadians(50F) - (xAngleOffset * 4F) + (Math.cos(xseed - 6) / 4F  * yAngleMultiplier), Math.cos(seed - 6) / 8F * yAngleMultiplier, Math.cos(xseed - 6) / 8F * yAngleMultiplier);
        setRotationRadians(tailTip, Math.toRadians(120F) - xAngleOffset, 0F, 0F);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        this.setRotationAngles(0, 0, 0, 0, 0, partialTicks, theEntity);

        if (subtype == 1) {
            this.tailTip.isHidden = true;
        }

        this.tailBase.render(0.0625F);
        this.tailTip.isHidden = false;
    }
}
