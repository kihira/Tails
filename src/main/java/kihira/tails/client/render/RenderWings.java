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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class RenderWings extends RenderPart {

    public RenderWings(String name, int subTypes, String modelAuthor, ModelPartBase modelPart, String... textureNames) {
        super(name, subTypes, modelAuthor, modelPart, textureNames);
    }

    @Override
    protected void doRender(EntityLivingBase entity, PartInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        boolean isFlying = entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying && entity.isAirBorne || entity.fallDistance > 0F;
        float timestep = ModelPartBase.getAnimationTime(isFlying ? 500 : 6500, entity);
        float angle = (float) Math.sin(timestep) * (isFlying ? 24F : 4F);
        float scale = info.subid == 1 ? 1F : 2F;

        GlStateManager.translate(0, -(scale * 8F) * ModelPartBase.SCALE + (info.subid == 1 ? 0.1F : 0), 0.1F);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.rotate(90, 0, 0, 1);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0.1F, -0.4F * ModelPartBase.SCALE, -0.025F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, 0F, 1F * ModelPartBase.SCALE);
        GlStateManager.rotate(30F - angle, 1F, 0F, 0F);
        // TODO Port to 1.8.8
/*        renderer.startDrawingQuads();
        renderer.addVertexWithUV(0, 1, 0, 0, 0);
        renderer.addVertexWithUV(1, 1, 0, 1, 0);
        renderer.addVertexWithUV(1, 0, 0, 1, 1);
        renderer.addVertexWithUV(0, 0, 0, 0, 1);
        Tessellator.getInstance().draw();*/
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, 0.3F * ModelPartBase.SCALE, 0F);
        GlStateManager.rotate(-30F + angle, 1F, 0F, 0F);
/*        renderer.startDrawingQuads();
        renderer.addVertexWithUV(0, 1, 0, 0, 0);
        renderer.addVertexWithUV(1, 1, 0, 1, 0);
        renderer.addVertexWithUV(1, 0, 0, 1, 1);
        renderer.addVertexWithUV(0, 0, 0, 0, 1);
        Tessellator.getInstance().draw();*/
        GlStateManager.popMatrix();
    }
}
