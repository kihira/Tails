package uk.kihira.tails.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;


import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class LegacyPartRenderer {
    public final ModelPartBase modelPart;

    public LegacyPartRenderer(ModelPartBase modelPart) {
        this.modelPart = modelPart;
    }

    public void render(EntityLivingBase entity, OutfitPart outfitPart, float partialTicks) {
/*        if (info.needsTextureCompile || info.getTexture() == null) {
            info.setTexture(TextureHelper.generateTexture(entity.getUniqueID(), info));
            info.needsTextureCompile = false;
        }*/

        GlStateManager.pushMatrix();

        IRenderHelper helper;
        //Support for Galacticraft as it adds its own EntityPlayer
        if (entity instanceof EntityPlayer) helper = getRenderHelper(EntityPlayer.class);
        else helper = getRenderHelper(entity.getClass());
        if (helper != null) {
            helper.onPreRenderTail(entity, outfitPart);
        }

        this.doRender(entity, outfitPart, partialTicks);
        GlStateManager.popMatrix();
    }

    protected void doRender(EntityLivingBase entity, OutfitPart outfitPart, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(outfitPart.compiledTexture);
        this.modelPart.render(entity, partialTicks);
    }

    // todo move below to somewhere else
    private static final HashMap<Class<? extends EntityLivingBase>, IRenderHelper> renderHelpers = new HashMap<>();

    public static void registerRenderHelper(Class<? extends EntityLivingBase> clazz, IRenderHelper helper) {
        if (!renderHelpers.containsKey(clazz) && helper != null) {
            renderHelpers.put(clazz, helper);
        }
        else {
            throw new IllegalArgumentException("An invalid RenderHelper was registered!");
        }
    }

    public static IRenderHelper getRenderHelper(Class<? extends EntityLivingBase> clazz) {
        return renderHelpers.getOrDefault(clazz, null);
    }
}
