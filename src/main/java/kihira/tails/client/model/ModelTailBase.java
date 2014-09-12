/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

/**
 * A base class that all tails extend
 */
public abstract class ModelTailBase extends ModelBase {

    /**
     * Renders the tail with the optional parts list provided
     * @param theEntity The owner of the tail
     * @param subtype The subtype
     * @param partialTicks
     */
    public abstract void render(EntityLivingBase theEntity, int subtype, float partialTicks);

    /**
     * Sets the rotation on a model where the provided params are in radians
     * @param model The model
     * @param x The x angle
     * @param y The y angle
     * @param z The z angle
     */
    protected void setRotationRadians(ModelRenderer model, double x, double y, double z) {
        model.rotateAngleX = (float) x;
        model.rotateAngleY = (float) y;
        model.rotateAngleZ = (float) z;
    }

    /**
     * Sets the rotation on a model where the provided params are in degrees
     * @param model The model
     * @param x The x angle
     * @param y The y angle
     * @param z The z angle
     */
    protected void setRotationDegrees(ModelRenderer model, float x, float y, float z) {
        this.setRotationRadians(model, (float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
    }

    protected float getAnimationTime(double cycleTime, Entity entity) {
        return (float) ((((entity.hashCode() + System.currentTimeMillis()) % cycleTime) / cycleTime) * 2F * Math.PI);
    }

    protected double[] getMotionAngles(EntityPlayer player, double partialTicks) {
        double x = player.field_71091_bM + (player.field_71094_bP - player.field_71091_bM) * partialTicks - (player.prevPosX + (player.posX - player.prevPosX) * partialTicks);
        double y = player.field_71096_bN + (player.field_71095_bQ - player.field_71096_bN) * partialTicks - (player.prevPosY + (player.posY - player.prevPosY) * partialTicks);
        double z = player.field_71097_bO + (player.field_71085_bR - player.field_71097_bO) * partialTicks - (player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks);
        float renderYawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * (float) partialTicks;
        double d1 = (double) MathHelper.sin(renderYawOffset * (float) Math.PI / 180F);
        double d2 = (double)(-MathHelper.cos(renderYawOffset * (float)Math.PI / 180F));
        float f5 = MathHelper.clamp_float((float) y * 10F, -6F, 32F);
        float f6 = (float)(x * d1 + z * d2) * 100F;
        float f7 = (float)(x * d2 - z * d1) * 100F;

        if (f6 < 0F) f6 = 0F;

        float cameraYaw = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * (float) partialTicks;
        f5 += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * (float) partialTicks) * 6F) * 32F * cameraYaw;

        return new double[] {Math.toRadians(f6 / 2.5F + f5), Math.toRadians(-f7 / 20F), Math.toRadians(f7 / 2F)};
    }
}
