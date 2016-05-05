/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.common;

import kihira.tails.common.network.PlayerDataMapMessage;
import kihira.tails.common.network.ServerCapabilitiesMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ServerEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        //Send current known tails to client
        Tails.networkWrapper.sendTo(new PlayerDataMapMessage(Tails.proxy.getPartsData()), (EntityPlayerMP) event.player);
        Tails.networkWrapper.sendTo(new ServerCapabilitiesMessage(Tails.libraryEnabled), (EntityPlayerMP) event.player);
        Tails.logger.debug(String.format("Sent tail data of size %d to %s ", Tails.proxy.getPartsData().size(), event.player.getName()));
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        //Server doesn't save tails so we discard
        Tails.proxy.removePartsData(EntityPlayer.getUUID(event.player.getGameProfile()));
    }
}
