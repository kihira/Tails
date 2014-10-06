/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.api.ITailRenderHelper;
import kihira.tails.client.model.ModelTailBase;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public abstract class RenderTail {

    private static HashMap<Class<? extends EntityLivingBase>, ITailRenderHelper> tailHelpers = new HashMap<Class<? extends EntityLivingBase>, ITailRenderHelper>();

    protected String name;
    public final ModelTailBase modelTail;

    public RenderTail(String name, ModelTailBase modelTail) {
        this.name = name;
        this.modelTail = modelTail;
    }

    public void render(EntityLivingBase entity, TailInfo info, double x, double y, double z, float partialTicks) {
        if (info.needsTextureCompile || info.getTexture() == null) {
            info.setTexture(TextureHelper.generateTexture(info));
            info.needsTextureCompile = false;
        }

        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        ITailRenderHelper helper = getTailHelper(entity.getClass());
        if (helper != null) {
            helper.onPreRenderTail(entity, this, info, x, y, z);
        }
        this.doRender(entity, info, partialTicks);
        GL11.glPopMatrix();
    }

    protected void doRender(EntityLivingBase entity, TailInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        this.modelTail.render(entity, info.subid, partialTicks);
    }

    /**
     * Gets the available textures for this tail subid
     * @return Available textures
     * @param subid The subid
     */
    public abstract String[] getTextureNames(int subid);

    /**
     * Gets the available subtypes for this tail
     * @return subtypes
     */
    public abstract int getAvailableSubTypes();

    public String getUnlocalisedName(int subType) {
        return "tail."+this.name+"."+subType+".name";
    }

    public static void registerTailHelper(Class<? extends EntityLivingBase> clazz, ITailRenderHelper helper) {
        if (!tailHelpers.containsKey(clazz) && helper != null) {
            tailHelpers.put(clazz, helper);
        }
        else {
            throw new IllegalArgumentException("An invalid Tail Helper was registered!");
        }
    }

    public static ITailRenderHelper getTailHelper(Class<? extends EntityLivingBase> clazz) {
        if (tailHelpers.containsKey(clazz)) {
            return tailHelpers.get(clazz);
        }
        else return null;
    }
}
