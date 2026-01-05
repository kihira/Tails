package uk.kihira.tails.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.model.FoxTailModel;
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

                if (mountPoint == MountPoint.HEAD && renderState.isCrouching)
                {
                    poseStack.translate(0f, 0.2F, 0f);
                }
                //poseStack.rotateAround(Axis.XP.rotationDegrees(headPitch * 0.017453292F), 0, 0, 0);
                //poseStack.rotateAround(Axis.YP.rotationDegrees(netHeadYaw * 0.017453292F), 0, 0, 0);

 /*               this.getParentModel().body.translateAndRotate(poseStack);
                poseStack.translate(part.mountOffset[0], -part.mountOffset[1], part.mountOffset[2]);
                poseStack.rotateAround(Axis.XP.rotationDegrees(part.rotation[0]), 0, 0, 0);
                poseStack.rotateAround(Axis.YP.rotationDegrees(part.rotation[1]), 0, 0, 0);
                poseStack.rotateAround(Axis.ZP.rotationDegrees(part.rotation[2]), 0, 0, 0);
                poseStack.scale(part.scale[0], part.scale[1], part.scale[2]);*/

                // TODO should this ever be null?
                if (part.textureLoc != null)
                {
                    //VertexConsumer vertexconsumer = buffer.getBuffer(RenderTypes.armorCutoutNoCull(part.textureLoc));
                    //model.setupAnim(player, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
                    //model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                    int i = LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);
                    nodeCollector.submitModel(model, renderState, poseStack, RenderTypes.entitySolid(part.textureLoc), packedLight, i, renderState.outlineColor, null);
                }
                poseStack.popPose();
            }
        }
    }
}
