package com.akashiro.tails.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class LegacyModelRenderer {

    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public boolean mirror;
    public boolean showModel = true;
    public boolean isHidden;

    private final ModelPartBase model;
    private int textureOffsetX;
    private int textureOffsetY;
    private float textureWidth;
    private float textureHeight;
    private final List<ModelPart.Cube> cubeList = new ArrayList<>();
    private final List<LegacyModelRenderer> childModels = new ArrayList<>();

    public LegacyModelRenderer(ModelPartBase model) {
        this(model, 0, 0);
    }

    public LegacyModelRenderer(ModelPartBase model, int textureOffsetX, int textureOffsetY) {
        this.model = model;
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
        this.textureWidth = model.textureWidth;
        this.textureHeight = model.textureHeight;
    }

    public LegacyModelRenderer setTextureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
        return this;
    }

    public LegacyModelRenderer setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }

    public LegacyModelRenderer addBox(float x, float y, float z, int width, int height, int depth) {
        return addBox(x, y, z, width, height, depth, 0F);
    }

    public LegacyModelRenderer addBox(float x, float y, float z, int width, int height, int depth, float delta) {
        cubeList.add(new ModelPart.Cube(
                textureOffsetX,
                textureOffsetY,
                x, y, z,
                width, height, depth,
                delta, delta, delta,
                mirror,
                textureWidth,
                textureHeight,
                EnumSet.allOf(Direction.class)
        ));
        return this;
    }

    public void setRotationPoint(float x, float y, float z) {
        this.rotationPointX = x;
        this.rotationPointY = y;
        this.rotationPointZ = z;
    }

    public void addChild(LegacyModelRenderer child) {
        childModels.add(child);
    }

    public void render(float scale) {
        ModelPartBase.RenderContext context = ModelPartBase.getRenderContext();
        if (context == null) {
            return;
        }
        render(context.poseStack, context.consumer, context.packedLight, OverlayTexture.NO_OVERLAY, scale);
    }

    private void render(PoseStack poseStack,
                        VertexConsumer consumer,
                        int packedLight,
                        int packedOverlay,
                        float scale) {
        if (!showModel || isHidden) {
            return;
        }

        poseStack.pushPose();
        if (offsetX != 0F || offsetY != 0F || offsetZ != 0F) {
            poseStack.translate(offsetX, offsetY, offsetZ);
        }
        poseStack.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

        if (rotateAngleZ != 0F) {
            poseStack.mulPose(Axis.ZP.rotation(rotateAngleZ));
        }
        if (rotateAngleY != 0F) {
            poseStack.mulPose(Axis.YP.rotation(rotateAngleY));
        }
        if (rotateAngleX != 0F) {
            poseStack.mulPose(Axis.XP.rotation(rotateAngleX));
        }

        if (!cubeList.isEmpty()) {
            PoseStack.Pose pose = poseStack.last();
            for (ModelPart.Cube cube : cubeList) {
                cube.compile(pose, consumer, packedLight, packedOverlay, 1F, 1F, 1F, 1F);
            }
        }

        for (LegacyModelRenderer childModel : childModels) {
            childModel.render(poseStack, consumer, packedLight, packedOverlay, scale);
        }

        poseStack.popPose();
    }
}
