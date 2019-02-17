package uk.kihira.tails.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.ServerCapabilitiesMessage;

public class ServerEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        //Send current known tails to uk.kihira.tails.client
        Tails.networkWrapper.sendTo(new PlayerDataMapMessage(Tails.proxy.getActiveOutfits()), (EntityPlayerMP) event.getPlayer());
        Tails.networkWrapper.sendTo(new ServerCapabilitiesMessage(Tails.libraryEnabled), (EntityPlayerMP) event.getPlayer());
        Tails.logger.debug(String.format("Sent tail data of size %d to %s ", Tails.proxy.getActiveOutfits().size(), event.getPlayer().getName()));
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        //Server doesn't save tails so we discard
        Tails.proxy.removeActiveOutfit(EntityPlayer.getUUID(event.getPlayer().getGameProfile()));
    }
}
