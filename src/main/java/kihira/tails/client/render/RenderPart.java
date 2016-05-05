/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import kihira.tails.api.IRenderHelper;
import kihira.tails.client.model.ModelPartBase;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.PartInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class RenderPart {

    private static final HashMap<Class<? extends EntityLivingBase>, IRenderHelper> renderHelpers = new HashMap<Class<? extends EntityLivingBase>, IRenderHelper>();

    protected final String name;
    protected final String[] textureNames;
    protected final int subTypes;
    protected final String[][] authors;
    protected final String modelAuthor;
    public final ModelPartBase modelPart;

    public RenderPart(String name, int subTypes, String modelAuthor, ModelPartBase modelPart, String... textureNames) {
        this.name = name;
        this.subTypes = subTypes;
        this.modelAuthor = modelAuthor;
        this.modelPart = modelPart;
        this.textureNames = textureNames;
        this.authors = new String[subTypes + 1][textureNames.length];
    }

    public void render(EntityLivingBase entity, PartInfo info, double x, double y, double z, float partialTicks) {
        if (info.needsTextureCompile || info.getTexture() == null) {
            info.setTexture(TextureHelper.generateTexture(entity.getUniqueID(), info));
            info.needsTextureCompile = false;
        }

        GL11.glPushMatrix();

        IRenderHelper helper;
        //Support for Galacticraft as it adds its own EntityPlayer
        if (entity instanceof EntityPlayer) helper = getRenderHelper(EntityPlayer.class);
        else helper = getRenderHelper(entity.getClass());
        if (helper != null) {
            helper.onPreRenderTail(entity, this, info, x, y, z);
        }

        this.doRender(entity, info, partialTicks);
        GL11.glPopMatrix();
    }

    protected void doRender(EntityLivingBase entity, PartInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        this.modelPart.render(entity, info.subid, partialTicks);
    }

    /**
     * Gets the available textures for this tail subid.
     * By default, this provides {@link #textureNames} for all subid's but you can override for finer control
     * @return Available textures
     * @param subid The subid
     */
    public String[] getTextureNames(int subid) {
        return textureNames;
    }

    /**
     * Gets the available subtypes for this tail
     * @return subtypes
     */
    public int getAvailableSubTypes() {
        return subTypes;
    }

    public String getUnlocalisedName(int subType) {
        return this.name+"."+subType+".name";
    }

    public RenderPart setAuthor(String author, int subID, int textureID) {
        authors[subID][textureID] = author;
        return this;
    }

    public RenderPart setAuthor(String author, int subID) {
        for (int textureID = 0; textureID < getTextureNames(subID).length; textureID++) {
            setAuthor(author, subID, textureID);
        }
        return this;
    }

    public RenderPart setAuthor(String author) {
        for (int subID = 0; subID <= subTypes; subID++) {
            setAuthor(author, subID);
        }
        return this;
    }

    public String getModelAuthor() {
        return modelAuthor;
    }

    public String getAuthor(int subID, int textureID) {
        return authors[subID][textureID];
    }

    public boolean hasAuthor(int subID, int textureID) {
        return getAuthor(subID, textureID) != null;
    }

    public static void registerRenderHelper(Class<? extends EntityLivingBase> clazz, IRenderHelper helper) {
        if (!renderHelpers.containsKey(clazz) && helper != null) {
            renderHelpers.put(clazz, helper);
        }
        else {
            throw new IllegalArgumentException("An invalid RenderHelper was registered!");
        }
    }

    public static IRenderHelper getRenderHelper(Class<? extends EntityLivingBase> clazz) {
        if (renderHelpers.containsKey(clazz)) {
            return renderHelpers.get(clazz);
        }
        else return null;
    }
}
