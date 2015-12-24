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
public abstract class ModelPartBase extends ModelBase {

    public static final float SCALE = 0.0625F;

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

    public static float getAnimationTime(double cycleTime, Entity entity) {
        //Returns between 0-360 in radians depending on far in the "cycle" we are.
        return (float) ((((entity.hashCode() + System.currentTimeMillis()) % cycleTime) / cycleTime) * 2F * Math.PI);
    }

    protected double[] getMotionAngles(EntityPlayer player, double partialTicks) {
        double xMotion = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * partialTicks - (player.prevPosX + (player.posX - player.prevPosX) * partialTicks);
        double yMotion = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * partialTicks - (player.prevPosY + (player.posY - player.prevPosY) * partialTicks); //Positive when falling, negative when climbing
        double zMotion = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * partialTicks - (player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks);
        float bodyYaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * (float) partialTicks;
        //Pretty sure renderYawOffset is actually the way the body is "pointing"
        //In degrees, not bound 0-360, be warned!
        double bodyYawSin = Math.sin(bodyYaw * (float) Math.PI / 180F);
        double bodyYawCos = -Math.cos(bodyYaw * (float) Math.PI / 180F);
        float xOffset = MathHelper.clamp_float((float) yMotion * 10F, -6F, 32F);
        float f1 = (float)(xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F;
        float f2 = (float)(xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F;

        if (f1 < 0F) f1 = 0F;

        return new double[] {Math.toRadians(f1 / 2.5F + (xOffset + getTailBob(player, (float) partialTicks))), Math.toRadians(-f2 / 20F), Math.toRadians(f2 / 2F)};
    }

    protected float getTailBob(EntityPlayer player, float partialTicks) {
        float cameraYaw = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
        return MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * partialTicks) * 6F) * 12F * cameraYaw;
    }
}
