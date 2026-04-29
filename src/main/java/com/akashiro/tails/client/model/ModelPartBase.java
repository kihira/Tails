package com.akashiro.tails.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;

public abstract class ModelPartBase extends Model {

    public static final float SCALE = 0.0625f;

    protected int textureWidth = 64;
    protected int textureHeight = 32;

    private static final ThreadLocal<RenderContext> CONTEXT = new ThreadLocal<>();

    protected ModelPartBase() {
        super(RenderType::entityCutoutNoCull);
    }

    public final void render(PoseStack poseStack,
                             VertexConsumer consumer,
                             int packedLight,
                             LivingEntity entity,
                             int subtype,
                             float partialTicks) {
        RenderContext previous = CONTEXT.get();
        CONTEXT.set(new RenderContext(poseStack, consumer, packedLight));
        try {
            render(entity, subtype, partialTicks);
        } finally {
            if (previous == null) {
                CONTEXT.remove();
            } else {
                CONTEXT.set(previous);
            }
        }
    }

    protected abstract void render(LivingEntity entity, int subtype, float partialTicks);

    protected void setRotationRadians(LegacyModelRenderer model, double x, double y, double z) {
        model.rotateAngleX = (float) x;
        model.rotateAngleY = (float) y;
        model.rotateAngleZ = (float) z;
    }

    protected void setRotationDegrees(LegacyModelRenderer model, float x, float y, float z) {
        setRotationRadians(model, Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
    }

    public static float getAnimationTime(double cycleTime, Entity entity) {
        return (float) ((((entity.hashCode() + System.currentTimeMillis()) % cycleTime) / cycleTime) * 2F * Math.PI);
    }

    protected double[] getMotionAngles(Player player, double partialTicks) {
        double xMotion = player.xCloakO + (player.xCloak - player.xCloakO) * partialTicks - (player.xo + (player.getX() - player.xo) * partialTicks);
        double yMotion = player.yCloakO + (player.yCloak - player.yCloakO) * partialTicks - (player.yo + (player.getY() - player.yo) * partialTicks);
        double zMotion = player.zCloakO + (player.zCloak - player.zCloakO) * partialTicks - (player.zo + (player.getZ() - player.zo) * partialTicks);
        float bodyYaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * (float) partialTicks;
        double bodyYawSin = Math.sin(bodyYaw * (float) Math.PI / 180F);
        double bodyYawCos = -Math.cos(bodyYaw * (float) Math.PI / 180F);
        float xOffset = Mth.clamp((float) yMotion * 10F, -6F, 32F);
        float f1 = (float) (xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F;
        float f2 = (float) (xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F;

        if (f1 < 0F) {
            f1 = 0F;
        }

        return new double[]{
                Math.toRadians(f1 / 2.5F + (xOffset + getTailBob(player, (float) partialTicks))),
                Math.toRadians(-f2 / 20F),
                Math.toRadians(f2 / 2F)
        };
    }

    protected float getTailBob(Player player, float partialTicks) {
        float cameraYaw = player.oBob + (player.bob - player.oBob) * partialTicks;
        return Mth.sin((player.walkDistO + (player.walkDist - player.walkDistO) * partialTicks) * 6F) * 12F * cameraYaw;
    }

    protected final void pushMatrix() {
        requireContext().poseStack.pushPose();
    }

    protected final void popMatrix() {
        requireContext().poseStack.popPose();
    }

    protected final void translate(float x, float y, float z) {
        requireContext().poseStack.translate(x, y, z);
    }

    protected final void scale(float x, float y, float z) {
        requireContext().poseStack.scale(x, y, z);
    }

    protected final void rotate(float degrees, float x, float y, float z) {
        PoseStack poseStack = requireContext().poseStack;
        if (x == 1F && y == 0F && z == 0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(degrees));
            return;
        }
        if (x == 0F && y == 1F && z == 0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(degrees));
            return;
        }
        if (x == 0F && y == 0F && z == 1F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(degrees));
            return;
        }
        poseStack.mulPose(new Quaternionf().rotationAxis((float) Math.toRadians(degrees), x, y, z));
    }

    static RenderContext getRenderContext() {
        return CONTEXT.get();
    }

    private static RenderContext requireContext() {
        RenderContext ctx = CONTEXT.get();
        if (ctx == null) {
            throw new IllegalStateException("ModelPartBase render context is not available.");
        }
        return ctx;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
    }

    static final class RenderContext {
        final PoseStack poseStack;
        final VertexConsumer consumer;
        final int packedLight;

        private RenderContext(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
            this.poseStack = poseStack;
            this.consumer = consumer;
            this.packedLight = packedLight;
        }
    }
}
