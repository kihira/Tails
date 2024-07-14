package uk.kihira.tails.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import uk.kihira.tails.common.Tails;

public class FoxTailModel extends PartModel
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Tails.MOD_ID, "foxtail"), "main");
    private final ModelPart tailBase;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;

    public FoxTailModel(ModelPart root)
    {
        super(RenderType::entityCutoutNoCull);
        this.tailBase = root.getChild("tailBase");
        this.tail1 = tailBase.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.tail3 = tail2.getChild("tail3");
        this.tail4 = tail3.getChild("tail4");
        this.tail5 = tail4.getChild("tail5");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition tailBase = partdefinition.addOrReplaceChild("tailBase", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition tail1 = tailBase.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(10, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.5F, -0.2618F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 5).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.5F, -0.2618F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(0, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.4363F, 0.0F, 0.0F));

        PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 7.4F, 0.2618F, 0.0F, 0.0F));

        PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(12, 26).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.4F, 0.2618F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        tailBase.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(Entity entity, float pLimbSwing, float pLimbSwingAmount, float partialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        var timestep = getAnimationTime(4000F, entity);
        var yOffset = 1f;
        var xOffset = 1f;
        var xAngleOffset = 0f;
        var yAngleOffset = 0f;
        var zAngleOffset = 0f;
        var yAngleMultiplier = 1f; //Used to suppress sway when running
        if (entity instanceof AbstractClientPlayer player)
        {
            var angles = getMotionAngles(player, partialTick);
            xAngleOffset = angles[0];
            yAngleOffset = angles[1];
            zAngleOffset = angles[2];

            xAngleOffset = Mth.clamp(xAngleOffset * 0.6f, -1f, 0.45f);
            zAngleOffset = Mth.clamp(zAngleOffset, -0.5f, 0.5f);
            yAngleMultiplier = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running
        }

        this.tailBase.setRotation(xAngleOffset, (((-zAngleOffset / 2F) + (Mth.cos(timestep + yOffset) / 8F)) * yAngleMultiplier) + yAngleOffset, -zAngleOffset / 8F);
        this.tail1.setRotation(-0.2617993877991494f + xAngleOffset + Math.abs(zAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 1 + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);
        this.tail2.setRotation(-0.2617993877991494f + (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 1.5F + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);
        this.tail3.setRotation(-0.4363323129985824f + (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 2 + yOffset) / 20F) * yAngleMultiplier, -zAngleOffset / 20F);
        this.tail4.setRotation(0.2617993877991494f - (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 3 + yOffset) / 8F) * yAngleMultiplier, 0F);
        this.tail5.setRotation(0.2617993877991494f - (xAngleOffset / 2.5F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 4 + yOffset) / 8F) * yAngleMultiplier, 0F);
    }
}