package kihira.tails.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import kihira.tails.common.network.TailMapMessage;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        //Send current known tails to client
        Tails.networkWrapper.sendTo(new TailMapMessage(Tails.proxy.getTailMap()), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        //Server doesn't save tails so we discard
        Tails.proxy.removeTailInfo(event.player.getGameProfile().getId());
    }
}
