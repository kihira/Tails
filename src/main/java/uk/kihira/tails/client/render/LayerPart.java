package uk.kihira.tails.client.render;

import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.common.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class LayerPart implements LayerRenderer<AbstractClientPlayer> {

    private final ModelRenderer modelRenderer;
    private final MountPoint mountPoint;

    public LayerPart(ModelRenderer modelRenderer, MountPoint mountPoint) {
        this.modelRenderer = modelRenderer;
        this.mountPoint = mountPoint;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        UUID uuid = EntityPlayer.getUUID(entity.getGameProfile());
        if (Tails.proxy.hasActiveOutfit(uuid)) {
            Outfit outfit = Tails.proxy.getActiveOutfit(uuid);
            for (OutfitPart part : outfit.parts) {
                if (part.mountPoint == mountPoint) {
                    GlStateManager.pushMatrix();
                    if (mountPoint == MountPoint.HEAD && entity.isSneaking()) GlStateManager.translate(0f, 0.2F, 0f);
                    modelRenderer.postRender(0.0625F);

                    PartRegistry.getRenderer(part.basePart).render(entity, part, partialTicks);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
