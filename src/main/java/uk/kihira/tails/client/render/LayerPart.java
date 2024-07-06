package uk.kihira.tails.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.PartRenderer;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class LayerPart<T extends Player, M extends PlayerModel<T>> extends RenderLayer<T, M>
{
    private final PartRenderer partRenderer;
    private final MountPoint mountPoint;
    private final boolean mpmCompat = false;

    public LayerPart(LivingEntityRenderer<T, M> entityRender, ModelPart modelPart, PartRenderer partRenderer, MountPoint mountPoint)
    {
        super(entityRender);

        this.partRenderer = partRenderer;
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

            for (OutfitPart part : outfit.parts) {
                if (part.mountPoint == mountPoint) {
                    poseStack.pushPose();

                    if (mountPoint == MountPoint.HEAD && player.isCrouching())
                    {
                        poseStack.translate(0f, 0.2F, 0f);
                    }
                    if (mpmCompat) 
                    {
                        poseStack.rotateAround(Axis.YP.rotationDegrees(netHeadYaw), 0, 0, 0);
                        poseStack.rotateAround(Axis.XP.rotationDegrees(headPitch), 0, 0, 0);
                    }
                    else
                    {
                        poseStack.rotateAround(Axis.XP.rotationDegrees(headPitch * 0.017453292F), 0, 0, 0);
                        poseStack.rotateAround(Axis.YP.rotationDegrees(netHeadYaw * 0.017453292F), 0, 0, 0);
                    }

                    partRenderer.render(poseStack, part);
                    poseStack.popPose();
                }
            }
        }
    }
}
