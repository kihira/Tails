package com.akashiro.tails.client.render;

import com.akashiro.tails.Tails;
import com.akashiro.tails.client.PartRegistry;
import com.akashiro.tails.common.data.PartInfo;
import com.akashiro.tails.common.data.PartsData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.UUID;

public class LayerPart extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final PartsData.PartType partType;
    private final ModelPart attachPoint;

    public LayerPart(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
                     PartsData.PartType partType,
                     ModelPart attachPoint) {
        super(renderer);
        this.partType = partType;
        this.attachPoint = attachPoint;
    }

    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight,
                       AbstractClientPlayer player,
                       float limbSwing,
                       float limbSwingAmount,
                       float partialTicks,
                       float ageInTicks,
                       float netHeadYaw,
                       float headPitch) {
        if (player.isInvisible()) return;

        UUID uuid = player.getUUID();
        if (!Tails.PROXY.hasPartsData(uuid)) return;

        PartsData data = Tails.PROXY.getPartsData(uuid);
        if (!data.hasPart(partType)) return;

        PartInfo info = data.getPartInfo(partType);
        if (info == null || !info.hasPart) return;

        RenderPart renderPart = PartRegistry.getRenderPart(partType, info.typeid);
        if (renderPart == null) return;

        poseStack.pushPose();

        if (partType == PartsData.PartType.EARS || partType == PartsData.PartType.MUZZLE) {
            if (player.isCrouching()) {
                poseStack.translate(0F, 0.2F, 0F);
            }
        }

        attachPoint.translateAndRotate(poseStack);
        renderPart.render(
                poseStack, bufferSource, packedLight, player, info, 0D, 0D, 0D, partialTicks);

        poseStack.popPose();
    }
}
