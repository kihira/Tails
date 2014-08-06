/*
 * Copyright (C) 2014  Kihira
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package kihira.tails;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

public class EventHandler {

    ModelFoxTail modelFoxTail = new ModelFoxTail();
    ResourceLocation tailTexture = new ResourceLocation("tails", "texture/foxTail.png");

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerRenderTick(RenderPlayerEvent.Specials.Pre e) {
        if (Tails.userList.contains(e.entityPlayer.getCommandSenderName()) && !e.entityPlayer.isInvisible()) {
            GL11.glPushMatrix();
            Minecraft.getMinecraft().renderEngine.bindTexture(tailTexture);
            if (!e.entityPlayer.isSneaking()) GL11.glTranslatef(0F, 0.65F, 0.1F);
            else GL11.glTranslatef(0F, 0.55F, 0.4F);
            GL11.glScalef(0.8F, 0.8F, 0.8F);
            modelFoxTail.render(e.entityPlayer, 0, 0, 0, 0, 0, 0.0625F);
            GL11.glPopMatrix();
        }
    }
}
