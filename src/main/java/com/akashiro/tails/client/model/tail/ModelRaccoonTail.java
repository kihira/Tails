/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package com.akashiro.tails.client.model.tail;

import com.akashiro.tails.client.model.ModelPartBase;
import com.akashiro.tails.client.model.LegacyModelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;

public class ModelRaccoonTail extends ModelPartBase {

    private final LegacyModelRenderer tailBase;
    private final LegacyModelRenderer tail1;
    private final LegacyModelRenderer tail2;

    public ModelRaccoonTail() {
        this.tailBase = new LegacyModelRenderer(this, 12, 16);
        this.tailBase.addBox(-1F, -1F, 0F, 2, 2, 2);
        this.tailBase.setRotationPoint(0F, 0F, 0F);

        this.tail1 = new LegacyModelRenderer(this, 0, 16);
        this.tail1.addBox(-1.5F, -1.5F, 0F, 3, 3, 3);
        this.tail1.setRotationPoint(0F, 0F, 1F);
        this.setRotationDegrees(this.tail1, -40F, 0F, 0F);

        this.tail2 = new LegacyModelRenderer(this, 0, 0);
        this.tail2.addBox(-2F, -2F, 0F, 4, 4, 12);
        this.tail2.setRotationPoint(0F, 0F, 2F);
        this.setRotationDegrees(this.tail2, -30F, 0F, 0F);

        LegacyModelRenderer tailTip = new LegacyModelRenderer(this, 0, 22);
        tailTip.addBox(-1.5F, -1.5F, 0F, 3, 3, 1);
        tailTip.setRotationPoint(0F, 0F, 12F);

        this.tail2.addChild(tailTip);
        this.tail1.addChild(this.tail2);
        this.tailBase.addChild(this.tail1);
    }

        public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float partialTicks, Entity entity) {
        float timestep = getAnimationTime(8000, entity);
        double xAngleOffset = 0;
        double yAngleOffset = 0;
        double zAngleOffset = 0;
        double yAngleMultiplier = 1; //Used to suppress sway when running

        if (!entity.isPassenger()) {
            if (entity instanceof Player) {
                double[] angles = getMotionAngles((Player) entity, partialTicks);

                xAngleOffset = angles[0];
                yAngleOffset = angles[1];
                zAngleOffset = angles[2];
                yAngleMultiplier = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running

                xAngleOffset = Mth.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
                zAngleOffset = Mth.clamp(zAngleOffset * 0.5D, -0.5D, 0.5D);
            }
        }
        //Mounted
        else {
            xAngleOffset = Math.toRadians(20F);
            yAngleMultiplier = 0.2F;
        }

        setRotationRadians(tailBase, xAngleOffset, (-zAngleOffset + Math.cos(timestep - 1) / 15F + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4F);
        setRotationRadians(tail1, Math.toRadians(-40F) + xAngleOffset, (-zAngleOffset + Math.cos(timestep - 1) / 15F + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4F);
        setRotationRadians(tail2, Math.toRadians(-30F) + xAngleOffset, (-zAngleOffset + Math.cos(timestep - 1) / 15F + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4F);
    }

        public void render(LivingEntity theEntity, int subtype, float partialTicks) {
        this.setRotationAngles(0, 0, 0, 0, 0, partialTicks, theEntity);

        this.tailBase.render(0.0625F);
    }
}