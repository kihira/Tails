package uk.kihira.tails.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import uk.kihira.tails.Tails;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.client.outfit.OutfitPart;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;

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
        var outfit = renderState.getRenderData(LayerPart.OUTFIT_KEY);
        if (outfit == null)
        {
            return;
        }

        for (var part : outfit.getParts())
        {
            var basePart = part.getPart();
            if (basePart != null && part.mountPoint == mountPoint)
            {
                var model = part.getPart().getModel();
                if (model == null)
                {
                    return;
                }

                // We're technically replicating data in outfit within the same render data, however we are putting it in a
                // slightly easier place to get, and we're putting it into an order.
                // As of the time of writing this, the list of "submitted models" are rendered in the order they are submitted,
                // so if we keep a list of part data, we can just use it like a queue and pop the first item off for each model rendered.
                var partDataQueue = renderState.getRenderDataOrDefault(PART_DATA_KEY, new ArrayDeque<>());
                partDataQueue.add(part);
                renderState.setRenderData(PART_DATA_KEY, partDataQueue);

                int i = LivingEntityRenderer.getOverlayCoords(renderState, 0f);
                nodeCollector.submitModel(model, renderState, poseStack, RenderTypes.entityTranslucent(part.textureIdentifier), packedLight, i, renderState.outlineColor, null);
            }
        }
    }

    public static ContextKey<Queue<OutfitPart>> PART_DATA_KEY = new ContextKey<>(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "part_data"));
}
