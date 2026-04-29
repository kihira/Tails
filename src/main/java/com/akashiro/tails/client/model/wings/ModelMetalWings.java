/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package com.akashiro.tails.client.model.wings;

import com.akashiro.tails.client.model.ModelPartBase;
import com.akashiro.tails.client.model.LegacyModelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;


public class ModelMetalWings extends ModelPartBase {

    final LegacyModelRenderer wing;

    public ModelMetalWings() {
        textureWidth = 64;
        textureHeight = 32;

        wing = new LegacyModelRenderer(this, 0, 0);
        wing.addBox(0F, 0F, 0.5F, 20, 29, 1);
        wing.setTextureSize(32, 32);
        wing.mirror = true;
    }

    public void render(LivingEntity theEntity, int subtype, float partialTicks) {
        translate(0, -7F * SCALE, 1F * SCALE * 2);

        rotate(90, 0, 1, 0);
        rotate(90, 0, 0, 1);

        boolean isFlying = (theEntity instanceof Player player
                && player.getAbilities().flying
                && !theEntity.onGround()) || theEntity.fallDistance > 0F;
        float timestep = getAnimationTime(isFlying ? 500 : 6000, theEntity);
        float angle = (float) Math.sin(timestep) * (isFlying ? 20F : 6F);

        translate(0F, -0.5F * SCALE, 0F);

        pushMatrix();
        translate(0F, 0F, 2F * SCALE);
        rotate(30F - angle, 1F, 0F, 0F);
        translate(0F, 0F, -1F * SCALE);
        wing.render(SCALE);
        popMatrix();

        pushMatrix();
        translate(0F, 0F, -2F * SCALE);
        rotate(-30F + angle, 1F, 0F, 0F);
        translate(0F, 0F, -1F * SCALE);
        wing.render(SCALE);
        translate(0F, 0F, 1F * SCALE);
        popMatrix();
    }
}
