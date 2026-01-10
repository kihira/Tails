package uk.kihira.tails.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.PartRenderer;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.Tails;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LayerPart extends RenderLayer<AvatarRenderState, PlayerModel>
{
    private final PartRenderer partRenderer;
    private final MountPoint mountPoint;
    private final boolean mpmCompat = false;

    public LayerPart(RenderLayerParent<AvatarRenderState, PlayerModel> entityRender, ModelPart modelPart, PartRenderer partRenderer, MountPoint mountPoint)
    {
        super(entityRender);

        this.partRenderer = partRenderer;
        this.mountPoint = mountPoint;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState renderState, float yRot, float xRot)
    {
        var outfit = renderState.getRenderData(OUTFIT_KEY);
        if (outfit == null)
        {
            return;
        }

        for (OutfitPart part : outfit.getParts())
        {
            if (part.mountPoint == mountPoint)
            {
                poseStack.pushPose();

                if (mountPoint == MountPoint.HEAD && renderState.isCrouching)
                {
                    poseStack.translate(0f, 0.2F, 0f);
                }
                // todo
/*                if (mpmCompat)
                {
                    poseStack.rotateAround(Axis.YP.rotationDegrees(netHeadYaw), 0, 0, 0);
                    poseStack.rotateAround(Axis.XP.rotationDegrees(headPitch), 0, 0, 0);
                }
                else
                {
                    poseStack.rotateAround(Axis.XP.rotationDegrees(headPitch * 0.017453292F), 0, 0, 0);
                    poseStack.rotateAround(Axis.YP.rotationDegrees(netHeadYaw * 0.017453292F), 0, 0, 0);
                }*/

                partRenderer.render(poseStack, part);
                poseStack.popPose();
            }
        }
    }

    public static final ContextKey<Outfit> OUTFIT_KEY = new ContextKey<>(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "outfit"));
}
