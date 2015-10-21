/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import kihira.tails.client.model.ModelPartBase;
import kihira.tails.common.PartInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class RenderWings extends RenderPart {

    public RenderWings(String name, int subTypes, String modelAuthor, ModelPartBase modelPart, String... textureNames) {
        super(name, subTypes, modelAuthor, modelPart, textureNames);
    }

    @Override
    protected void doRender(EntityLivingBase entity, PartInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        Tessellator tessellator = Tessellator.getInstance();
        boolean isFlying = entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying && entity.isAirBorne || entity.fallDistance > 0F;
        float timestep = ModelPartBase.getAnimationTime(isFlying ? 500 : 6500, entity);
        float angle = (float) Math.sin(timestep) * (isFlying ? 24F : 4F);
        float scale = info.subid == 1 ? 1F : 2F;

        GL11.glTranslatef(0, -(scale * 8F) * ModelPartBase.SCALE + (info.subid == 1 ? 0.1F : 0), 0.1F);
        GL11.glRotatef(90, 0, 1, 0);
        GL11.glRotatef(90, 0, 0, 1);
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(0.1F, -0.4F * ModelPartBase.SCALE, -0.025F);

        GL11.glPushMatrix();
        GL11.glTranslatef(0F, 0F, 1F * ModelPartBase.SCALE);
        GL11.glRotatef(30F - angle, 1F, 0F, 0F);
        // TODO ItemRenderer.renderItemIn2D(tessellator, 0, 0, 1, 1, 32, 32, ModelPartBase.SCALE / scale);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 32, 32, 32, 32);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(0F, 0.3F * ModelPartBase.SCALE, 0F);
        GL11.glRotatef(-30F + angle, 1F, 0F, 0F);
        // TODO ItemRenderer.renderItemIn2D(tessellator, 0, 0, 1, 1, 32, 32, ModelPartBase.SCALE / scale);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 32, 32, 32, 32);
        GL11.glPopMatrix();
    }
}
