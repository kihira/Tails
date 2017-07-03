package uk.kihira.tails.client.render;

import net.minecraftforge.fml.common.Loader;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class LayerPart implements LayerRenderer<AbstractClientPlayer> {

    private final ModelRenderer modelRenderer;
    private final PartsData.PartType partType;
    private final boolean mpmCompat;

    public LayerPart(ModelRenderer modelRenderer, PartsData.PartType partType) {
        this.modelRenderer = modelRenderer;
        this.partType = partType;
        this.mpmCompat = Loader.isModLoaded("moreplayermodels");
    }

    @Override
    public void doRenderLayer(@Nonnull AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity.isInvisible()) return;

        UUID uuid = EntityPlayer.getUUID(entity.getGameProfile());
        if (Tails.proxy.hasPartsData(uuid)) {
            PartsData partsData = Tails.proxy.getPartsData(uuid);
            if (partsData.hasPartInfo(partType)) {
                PartInfo tailInfo = partsData.getPartInfo(partType);

                GlStateManager.pushMatrix();
                modelRenderer.postRender(0.0625F);

                if (partType == PartsData.PartType.EARS || partType == PartsData.PartType.MUZZLE) {
                    if (entity.isSneaking()) {
                        GlStateManager.translate(0f, 0.2F, 0f);
                    }

                    // todo should really do transforms on the model instead, should hopefully be "fixed" on model rewrite
                    if (mpmCompat) {
                        GlStateManager.rotate(netHeadYaw, 0f, 1f, 0f);
                        GlStateManager.rotate(headPitch, 1f, 0f, 0f);
                    }
                    else {
                        GlStateManager.rotate(headPitch * 0.017453292F, 1f, 0f, 0f);
                        GlStateManager.rotate(netHeadYaw * 0.017453292F, 0f, 1f, 0f);
                    }
                }

                PartRegistry.getRenderPart(tailInfo.partType, tailInfo.typeid).render(entity, tailInfo, 0, 0, 0, partialTicks);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
