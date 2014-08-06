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
import kihira.tails.render.RenderDragonTail;
import kihira.tails.render.RenderFoxTail;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class EventHandler {

    RenderDragonTail renderDragonTail = new RenderDragonTail();
    RenderFoxTail renderFoxTail = new RenderFoxTail();

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerRenderTick(RenderPlayerEvent.Specials.Pre e) {
        if (Tails.userList.contains(e.entityPlayer.getCommandSenderName()) && !e.entityPlayer.isInvisible()) {
            renderDragonTail.render(e.entityPlayer);
        }
    }
}
