/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model.wings;

import kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class ModelMetalWings extends ModelPartBase {

    ModelRenderer wing;

    public ModelMetalWings() {
        textureWidth = 64;
        textureHeight = 32;

        wing = new ModelRenderer(this, 0, 0);
        wing.addBox(0F, 0F, 0.5F, 20, 29, 1);
        wing.setTextureSize(32, 32);
        wing.mirror = true;
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        GL11.glTranslatef(0, -7F * SCALE, 1F * SCALE * 2);

        GL11.glRotatef(90, 0, 1, 0);
        GL11.glRotatef(90, 0, 0, 1);

        float angle = getWingAngle(false, 40, 4000, 250, theEntity);

        GL11.glTranslatef(0F, -0.5F * SCALE, 0F);

        GL11.glPushMatrix();
        GL11.glTranslatef(0F, 0F, 2F * SCALE);
        GL11.glRotatef(40F - angle, 1F, 0F, 0F);
        GL11.glTranslatef(0F, 0F, -1F * SCALE);
        wing.render(SCALE);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(0F, 0F, -2F * SCALE);
        GL11.glRotatef(-40F + angle, 1F, 0F, 0F);
        GL11.glTranslatef(0F, 0F, -1F * SCALE);
        wing.render(SCALE);
        GL11.glTranslatef(0F, 0F, 1F * SCALE);
        GL11.glPopMatrix();
    }

    protected float getWingAngle(boolean isFlying, float maxAngle, int totalTime, int flyingTime, EntityLivingBase theEntity) {
        float angle;

        int flapTime = totalTime;
        if (isFlying) {
            flapTime = flyingTime;
        }

        float deltaTime = getAnimationTime(flapTime, theEntity.hashCode());

        if (deltaTime <= 0.5F) {
            angle = sigmoid(-4 + ((deltaTime * 2) * 8));
        } else {
            angle = 1 - sigmoid(-4 + (((deltaTime * 2) - (1)) * 8));
        }
        angle *= maxAngle;

        return angle;
    }

    private static float sigmoid(double value) {
        return 1F / (1F + (float) Math.exp(-value));
    }

    private float getAnimationTime(int totalTime, int offset) {
        float time = (System.currentTimeMillis() + offset) % totalTime;
        return time / totalTime;
    }
}
