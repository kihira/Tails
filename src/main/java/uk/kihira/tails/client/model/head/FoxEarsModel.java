package uk.kihira.tails.client.model.head;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.player.PlayerModel;
import uk.kihira.tails.client.model.PartModel;

public class FoxEarsModel extends PartModel
{
	private final ModelPart rightEar;
	private final ModelPart leftEar;

	public FoxEarsModel(ModelPart root)
	{
		super(root);
		this.rightEar = root.getChild("head").getChild("rightEar");
		this.leftEar = root.getChild("head").getChild("leftEar");
	}

	public static LayerDefinition createModelLayer()
	{
		// Create the mesh definition based on the player model so we can get the correct rotations etc
		MeshDefinition meshdefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
		PartDefinition head = meshdefinition.getRoot().clearRecursively().getChild("head");

		PartDefinition rightEar = head.addOrReplaceChild("rightEar",
				CubeListBuilder.create()
						.texOffs(0, 19).addBox(4.0F, -5.0F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(4, 19).addBox(3.0F, -3.0F, 1.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(0, 4).addBox(2.0F, -3.0F, 1.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(4, 2).addBox(3.0F, -4.0F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(4, 6).addBox(4.0F, -4.0F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(0, 12).addBox(5.0F, -4.0F, 1.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(10, 12).addBox(3.0F, -1.0F, 1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(4, 12).addBox(4.0F, -4.0F, 2.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(8, 3).addBox(3.0F, -3.0F, 2.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE),
				PartPose.offset(-1.0F, -7.0F, 0.0F)); // offset to the top of the head

		PartDefinition leftEar = head.addOrReplaceChild("leftEar",
				CubeListBuilder.create()
						.texOffs(0, 16).addBox(-5.0F, -5.0F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(4, 16).addBox(-5.0F, -3.0F, 1.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(0, 0).addBox(-3.0F, -3.0F, 1.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(2, 1).addBox(-4.0F, -4.0F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(4, 4).addBox(-5.0F, -4.0F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(0, 8).addBox(-6.0F, -4.0F, 1.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(10, 14).addBox(-5.0F, -1.0F, 1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(4, 8).addBox(-5.0F, -4.0F, 2.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
						.texOffs(8, 3).addBox(-4.0F, -3.0F, 2.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE),
				PartPose.offset(1.0F, -7.0F, 0.0F)); // offset to the top of the head

		return LayerDefinition.create(meshdefinition, 16, 32);
	}
}