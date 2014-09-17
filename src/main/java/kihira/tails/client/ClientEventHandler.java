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

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.gui.GuiEditTail;
import kihira.tails.client.render.*;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import kihira.tails.common.network.TailInfoMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {

    //TODO this is not the best way to keep track of tails, enums?
	public static final RenderTail[] tailTypes = { new RenderFoxTail(), new RenderDragonTail(), new RenderRaccoonTail(), new RenderDevilTail()};

    private boolean hasSentTailInfoToServer = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRenderTick(RenderPlayerEvent.Specials.Pre e) {
    	UUID uuid = e.entityPlayer.getGameProfile().getId();
        if (uuid != null && Tails.proxy.hasTailInfo(uuid) && Tails.proxy.getTailInfo(uuid).hastail && !e.entityPlayer.isInvisible()) {
        	TailInfo info = Tails.proxy.getTailInfo(uuid);
        	
        	int type = info.typeid;
        	type = type > tailTypes.length ? 0 : type;
        	
            tailTypes[type].render(e.entityPlayer, info, e.partialRenderTick);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiIngameMenu) {
            event.buttonList.add(new GuiButton(1234, (event.gui.width / 2) - 35, event.gui.height - 25, 70, 20, I18n.format("gui.button.editor")));
        }
    }

    @SubscribeEvent
    public void onButtonClickPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.gui instanceof GuiIngameMenu) {
            if (event.button.id == 1234) {
                event.gui.mc.displayGuiScreen(new GuiEditTail());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        //Add local player texture to map
        if (Tails.localPlayerTailInfo != null) {
            Tails.proxy.addTailInfo(Tails.localPlayerTailInfo.uuid, Tails.localPlayerTailInfo);
        }
    }

    @SubscribeEvent
    public void onPlayerLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        Tails.hasRemote = false;
        this.hasSentTailInfoToServer = false;
        Tails.proxy.clearAllTailInfo(); //TODO This won't delete the textures cause it's called from the wrong thread
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (TextureHelper.needsBuild(e.player)) {
            TextureHelper.buildPlayerInfo(e.player);
        }
        //World can't be null if we want to send a packet it seems
        else if (!this.hasSentTailInfoToServer && Minecraft.getMinecraft().theWorld != null) {
            Tails.networkWrapper.sendToServer(new TailInfoMessage(Tails.localPlayerTailInfo, false));
            this.hasSentTailInfoToServer = true;
        }
    }
}
