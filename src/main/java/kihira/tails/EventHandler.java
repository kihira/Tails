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

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.render.RenderDragonTail;
import kihira.tails.render.RenderFoxTail;
import kihira.tails.render.RenderRacoonTail;
import kihira.tails.texture.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

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

    /*@SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent e) {
    	if (e.entityLiving instanceof EntityPlayer) {
    		EntityPlayer p = (EntityPlayer)e.entityLiving;
    		
    		System.out.println(p.getGameProfile().getName());
    		
    		if(TextureHelper.needsBuild(p)) {
    			TextureHelper.buildPlayerInfo(p);
    		}
    	}
    }*/
}
