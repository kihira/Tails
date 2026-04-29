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


public class ModelFluffyTail extends ModelPartBase {
    private final LegacyModelRenderer tailBase;
    private final LegacyModelRenderer tail1;
    private final LegacyModelRenderer tail2;
    private final LegacyModelRenderer tail3;
    private final LegacyModelRenderer tail4;
    private final LegacyModelRenderer tail5;

    public ModelFluffyTail() {
        this.tailBase = new LegacyModelRenderer(this);
        this.tailBase.addBox(-1, -1, 0, 2, 2, 3);
        this.tailBase.setRotationPoint(0, 0, 0);
        this.setRotationDegrees(this.tailBase, -15F, 0, 0);

        this.tail1 = new LegacyModelRenderer(this, 10, 0);
        this.tail1.addBox(-1.5F, -1.5F, 0, 3, 3, 2);
        this.tail1.setRotationPoint(0, 0, 1.5F);
        this.setRotationDegrees(this.tail1, -15F, 0, 0);

        this.tail2 = new LegacyModelRenderer(this, 0, 5);
        this.tail2.addBox(-2, -2, 0, 4, 4, 4);
        this.tail2.setRotationPoint(0, 0, 1.5F);
        this.setRotationDegrees(this.tail2, -15F, 0, 0);

        this.tail3 = new LegacyModelRenderer(this, 0, 13);
        this.tail3.addBox(-2.5F, -2.5F, 0, 5, 5, 8);
        this.tail3.setRotationPoint(0, 0, 3F);
        this.setRotationDegrees(this.tail3, -25F, 0, 0);

        this.tail4 = new LegacyModelRenderer(this, 0, 26);
        this.tail4.addBox(-2, -2, 0, 4, 4, 2);
        this.tail4.setRotationPoint(0, 0, 7.4F);
        this.setRotationDegrees(this.tail4, 15F, 0, 0);

        this.tail5 = new LegacyModelRenderer(this, 12, 26);
        this.tail5.addBox(-1.5F, -1.5F, 0, 3, 3, 2);
        this.tail5.setRotationPoint(0, 0, 1.4F);
        this.setRotationDegrees(this.tail5, 15F, 0, 0);

        this.tail4.addChild(this.tail5);
        this.tail3.addChild(this.tail4);
        this.tail2.addChild(this.tail3);
        this.tail1.addChild(this.tail2);
        this.tailBase.addChild(this.tail1);
    }

    public void setRotationAngles(int subtype, float timestep, float yOffset, float xOffset, float xAngle, float yAngle, float partialTicks, Entity entity) {
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

                switch (subtype) {
                    //Fox Tail
                    case 0:
                        xAngleOffset = Mth.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
                        zAngleOffset = Mth.clamp(zAngleOffset, -0.5D, 0.5D);
                        break;
                    //Twin Tails
                    case 1:
                        xAngleOffset = Mth.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
                        zAngleOffset = Mth.clamp(zAngleOffset, -0.5D, 0.5D);
                        break;
                    //Nine tails
                    case 2:
                        zAngleOffset = Mth.clamp(zAngleOffset * 0.5D, -1D, 0.5D);
                        xAngleOffset = Mth.clamp(xAngleOffset * 0.25D, -1D, 0.2D);
                        xAngleOffset += (Math.cos(timestep + xOffset) / 30F);
                        break;
                }
                yAngleMultiplier = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running
            }
        }
        //Mounted
        else {
            switch (subtype) {
                //Fox Tail
                case 0:
                    xAngleOffset = Math.toRadians(22F);
                    yAngleMultiplier = 0.5F;
                    break;
                //Twin Tails
                case 1:
                    xAngleOffset = Math.toRadians(20F);
                    yAngleMultiplier = 0.5F;
                    break;
                //Nine tails
                case 2:
                    xAngleOffset = Math.toRadians(15F);
                    yAngleMultiplier = 0.75F;
                    break;
            }
        }

        this.setRotationRadians(this.tailBase, xAngle + xAngleOffset, (((-zAngleOffset / 2F) + yAngle + (Math.cos(timestep + yOffset) / 8F)) * yAngleMultiplier) + yAngleOffset, -zAngleOffset / 8F);
        this.setRotationRadians(this.tail1, -0.2617993877991494 + xAngleOffset + Math.abs(zAngleOffset / 2F), ((-zAngleOffset / 2F) + Math.cos(timestep - 1 + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);
        this.setRotationRadians(this.tail2, -0.2617993877991494 + (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Math.cos(timestep - 1.5F + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);
        this.setRotationRadians(this.tail3, -0.4363323129985824 + (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Math.cos(timestep - 2 + yOffset) / 20F) * yAngleMultiplier, -zAngleOffset / 20F);
        this.setRotationRadians(this.tail4, 0.2617993877991494 - (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Math.cos(timestep - 3 + yOffset) / 8F) * yAngleMultiplier, 0F);
        this.setRotationRadians(this.tail5, 0.2617993877991494 - (xAngleOffset / 2.5F), ((-zAngleOffset / 2F) + Math.cos(timestep - 4 + yOffset) / 8F) * yAngleMultiplier, 0F);
    }

        public void render(LivingEntity theEntity, int subtype, float partialTicks) {
        float timestep = getAnimationTime(4000F, theEntity);

        if (subtype == 0) {
            this.setRotationAngles(0, timestep, 1F, 1F, 0, 0, partialTicks, theEntity);
            rotate(-20F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);
        }
        else if (subtype == 1) {
            this.setRotationAngles(1, timestep, 1F, 1F, 0F, (float) Math.toRadians(40F), partialTicks, theEntity);
            rotate(-20F, 1F, 0F, 0F);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(1, timestep, 1.4F, 0F, 0F, (float) Math.toRadians(-40F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);
        }
        else if (subtype == 2) {
            timestep = getAnimationTime(6500F, theEntity);

            this.setRotationAngles(2, timestep, -1.5F, 2.5F, 0F, 0, partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, timestep, -1.3F, 1.6F, 0, (float) Math.toRadians(30F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, timestep, -1.1F, 0.7F, 0, (float) Math.toRadians(-30F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, timestep, -1.2F, 2.6F, (float) Math.toRadians(20F), (float) Math.toRadians(-15F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, timestep, -0.9F, 1.1F, (float) Math.toRadians(20F), (float) Math.toRadians(15F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, timestep, -0.8F, 2F, (float) Math.toRadians(20F), (float) Math.toRadians(45F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, timestep, -1.25F, 0.6F, (float) Math.toRadians(20F), (float) Math.toRadians(-45F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, timestep, -1.4F, 0.9F, (float) Math.toRadians(45F), (float) Math.toRadians(15F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);

            this.setRotationAngles(2, timestep, -1.1F, 1.6F, (float) Math.toRadians(45F), (float) Math.toRadians(-15F), partialTicks, theEntity);
            this.tailBase.render(0.0625F);
        }
    }
}