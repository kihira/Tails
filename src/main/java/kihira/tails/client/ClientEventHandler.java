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

package kihira.tails.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.gui.GuiEditTail;
import kihira.tails.client.render.RenderDragonTail;
import kihira.tails.client.render.RenderFoxTail;
import kihira.tails.client.render.RenderRacoonTail;
import kihira.tails.client.render.RenderTail;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {

	public static final RenderTail[] tailTypes = { new RenderFoxTail(), new RenderDragonTail(), new RenderRacoonTail() };

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerRenderTick(RenderPlayerEvent.Specials.Pre e) {
    	if(TextureHelper.needsBuild(e.entityPlayer)) {
			TextureHelper.buildPlayerInfo(e.entityPlayer);
		}

        //TESTING REMOVE
/*        if (e.entityPlayer.isSneaking()) {
            EntityPlayer player = e.entityPlayer;
            UUID uuid = player.getGameProfile().getId();
            int typeid = 1;
            int subtype = 1;
            RenderTail type = EventHandler.tailTypes[typeid];
            String[] textures = type.getTextureNames();
            String texturepath = "texture/"+textures[0]+".png";

            ResourceLocation tailtexture = new ResourceLocation("tails_"+uuid.toString()+"_"+type+"_"+subtype+"_"+0);
            Minecraft.getMinecraft().getTextureManager().loadTexture(tailtexture, new TripleTintTexture("tails", texturepath, 0x0c0810, 0x140d16, 0x170d1c));

            TextureHelper.clearTailInfo(player);
            EventHandler.tailMap.put(uuid, new TailInfo(uuid, true, typeid, subtype, tailtexture));
        }*/
        if (e.entityPlayer.isSneaking() && !(Minecraft.getMinecraft().currentScreen instanceof GuiEditTail)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiEditTail());
        }

    	UUID uuid = e.entityPlayer.getPersistentID();
        if (Tails.proxy.hasTailInfo(uuid) && Tails.proxy.getTailInfo(uuid).hastail && !e.entityPlayer.isInvisible()) {
        	TailInfo info = Tails.proxy.getTailInfo(uuid);
        	
        	int type = info.typeid;
        	type = type > tailTypes.length ? 0 : type;
        	
            tailTypes[type].render(e.entityPlayer, info);
        }
    }
    
/*    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
    	TextureHelper.clearTailInfo(e.player);
    }*/

    @SubscribeEvent
    public void onPlayerLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        //TODO this doesn't work as it's called from the client network thread which doesn't have the gl instance in
        Tails.hasRemote = false;
        Tails.proxy.clearAllTailInfo();
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