package com.akashiro.tails.client.render;

import com.akashiro.tails.client.model.ModelPartBase;
import com.akashiro.tails.common.data.PartInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RenderWings extends RenderPart {

    public RenderWings(String name, int subTypes, String modelAuthor,
                       ModelPartBase modelPart, String[] textureNames) {
        super(name, subTypes, modelPart, modelAuthor, textureNames);
    }

    @Override
    protected void doRender(PoseStack poseStack,
                            MultiBufferSource buffer,
                            int packedLight,
                            LivingEntity entity,
                            PartInfo info,
                            float partialTicks) {
        ResourceLocation texture = info.getTexture();
        if (texture == null) return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        boolean isFlying = (entity instanceof Player player && player.getAbilities().flying && !entity.onGround())
                || entity.fallDistance > 0F;
        float timestep = ModelPartBase.getAnimationTime(isFlying ? 500 : 6500, entity);
        float angle = (float) Math.sin(timestep) * (isFlying ? 24F : 4F);
        float scale = info.subid == 1 ? 1F : 2F;

        poseStack.translate(0F, -(scale * 8F) * ModelPartBase.SCALE + (info.subid == 1 ? 0.1F : 0F), 0.1F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90F));
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.1F, -0.4F * ModelPartBase.SCALE, -0.025F);

        poseStack.pushPose();
        poseStack.translate(0F, 0F, 1F * ModelPartBase.SCALE);
        poseStack.mulPose(Axis.XP.rotationDegrees(30F - angle));
        drawWingQuad(poseStack, consumer, packedLight);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0F, 0.3F * ModelPartBase.SCALE, 0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-30F + angle));
        drawWingQuad(poseStack, consumer, packedLight);
        poseStack.popPose();
    }

    private static void drawWingQuad(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        PoseStack.Pose pose = poseStack.last();

        consumer.vertex(pose.pose(), 0F, 1F, 0F)
                .color(255, 255, 255, 255)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0F, 0F, 1F)
                .endVertex();
        consumer.vertex(pose.pose(), 1F, 1F, 0F)
                .color(255, 255, 255, 255)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0F, 0F, 1F)
                .endVertex();
        consumer.vertex(pose.pose(), 1F, 0F, 0F)
                .color(255, 255, 255, 255)
                .uv(1F, 1F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0F, 0F, 1F)
                .endVertex();
        consumer.vertex(pose.pose(), 0F, 0F, 0F)
                .color(255, 255, 255, 255)
                .uv(0F, 1F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0F, 0F, 1F)
                .endVertex();
    }
}
