package uk.kihira.tails.client.model.head;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.player.PlayerModel;
import uk.kihira.tails.client.model.PartModel;

public class SmallCatEarsModel extends PartModel
{
    private final ModelPart leftEar;
    private final ModelPart rightEar;

    public SmallCatEarsModel(ModelPart root)
    {
        super(root);
        this.leftEar = this.partRoot.getChild("leftEar");
        this.rightEar = this.partRoot.getChild("rightEar");
    }

    public static LayerDefinition createModelLayer()
    {
        // Create the mesh definition based on the player model so we can get the correct rotations etc
        // We create a "partRoot" which we can set for the offset/rotation/scale set by the player without needing to worry about our animations
        var meshDefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
        var emptyPlayerDefinition = meshDefinition.getRoot().clearRecursively();
        var partRoot = emptyPlayerDefinition.getChild("head").addOrReplaceChild(PART_ROOT_NAME, CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition leftEar = partRoot.addOrReplaceChild("leftEar", CubeListBuilder.create()
                        .texOffs(0, 14).addBox(-4.0F, -10.0F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(0, 2).addBox(-5.0F, -9.0F, 0.0F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(0, 4).addBox(-5.0F, -10.0F, 0.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(0, 12).addBox(-4.0F, -9.0F, 1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(0, 6).addBox(-4.0F, -11.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE),
                PartPose.ZERO);

        PartDefinition rightEar = partRoot.addOrReplaceChild("rightEar", CubeListBuilder.create()
                        .texOffs(13, 14).addBox(3.0F, -10.0F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(13, 12).addBox(2.0F, -9.0F, 1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(13, 2).addBox(1.0F, -9.0F, 0.0F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(13, 0).addBox(4.0F, -8.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(13, 6).addBox(3.0F, -11.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(13, 4).addBox(2.0F, -10.0F, 0.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE),
                PartPose.ZERO);

        return LayerDefinition.create(meshDefinition, 64, 32);
    }
}