package com.akashiro.tails.client.render;

import com.akashiro.tails.client.model.tail.ModelCatTail;
import com.akashiro.tails.client.model.tail.ModelDevilTail;
import com.akashiro.tails.client.model.tail.ModelDragonTail;
import com.akashiro.tails.common.data.PartInfo;
import com.akashiro.tails.common.data.PartsData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;

public class PlayerRenderHelper implements IRenderHelper {

    @Override
    public void onPreRenderPart(PoseStack poseStack,
                                LivingEntity entity,
                                RenderPart renderPart,
                                PartInfo info,
                                double x,
                                double y,
                                double z) {
        if (info.partType == PartsData.PartType.EARS
                || info.partType == PartsData.PartType.MUZZLE
                || info.partType == PartsData.PartType.WINGS) {
            return;
        }

        if (renderPart.modelPart instanceof ModelDragonTail) {
            if (entity.isCrouching()) poseStack.translate(0F, 0.82F, 0F);
            else poseStack.translate(0F, 0.68F, 0.1F);
            poseStack.scale(0.8F, 0.8F, 0.8F);
        } else if (renderPart.modelPart instanceof ModelCatTail
                || renderPart.modelPart instanceof ModelDevilTail) {
            if (entity.isCrouching()) poseStack.translate(0F, 0.82F, 0F);
            else poseStack.translate(0F, 0.65F, 0.1F);
            poseStack.scale(0.9F, 0.9F, 0.9F);
        } else {
            if (entity.isCrouching()) poseStack.translate(0F, 0.82F, 0F);
            else poseStack.translate(0F, 0.65F, 0.1F);
            poseStack.scale(0.8F, 0.8F, 0.8F);
        }
    }
}
