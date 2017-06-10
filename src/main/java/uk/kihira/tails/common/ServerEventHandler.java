/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.ServerCapabilitiesMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ServerEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        //Send current known tails to uk.kihira.tails.client
        Tails.networkWrapper.sendTo(new PlayerDataMapMessage(Tails.proxy.getActiveOutfits()), (EntityPlayerMP) event.player);
        Tails.networkWrapper.sendTo(new ServerCapabilitiesMessage(Tails.libraryEnabled), (EntityPlayerMP) event.player);
        Tails.logger.debug(String.format("Sent tail data of size %d to %s ", Tails.proxy.getActiveOutfits().size(), event.player.getName()));
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        //Server doesn't save tails so we discard
        Tails.proxy.removeActiveOutfit(EntityPlayer.getUUID(event.player.getGameProfile()));
    }
}
