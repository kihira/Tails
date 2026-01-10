package uk.kihira.tails.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.client.outfit.OutfitPart;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LegacyLayerPart extends RenderLayer<AvatarRenderState, PlayerModel>
{
    private final MountPoint mountPoint;

    public LegacyLayerPart(RenderLayerParent<AvatarRenderState, PlayerModel> entityRender, ModelPart modelPart, EntityModelSet modelSet, MountPoint mountPoint)
    {
        super(entityRender);

        this.mountPoint = mountPoint;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState renderState, float yRot, float xRot)
    {
        Outfit outfit = renderState.getRenderData(LayerPart.OUTFIT_KEY);
        if (outfit == null || outfit.parts == null)
        {
            return;
        }

        for (OutfitPart part : outfit.parts)
        {
            var basePart = part.getPart();
            if (basePart != null && part.mountPoint == mountPoint)
            {
                var model = part.getPart().getModel();
                if (model == null)
                {
                    return;
                }

                poseStack.pushPose();

                // todo this doesn't translate right, it doesn't seem to be relative to the offset/pivot point
                // this likely needs to be done in setupAnim of the model in PartModel which means we need to ensure the
                // data is in the renderstate. We currently have the outfit but need to associate which part has what data
/*                poseStack.translate(part.mountOffset[0], -part.mountOffset[1], part.mountOffset[2]);
                poseStack.rotateAround(Axis.XP.rotationDegrees(part.rotation[0]), 0, 0, 0);
                poseStack.rotateAround(Axis.YP.rotationDegrees(part.rotation[1]), 0, 0, 0);
                poseStack.rotateAround(Axis.ZP.rotationDegrees(part.rotation[2]), 0, 0, 0);
                poseStack.scale(part.scale[0], part.scale[1], part.scale[2]);*/

                int i = LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);
                nodeCollector.submitModel(model, renderState, poseStack, RenderTypes.entityTranslucent(part.textureIdentifier), packedLight, i, renderState.outlineColor, null);
                poseStack.popPose();
            }
        }
    }
}
