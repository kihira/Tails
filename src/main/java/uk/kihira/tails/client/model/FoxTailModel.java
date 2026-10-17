package uk.kihira.tails.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import uk.kihira.tails.Tails;

public class FoxTailModel extends PartModel
{
    private final ModelPart tailBase;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;

    public FoxTailModel(ModelPart root)
    {
        super(root);
        this.tailBase = this.partRoot.getChild("tailBase");
        this.tail1 = tailBase.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.tail3 = tail2.getChild("tail3");
        this.tail4 = tail3.getChild("tail4");
        this.tail5 = tail4.getChild("tail5");
    }

    public static LayerDefinition createBodyLayer()
    {
        // Create the mesh definition based on the player model so we can get the correct rotations etc
        var meshDefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
        var emptyPlayerDefinition = meshDefinition.getRoot().clearRecursively();
        var partRoot = emptyPlayerDefinition.getChild("body")
                .addOrReplaceChild(PART_ROOT_NAME,
                        CubeListBuilder.create(),
                        // TODO: This is hisotrical in the scale, location etc, but should we be scaling models?
                        PartPose.offsetAndRotation(0f, 10.4f, 1.6f, (float) Math.toRadians(-20f), 0f, 0f).withScale(0.8f)); // offset to bottom of the body

        PartDefinition tailBase = partRoot.addOrReplaceChild("tailBase",
                        CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE),
                        PartPose.ZERO);

        PartDefinition tail1 = tailBase.addOrReplaceChild("tail1",
                CubeListBuilder.create().texOffs(10, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 1.5F, -0.2618F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(0, 5).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 1.5F, -0.2618F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3",
                CubeListBuilder.create().texOffs(0, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 8.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.4363F, 0.0F, 0.0F));

        PartDefinition tail4 = tail3.addOrReplaceChild("tail4",
                CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 7.4F, 0.2618F, 0.0F, 0.0F));

        PartDefinition tail5 = tail4.addOrReplaceChild("tail5",
                CubeListBuilder.create().texOffs(12, 26).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 1.4F, 0.2618F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public void setupAnim(AvatarRenderState renderState)
    {
        super.setupAnim(renderState);

        var timestep = getAnimationTime(SLOW_SPEED, renderState.id);
        var motion = renderState.getRenderData(PartAnimation.MOTION_KEY);
        var xAngleOffset = 0f;
        var yAngleOffset = 0f;
        var zAngleOffset = 0f;
        var swayDampen = 0f;
        var swayPhaseOffset = 1f;

        if (renderState.isPassenger)
        {
            xAngleOffset = (float) Math.toRadians(22f);
            yAngleOffset = 0f;
            zAngleOffset = 0f;
            swayDampen = 0.5f;
        }
        else
        {
            xAngleOffset = Mth.clamp(motion.xAngle() * 0.6f, -1f, 0.45f);
            yAngleOffset = motion.yAngle();
            zAngleOffset = Mth.clamp(motion.zAngle(), -0.5f, 0.5f);
            swayDampen = 1f - xAngleOffset * 2f;
        }

        this.tailBase.setRotation(xAngleOffset, (((-zAngleOffset / 2f) + (Mth.cos(timestep + swayPhaseOffset) / 8f)) * swayDampen + yAngleOffset), -zAngleOffset / 8f);
        this.tail1.setRotation(-0.2617993877991494f + xAngleOffset + Math.abs(zAngleOffset / 2f), ((-zAngleOffset / 2f) + Mth.cos(timestep - 1 + swayPhaseOffset) / 8f) * swayDampen, -zAngleOffset / 8f);
        this.tail2.setRotation(-0.2617993877991494f + (xAngleOffset / 2f), ((-zAngleOffset / 2f) + Mth.cos(timestep - 1.5f + swayPhaseOffset) / 8F) * swayDampen, -zAngleOffset / 8f);
        this.tail3.setRotation(-0.4363323129985824f + (xAngleOffset / 2f), ((-zAngleOffset / 2f) + Mth.cos(timestep - 2f + swayPhaseOffset) / 20f) * swayDampen, -zAngleOffset / 20f);
        this.tail4.setRotation(0.2617993877991494f - (xAngleOffset / 2f), ((-zAngleOffset / 2f) + Mth.cos(timestep - 3f + swayPhaseOffset) / 8f) * swayDampen, 0f);
        this.tail5.setRotation(0.2617993877991494f - (xAngleOffset / 2.5f), ((-zAngleOffset / 2f) + Mth.cos(timestep - 4f + swayPhaseOffset) / 8f) * swayDampen, 0f);
    }

    private static final float HEAVY_SWAP_DAMPEN = 4f;
    private static final float MEDIUM_SWAY_DAMPEN = 3f;
    private static final float LOW_SWAY_DAMPEN = 2f;

    private static final float SLOW_SPEED = 4000f;
    private static final float AVERAGE_SPEED = 3000f;
    private static final float FAST_SPEED = 2000f;
}