package uk.kihira.tails.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.player.PlayerModel;

public final class DevilTailModel extends PartModel
{
	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart tail5;
	private final ModelPart tailTip;

	public DevilTailModel(ModelPart root)
	{
		super(root);
		this.tailBase = this.partRoot.getChild("tailBase");
		this.tail1 = this.tailBase.getChild("tail1");
		this.tail2 = this.tail1.getChild("tail2");
		this.tail3 = this.tail2.getChild("tail3");
		this.tail4 = this.tail3.getChild("tail4");
		this.tail5 = this.tail4.getChild("tail5");
		this.tailTip = this.tail5.getChild("tailTip");
	}

	public static LayerDefinition createModelLayer()
	{
		// Create the mesh definition based on the player model so we can get the correct rotations etc
		// We create a "partRoot" which we can set for the offset/rotation/scale set by the player without needing to worry about our animations
		var meshDefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
		var emptyPlayerDefinition = meshDefinition.getRoot().clearRecursively();
		var partRoot = emptyPlayerDefinition.getChild("body").addOrReplaceChild(PART_ROOT_NAME, CubeListBuilder.create(), PartPose.ZERO);

		var tailBase = partRoot.addOrReplaceChild("tailBase", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE), 
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

		var tail1 = tailBase.addOrReplaceChild("tail1", CubeListBuilder.create()
				.texOffs(0, 4).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, CubeDeformation.NONE), 
				PartPose.offsetAndRotation(0.0F, 0.0F, 1.8F, -0.5236F, 0.0F, 0.0F));

		var tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(0, 9).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, CubeDeformation.NONE), 
				PartPose.offsetAndRotation(0.0F, 0.0F, 3.8F, -0.5236F, 0.0F, 0.0F));

		var tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create()
				.texOffs(0, 15).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE), 
				PartPose.offsetAndRotation(0.0F, 0.0F, 4.8F, 0.3491F, 0.0F, 0.0F));

		var tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create()
				.texOffs(0, 19).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), 
				PartPose.offsetAndRotation(0.0F, 0.0F, 2.6F, 0.8727F, 0.0F, 0.0F));

		var tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create()
				.texOffs(0, 22).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), 
				PartPose.offsetAndRotation(0.0F, 0.0F, 1.7F, 0.8727F, 0.0F, 0.0F));

		var tailTip = tail5.addOrReplaceChild("tailTip", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 1.8F, -2.0944F, 0.0F, 0.0F));

		var cube_r1 = tailTip.addOrReplaceChild("cube_r1", CubeListBuilder.create()
				.texOffs(12, 0).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, CubeDeformation.NONE), 
				PartPose.offsetAndRotation(0.0F, -2.5F, 0.0F, 0.0F, 0.0F, -3.1416F));

		return LayerDefinition.create(meshDefinition, 64, 32);
	}
}