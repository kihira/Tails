package uk.kihira.tails.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.joml.Math;

public class RacoonTailModel extends PartModel
{
	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tailTip;

	public RacoonTailModel(ModelPart root)
	{
		super(root);
		this.tailBase = root.getChild("body").getChild("tailBase");
		this.tail1 = this.tailBase.getChild("tail1");
		this.tail2 = this.tail1.getChild("tail2");
		this.tailTip = this.tail2.getChild("tailTip");
	}

	public static LayerDefinition createModelLayer()
	{
		// Create the mesh definition based on the player model so we can get the correct rotations etc
		MeshDefinition meshdefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
		PartDefinition body = meshdefinition.getRoot().clearRecursively().getChild("body");

		PartDefinition tailBase = body.addOrReplaceChild("tailBase",
				CubeListBuilder.create().texOffs(12, 16).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition tail1 = tailBase.addOrReplaceChild("tail1",
				CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, CubeDeformation.NONE),
				PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -0.6981F, 0.0F, 0.0F));

		PartDefinition tail2 = tail1.addOrReplaceChild("tail2",
				CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 12.0F, CubeDeformation.NONE),
				PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition tailTip = tail2.addOrReplaceChild("tailTip",
				CubeListBuilder.create().texOffs(0, 22).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE),
				PartPose.offset(0.0F, 0.0F, 12.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(AvatarRenderState renderState)
	{
		super.setupAnim(renderState);

		float timestep = getAnimationTime(8000, renderState.id);
		float xAngleOffset = 0;
		float yAngleOffset = 0;
		float zAngleOffset = 0;
		float yAngleMultiplier = 1; //Used to suppress sway when running

		if (!renderState.isPassenger)
		{
/*			if (entity instanceof EntityPlayer) {
				double[] angles = getMotionAngles((EntityPlayer) entity, partialTicks);

				xAngleOffset = angles[0];
				yAngleOffset = angles[1];
				zAngleOffset = angles[2];
				yAngleMultiplier = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running

				xAngleOffset = MathHelper.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
				zAngleOffset = MathHelper.clamp(zAngleOffset * 0.5D, -0.5D, 0.5D);
			}*/
		}
		//Mounted
		else
		{
			xAngleOffset = Math.toRadians(20F);
			yAngleMultiplier = 0.2F;
		}

		this.tailBase.setRotation(xAngleOffset, (-zAngleOffset + Math.cos(timestep - 1) / 15F + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4F);
		this.tail1.setRotation(Math.toRadians(-40F) + xAngleOffset, (-zAngleOffset + Math.cos(timestep - 1) / 15F + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4F);
		this.tail2.setRotation(Math.toRadians(-30F) + xAngleOffset, (-zAngleOffset + Math.cos(timestep - 1) / 15F + yAngleOffset) * yAngleMultiplier, -zAngleOffset / 4F);
	}
}