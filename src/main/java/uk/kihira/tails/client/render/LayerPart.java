package uk.kihira.tails.client.render;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.PartRenderer;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.matrix.MatrixStack;

import java.util.UUID;

@ParametersAreNonnullByDefault
public class LayerPart extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> 
{
    private final ModelRenderer modelRenderer;
    private final PartRenderer partRenderer;
    private final MountPoint mountPoint;
    private final boolean mpmCompat;

    public LayerPart(ModelRenderer modelRenderer, PartRenderer partRenderer, MountPoint mountPoint) 
    {
        this.modelRenderer = modelRenderer;
        this.partRenderer = partRenderer;
        this.mountPoint = mountPoint;
        this.mpmCompat = Loader.isModLoaded("moreplayermodels");
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn,
            AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {
        UUID uuid = PlayerEntity.getUUID(entitylivingbaseIn.getGameProfile());
        if (Tails.proxy.hasActiveOutfit(uuid)) {
            Outfit outfit = Tails.proxy.getActiveOutfit(uuid);
            if (outfit == null || outfit.parts == null) 
            {
                return;
            }

            for (OutfitPart part : outfit.parts) {
                if (part.mountPoint == mountPoint) {
                    matrixStackIn.push();

                    if (mountPoint == MountPoint.HEAD && entitylivingbaseIn.isSneaking())
                    {
                        matrixStackIn.translate(0f, 0.2F, 0f);
                    }
                    if (mpmCompat) 
                    {
                        matrixStackIn.rotate(netHeadYaw, 0f, 1f, 0f);
                        matrixStackIn.rotate(headPitch, 1f, 0f, 0f);
                    } else {
                        matrixStackIn.rotate(headPitch * 0.017453292F, 1f, 0f, 0f);
                        matrixStackIn.rotate(netHeadYaw * 0.017453292F, 0f, 1f, 0f);
                    }

                    modelRenderer.postRender(0.0625F);
                    partRenderer.render(part);
                    matrixStackIn.pop();
                }
            }
        }
    }
}
