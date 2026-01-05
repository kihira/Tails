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
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "player"), "chest");
    private final ModelPart tailBase;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;

    public FoxTailModel(ModelPart root)
    {
        super(root);
        this.tailBase = root.getChild("body").getChild("tailBase");
        this.tail1 = tailBase.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.tail3 = tail2.getChild("tail3");
        this.tail4 = tail3.getChild("tail4");
        this.tail5 = tail4.getChild("tail5");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
        PartDefinition partdefinition = meshdefinition.getRoot().clearRecursively();
        PartDefinition partdefinition1 = partdefinition.getChild("body");
        PartDefinition tailBase = partdefinition1.addOrReplaceChild("tailBase",
                        CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE),
                        PartPose.offsetAndRotation(0.0F, 11.0F, 1.5F, -0.2618F, 0.0F, 0.0F)); // offset to bottom of the body

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

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(AvatarRenderState renderState)
    {
        super.setupAnim(renderState);

        var timestep = getAnimationTime(AVERAGE_SPEED, renderState.id);

        var yOffset = 1f;
        var xAngleOffset = 0f;
        var swayDampen = 0f;
        var zAngleOffset = 0f;

        //xAngleOffset = renderState.speedValue -1.f; // Good for when running
        //xAngleOffset = (float) -deltaMovement.y * 0.5f; // Need to invert

        //xAngleOffset = Mth.clamp(xAngleOffset, -1f, 0.35f); // Limit lifting of tail
        swayDampen = (1 - (xAngleOffset * LOW_SWAY_DAMPEN)); //Used to suppress sway when running TODO: Ideally this should be a curve

        var bodyYaw = Mth.rotLerp(renderState.partialTick, renderState.yRot, renderState.bodyRot);
        var bodyYawSin = Mth.sin(bodyYaw * Mth.PI / 180f);
        var bodyYawCos = -Mth.cos(bodyYaw * Mth.PI / 180f);
        float f2 = (float)(renderState.speedValue * bodyYawCos - renderState.speedValue * bodyYawSin) * 100F;
        //zAngleOffset = (float) -Math.toRadians(f2) * 3f;

        //zAngleOffset = Mth.clamp(zAngleOffset, -0.5f, 0.5f);

/*            var angles = getMotionAngles(player, partialTick);
        xAngleOffset = angles[0];
        yAngleOffset = angles[1];
        zAngleOffset = angles[2];

        xAngleOffset = Mth.clamp(xAngleOffset * 0.6f, -1f, 0.45f);
        zAngleOffset = Mth.clamp(zAngleOffset, -0.5f, 0.5f);
        swayDampen = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running*/

        this.tailBase.setRotation(xAngleOffset, (((-zAngleOffset / 2F) + (Mth.cos(timestep + yOffset) / 8F)) * swayDampen), -zAngleOffset / 8F);
        this.tail1.setRotation(-0.2617993877991494f + xAngleOffset + Math.abs(zAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 1 + yOffset) / 8F) * swayDampen, -zAngleOffset / 8F);
        this.tail2.setRotation(-0.2617993877991494f + (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 1.5F + yOffset) / 8F) * swayDampen, -zAngleOffset / 8F);
        this.tail3.setRotation(-0.4363323129985824f + (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 2 + yOffset) / 20F) * swayDampen, -zAngleOffset / 20F);
        this.tail4.setRotation(0.2617993877991494f - (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 3 + yOffset) / 8F) * swayDampen, 0F);
        this.tail5.setRotation(0.2617993877991494f - (xAngleOffset / 2.5F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 4 + yOffset) / 8F) * swayDampen, 0F);
    }

    @Override
    public void setupAnim(Entity entity, float pLimbSwing, float pLimbSwingAmount, float partialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        var timestep = getAnimationTime(AVERAGE_SPEED, pAgeInTicks);

        var yOffset = 1f;
        var xAngleOffset = 0f;
        var swayDampen = 0f;
        var zAngleOffset = 0f;
        if (entity instanceof AbstractClientPlayer player)
        {
            var deltaMovement = player.getDeltaMovement();

            xAngleOffset = (float) deltaMovement.horizontalDistance(); // Good for when running
            //xAngleOffset = (float) -deltaMovement.y * 0.5f; // Need to invert

            xAngleOffset = Mth.clamp(xAngleOffset, -1f, 0.35f); // Limit lifting of tail
            swayDampen = (1 - (xAngleOffset * LOW_SWAY_DAMPEN)); //Used to suppress sway when running TODO: Ideally this should be a curve

            var bodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
            var bodyYawSin = Mth.sin(bodyYaw * Mth.PI / 180f);
            var bodyYawCos = -Mth.cos(bodyYaw * Mth.PI / 180f);
            float f2 = (float)(deltaMovement.x * bodyYawCos - deltaMovement.z * bodyYawSin) * 100F;
            zAngleOffset = (float) -Math.toRadians(f2) * 3f;

            zAngleOffset = Mth.clamp(zAngleOffset, -0.5f, 0.5f);

/*            var angles = getMotionAngles(player, partialTick);
            xAngleOffset = angles[0];
            yAngleOffset = angles[1];
            zAngleOffset = angles[2];

            xAngleOffset = Mth.clamp(xAngleOffset * 0.6f, -1f, 0.45f);
            zAngleOffset = Mth.clamp(zAngleOffset, -0.5f, 0.5f);
            swayDampen = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running*/
        }

        this.tailBase.setRotation(xAngleOffset, (((-zAngleOffset / 2F) + (Mth.cos(timestep + yOffset) / 8F)) * swayDampen), -zAngleOffset / 8F);
        this.tail1.setRotation(-0.2617993877991494f + xAngleOffset + Math.abs(zAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 1 + yOffset) / 8F) * swayDampen, -zAngleOffset / 8F);
        this.tail2.setRotation(-0.2617993877991494f + (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 1.5F + yOffset) / 8F) * swayDampen, -zAngleOffset / 8F);
        this.tail3.setRotation(-0.4363323129985824f + (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 2 + yOffset) / 20F) * swayDampen, -zAngleOffset / 20F);
        this.tail4.setRotation(0.2617993877991494f - (xAngleOffset / 2F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 3 + yOffset) / 8F) * swayDampen, 0F);
        this.tail5.setRotation(0.2617993877991494f - (xAngleOffset / 2.5F), ((-zAngleOffset / 2F) + Mth.cos(timestep - 4 + yOffset) / 8F) * swayDampen, 0F);
    }

    private static final float HEAVY_SWAP_DAMPEN = 4f;
    private static final float MEDIUM_SWAY_DAMPEN = 3f;
    private static final float LOW_SWAY_DAMPEN = 2f;

    private static final float SLOW_SPEED = 4000f;
    private static final float AVERAGE_SPEED = 3000f;
    private static final float FAST_SPEED = 2000f;
}