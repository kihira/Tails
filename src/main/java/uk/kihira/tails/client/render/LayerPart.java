package uk.kihira.tails.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.gltf.Model;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class LayerPart implements LayerRenderer<AbstractClientPlayer> {

    private final ModelRenderer modelRenderer;
    private final MountPoint mountPoint;
    private final boolean mpmCompat;

    public LayerPart(ModelRenderer modelRenderer, MountPoint mountPoint) {
        this.modelRenderer = modelRenderer;
        this.mountPoint = mountPoint;
        this.mpmCompat = Loader.isModLoaded("moreplayermodels");
    }

    @Override
    public void doRenderLayer(@Nonnull AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        UUID uuid = EntityPlayer.getUUID(entity.getGameProfile());
        if (Tails.proxy.hasActiveOutfit(uuid)) {
            Outfit outfit = Tails.proxy.getActiveOutfit(uuid);
            if (outfit == null || outfit.parts == null) return;

            for (OutfitPart part : outfit.parts) {
                if (part.mountPoint == mountPoint) {
                    GlStateManager.pushMatrix();

                    if (mountPoint == MountPoint.HEAD && entity.isSneaking()) GlStateManager.translate(0f, 0.2F, 0f);
                    if (mpmCompat) {
                        GlStateManager.rotate(netHeadYaw, 0f, 1f, 0f);
                        GlStateManager.rotate(headPitch, 1f, 0f, 0f);
                    } else {
                        GlStateManager.rotate(headPitch * 0.017453292F, 1f, 0f, 0f);
                        GlStateManager.rotate(netHeadYaw * 0.017453292F, 0f, 1f, 0f);
                    }

                    modelRenderer.postRender(0.0625F);
                    Model model = PartRegistry.getModel(part.basePart);
                    if (model != null) {
                        model.render();
                    }
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
