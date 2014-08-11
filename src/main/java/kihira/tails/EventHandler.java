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
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.render.RenderDragonTail;
import kihira.tails.render.RenderFoxTail;
import kihira.tails.render.RenderRacoonTail;
import kihira.tails.render.RenderTail;
import kihira.tails.texture.TextureHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.Hashtable;
import java.util.UUID;

public class EventHandler {

	public static final RenderTail[] tailTypes = { new RenderFoxTail(), new RenderDragonTail(), new RenderRacoonTail() };
    public static Hashtable<UUID, TailInfo> TailMap = new Hashtable<UUID, TailInfo>();

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerRenderTick(RenderPlayerEvent.Specials.Pre e) {
    	if(TextureHelper.needsBuild(e.entityPlayer)) {
			TextureHelper.buildPlayerInfo(e.entityPlayer);
		}

        //TESTING REMOVE
/*        if (e.entityPlayer.isSneaking()) {
            EntityPlayer player = e.entityPlayer;
            UUID id = player.getGameProfile().getId();
            int typeid = 1;
            int subtype = 1;
            RenderTail type = EventHandler.tailTypes[typeid];
            String[] textures = type.getTextureNames();
            String texturepath = "texture/"+textures[0]+".png";

            ResourceLocation tailtexture = new ResourceLocation("tails_"+id.toString()+"_"+type+"_"+subtype+"_"+0);
            Minecraft.getMinecraft().getTextureManager().loadTexture(tailtexture, new TripleTintTexture("tails", texturepath, 0x0c0810, 0x140d16, 0x170d1c));

            TextureHelper.clearTailInfo(player);
            EventHandler.TailMap.put(id, new TailInfo(id, true, typeid, subtype, tailtexture));
        }*/
    	
    	UUID id = e.entityPlayer.getGameProfile().getId();
        if (TailMap.containsKey(id) && TailMap.get(id).hastail && !e.entityPlayer.isInvisible()) {
        	TailInfo info = TailMap.get(id);
        	
        	int type = info.typeid;
        	type = type > tailTypes.length ? 0 : type;
        	
            tailTypes[type].render(e.entityPlayer, info);
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
			if(TextureHelper.needsBuild(e.player)) {
				TextureHelper.buildPlayerInfo(e.player);
			}
		}
    }
}
