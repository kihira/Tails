/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.client.Part;
import uk.kihira.tails.client.model.ModelPartBase;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.PartInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;


import javax.annotation.Nullable;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class LegacyPartRenderer {

    private final Part part;
    public final ModelPartBase modelPart;

    public LegacyPartRenderer(Part part, ModelPartBase modelPart) {
        this.part = part;
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
            helper.onPreRenderTail(entity, this, outfitPart);
        }

        this.doRender(entity, info, partialTicks);
        GlStateManager.popMatrix();
    }

    protected void doRender(EntityLivingBase entity, PartInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        this.modelPart.render(entity, info.subid, partialTicks);
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
