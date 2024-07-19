package uk.kihira.tails.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.model.FoxTailModel;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.common.Tails;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Legacy layer render for code based models
 * @param <T>
 * @param <M>
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class LegacyLayerPart<T extends Player, M extends PlayerModel<T>> extends RenderLayer<T, M>
{
    private final MountPoint mountPoint;

    public LegacyLayerPart(LivingEntityRenderer<T, M> entityRender, ModelPart modelPart, MountPoint mountPoint)
    {
        super(entityRender);

        this.mountPoint = mountPoint;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch)
    {
        var uuid = player.getGameProfile().getId();
        if (Tails.proxy.hasActiveOutfit(uuid)) {
            Outfit outfit = Tails.proxy.getActiveOutfit(uuid);
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

                    if (mountPoint == MountPoint.HEAD && player.isCrouching())
                    {
                        poseStack.translate(0f, 0.2F, 0f);
                    }
                    //poseStack.rotateAround(Axis.XP.rotationDegrees(headPitch * 0.017453292F), 0, 0, 0);
                    //poseStack.rotateAround(Axis.YP.rotationDegrees(netHeadYaw * 0.017453292F), 0, 0, 0);

                    this.getParentModel().body.translateAndRotate(poseStack);
                    poseStack.translate(part.mountOffset[0], -part.mountOffset[1], part.mountOffset[2]);
                    poseStack.rotateAround(Axis.XP.rotationDegrees(part.rotation[0]), 0, 0, 0);
                    poseStack.rotateAround(Axis.YP.rotationDegrees(part.rotation[1]), 0, 0, 0);
                    poseStack.rotateAround(Axis.ZP.rotationDegrees(part.rotation[2]), 0, 0, 0);
                    poseStack.scale(part.scale[0], part.scale[1], part.scale[2]);

                    VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.armorCutoutNoCull(part.textureLoc));
                    model.setupAnim(player, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
                    model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    poseStack.popPose();
                }
            }
        }
    }
}
