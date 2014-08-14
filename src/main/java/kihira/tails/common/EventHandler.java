package kihira.tails.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class EventHandler {

    @SubscribeEvent
    public void onPlayerLogin(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        //TODO send tail map to client
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Tails.proxy.removeTailInfo(event.player.getPersistentID());
    }
}
