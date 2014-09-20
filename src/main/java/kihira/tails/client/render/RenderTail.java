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
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class RenderTail {

    protected String name;

    public RenderTail(String name) {
        this.name = name;
    }

    public void render(EntityLivingBase entity, TailInfo info, float partialTicks) {
        if (info.needsTextureCompile || info.getTexture() == null) {
            info.setTexture(TextureHelper.generateTexture(info));
            info.needsTextureCompile = false;
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        this.doRender(entity, info, partialTicks);
    }

    protected abstract void doRender(EntityLivingBase player, TailInfo info, float partialTicks);

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
}
