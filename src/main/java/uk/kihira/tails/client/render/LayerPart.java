package uk.kihira.tails.client.render;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.PartRenderer;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.matrix.MatrixStack;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class LayerPart extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
{
    private final ModelRenderer modelRenderer;
    private final PartRenderer partRenderer;
    private final MountPoint mountPoint;
    private final boolean mpmCompat = false;

    public LayerPart(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRender, ModelRenderer modelRenderer, PartRenderer partRenderer, MountPoint mountPoint)
    {
        super(entityRender);

        this.modelRenderer = modelRenderer;
        this.partRenderer = partRenderer;
        this.mountPoint = mountPoint;
        // TODO this.mpmCompat = Loader.isModLoaded("moreplayermodels");
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
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(netHeadYaw));
                        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(headPitch));
                    }
                    else
                    {
                        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(headPitch * 0.017453292F));
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(netHeadYaw * 0.017453292F));
                    }

                    partRenderer.render(matrixStackIn, part);
                    matrixStackIn.pop();
                }
            }
        }
    }
}
