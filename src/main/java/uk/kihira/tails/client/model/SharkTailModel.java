package uk.kihira.tails.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.joml.Math;
import org.joml.Vector3f;
import uk.kihira.tails.client.render.LayerPart;
import uk.kihira.tails.client.render.LegacyLayerPart;

public class SharkTailModel extends PartModel
{
    private final ModelPart tailBase;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart finBase;
    private final ModelPart finBot1;
    private final ModelPart finBot2;
    private final ModelPart finBot3;
    private final ModelPart finTop1;
    private final ModelPart finTop2;
    private final ModelPart finTop3;

    public SharkTailModel(ModelPart root)
    {
        super(root);
        this.tailBase = this.partRoot.getChild("tailBase");
        this.tail1 = this.tailBase.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.finBase = this.tail3.getChild("finBase");
        this.finBot1 = this.finBase.getChild("finBot1");
        this.finBot2 = this.finBot1.getChild("finBot2");
        this.finBot3 = this.finBot2.getChild("finBot3");
        this.finTop1 = this.finBase.getChild("finTop1");
        this.finTop2 = this.finTop1.getChild("finTop2");
        this.finTop3 = this.finTop2.getChild("finTop3");
    }

    public static LayerDefinition createModelLayer()
    {
        // Create the mesh definition based on the player model so we can get the correct rotations etc
        // We create a "partRoot" which we can set for the offset/rotation/scale set by the player without needing to worry about our animations
        var meshDefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
        var emptyPlayerDefinition = meshDefinition.getRoot().clearRecursively();
        var partRoot = emptyPlayerDefinition.getChild("body").addOrReplaceChild(PART_ROOT_NAME, CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition tailBase = partRoot.addOrReplaceChild("tailBase",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.6458F, 0.0F, 0.0F));

        PartDefinition tail1 = tailBase.addOrReplaceChild("tail1",
                CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 3.5F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1.0F, -1.0F, -0.2F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.5F, 0.2785F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3",
                CubeListBuilder.create().texOffs(0, 3).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.4F, 0.2269F, 0.0F, 0.0F));

        PartDefinition finBase = tail3.addOrReplaceChild("finBase",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -2.595F, 0.0F, -3.1416F));

        PartDefinition cube_r1 = finBase.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(16, 21).addBox(-0.5F, -3.6F, -4.0F, 1.0F, 7.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        PartDefinition finBot1 = finBase.addOrReplaceChild("finBot1",
                CubeListBuilder.create().texOffs(26, 27).addBox(-1.0F, -3.0F, -2.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.5F, 0.4F, -4.0F, -0.0911F, 0.0F, 0.0F));

        PartDefinition finBot2 = finBot1.addOrReplaceChild("finBot2",
                CubeListBuilder.create().texOffs(26, 21).addBox(-1.0F, -3.0F, -3.0F, 1.0F, 3.0F, 3.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.1367F, 0.0F, 0.0F));

        PartDefinition finBot3 = finBot2.addOrReplaceChild("finBot3",
                CubeListBuilder.create().texOffs(26, 17).addBox(-1.0F, -2.0F, -2.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.1981F, 0.0F, 0.0F));

        PartDefinition finTop1 = finBase.addOrReplaceChild("finTop1",
                CubeListBuilder.create().texOffs(16, 10).addBox(-0.5F, -2.0F, -2.9F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, -6.5F, -0.1F, 0.0911F, 0.0F, 0.0F));

        PartDefinition finTop2 = finTop1.addOrReplaceChild("finTop2",
                CubeListBuilder.create().texOffs(16, 4).addBox(-1.0F, -4.0F, -2.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.5F, -2.0F, 0.1F, 0.1365F, 0.0F, 0.0F));

        PartDefinition finTop3 = finTop2.addOrReplaceChild("finTop3",
                CubeListBuilder.create().texOffs(16, 1).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.1367F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    @Override
    public void setupAnim(AvatarRenderState renderState)
    {
        super.setupAnim(renderState);

        float xAngleOffset = 0;
        float yAngleMultiplier = 1; //Used to suppress sway when running
        if (!renderState.isPassenger)
        {
/*            if (entity instanceof EntityPlayer)
            {
                float[] angles = getMotionAngles((EntityPlayer) entity, partialTicks);

                xAngleOffset = Math.clamp(angles[0] / 5F, -1F, 0.45F);
                yAngleMultiplier = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running
            }*/
        }
        //Mounted
        else
        {
            xAngleOffset = Math.toRadians(12F);
            yAngleMultiplier = 0.25F;
        }

        float timestep = getAnimationTime(3000D, renderState.id);
        this.tailBase.setRotation(-0.6522295414702809F + xAngleOffset * 4F, (Math.cos(timestep - 1) / 5F) * yAngleMultiplier, 0F);
        this.tail1.setRotation(0.0013962634015954637F + xAngleOffset * 1F, (Math.cos(timestep - 2) / 5F) * yAngleMultiplier, 0F);
        this.tail2.setRotation(0.278554548618295F - xAngleOffset * 2F, (Math.cos(timestep - 3) / 5F) * yAngleMultiplier, 0F);
        this.tail3.setRotation(0.22759093446006054F - xAngleOffset, (Math.cos(timestep - 4) / 5F) * yAngleMultiplier, 0F);
        this.finBase.setRotation(this.finBase.xRot, (Math.cos(timestep - 10) / 5F) * -yAngleMultiplier, this.finBase.zRot);
    }
}
