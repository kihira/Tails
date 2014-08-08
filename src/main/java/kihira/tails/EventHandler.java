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

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.render.RenderDragonTail;
import kihira.tails.render.RenderFoxTail;
import kihira.tails.render.RenderRacoonTail;
import kihira.tails.texture.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class EventHandler {

    RenderDragonTail renderDragonTail = new RenderDragonTail();
    RenderFoxTail renderFoxTail = new RenderFoxTail();
    RenderRacoonTail renderRacoonTail = new RenderRacoonTail();

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerRenderTick(RenderPlayerEvent.Specials.Pre e) {
    	if(TextureHelper.needsBuild(e.entityPlayer)) {
			TextureHelper.buildPlayerInfo(e.entityPlayer);
		}    	
    	
    	UUID id = e.entityPlayer.getGameProfile().getId();
        if (TextureHelper.TailMap.containsKey(id) && TextureHelper.TailMap.get(id).hastail && !e.entityPlayer.isInvisible()) {
        	TailInfo info = TextureHelper.TailMap.get(id);
        	
            renderDragonTail.render(e.entityPlayer, info);
        }
    }
    
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
    	TextureHelper.clearTailInfo(e.player);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.side.isClient()) {
			//System.out.println(e.player.getGameProfile().getName());
			
			if(TextureHelper.needsBuild(e.player)) {
				TextureHelper.buildPlayerInfo(e.player);
			}
		}
    }
    
    private Gui g = new Gui();
    
    @SubscribeEvent
    public void onRenderExperienceBar(RenderGameOverlayEvent e)
    {
    	if(e.isCancelable() || e.type != ElementType.EXPERIENCE)
    	{      
    		return;
    	}
    	
    	Minecraft mc = Minecraft.getMinecraft();
    	EntityPlayer p = mc.thePlayer;
    	
    	if (p != null) {
    		UUID id = p.getGameProfile().getId();
    		
    		TailInfo info = TextureHelper.TailMap.get(id);
    		if (info == null) {return;}
    		
    		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    	    GL11.glDisable(GL11.GL_LIGHTING);      
    	    mc.renderEngine.bindTexture(info.texture);
    	    
    	    Tessellator tessellator = Tessellator.instance;
    	    tessellator.startDrawingQuads();    
    	    tessellator.addVertexWithUV(5, 5 + 32, 0, 0.0, 1.0);
    	    tessellator.addVertexWithUV(5 + 64, 5 + 32, 0, 1.0, 1.0);
    	    tessellator.addVertexWithUV(5 + 64, 5, 0, 1.0, 0.0);
    	    tessellator.addVertexWithUV(5, 5, 0, 0.0, 0.0);
    	    tessellator.draw();
    	}
    }
}
