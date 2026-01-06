package uk.kihira.tails.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.joml.Math;

public class DragonTailModel extends PartModel
{
	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;

	public DragonTailModel(ModelPart root)
	{
		super(root);
		this.tailBase = root.getChild("body").getChild("tailBase");
		this.tail1 = this.tailBase.getChild("tail1");
		this.tail2 = this.tail1.getChild("tail2");
		this.tail3 = this.tail2.getChild("tail3");
	}

	public static LayerDefinition createModelLayer()
	{
		// Create the mesh definition based on the player model so we can get the correct rotations etc
		MeshDefinition meshdefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
		PartDefinition body = meshdefinition.getRoot().clearRecursively().getChild("body");

		PartDefinition tailBase = body.addOrReplaceChild("tailBase",
				CubeListBuilder.create()
						.texOffs(22, 0).addBox(-2.5F, -2.5F, -2.0F, 5.0F, 5.0F, 8.0F, CubeDeformation.NONE)
						.texOffs(22, 5).addBox(0.0F, -7.25F, -2.0F, 0.0F, 5.0F, 8.0F, CubeDeformation.NONE),
				PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, -0.6981F, 0.0F, 0.0F));

		PartDefinition tail1 = tailBase.addOrReplaceChild("tail1",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 7.0F, CubeDeformation.NONE)
						.texOffs(22, 11).addBox(0.0F, -7.05F, 0.7F, 0.0F, 5.0F, 7.0F, CubeDeformation.NONE),
				PartPose.offsetAndRotation(0.0F, -0.3F, 5.0F, -0.1396F, 0.0F, 0.0F));

		PartDefinition tail2 = tail1.addOrReplaceChild("tail2",
				CubeListBuilder.create()
						.texOffs(0, 11).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 3.0F, 8.0F, CubeDeformation.NONE)
						.texOffs(22, 15).addBox(0.0F, -6.0F, 1.0F, 0.0F, 5.0F, 8.0F, CubeDeformation.NONE),
				PartPose.offsetAndRotation(0.0F, -0.2F, 5.5F, 0.1745F, 0.0F, 0.0F));

		PartDefinition tail3 = tail2.addOrReplaceChild("tail3",
				CubeListBuilder.create()
						.texOffs(0, 22).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 7.0F, CubeDeformation.NONE)
						.texOffs(29, 6).addBox(0.0F, -5.0F, 1.0F, 0.0F, 5.0F, 7.0F, CubeDeformation.NONE),
				PartPose.offsetAndRotation(0.0F, -0.4F, 7.5F, 0.3491F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(AvatarRenderState renderState)
	{
		super.setupAnim(renderState);

		float xAngleOffset = 0;
		float yAngleMultiplier = 1; //Used to suppress sway when running
		if (!renderState.isPassenger)
		{
/*			if (entity instanceof EntityPlayer)
			{
				double[] angles = getMotionAngles((EntityPlayer) entity, partialTicks);

				xAngleOffset = MathHelper.clamp(angles[0] / 5F, -1D, 0.45D);
				yAngleMultiplier = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running
			}*/
		}
		//Mounted
		else
		{
			xAngleOffset = Math.toRadians(12F);
			yAngleMultiplier = 0.25F;
		}

		float timestep = getAnimationTime(4000D, renderState.id);
		this.tailBase.setRotation(Math.toRadians(-40F) + xAngleOffset * 2F, (Math.cos(timestep - 1) / 5F) * yAngleMultiplier, 0F);
		this.tail1.setRotation(Math.toRadians(-8F) + xAngleOffset * 2F, (Math.cos(timestep - 2) / 5F) * yAngleMultiplier, 0F);
		this.tail2.setRotation(Math.toRadians(10F) - xAngleOffset / 4F, (Math.cos(timestep - 3) / 5F) * yAngleMultiplier, 0F);
		this.tail3.setRotation(Math.toRadians(20F) - xAngleOffset, (Math.cos(timestep - 4) / 5F) * yAngleMultiplier, 0F);
	}
}