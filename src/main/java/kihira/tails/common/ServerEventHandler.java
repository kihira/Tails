package kihira.tails.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import kihira.tails.common.network.TailMapMessage;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        //Send current known tails to client
        //UUID uuid = UUID.fromString("7cdaf700590f421082047933f8af67f3");
        //Tails.proxy.addTailInfo(uuid, new TailInfo(uuid, true, 0, 0, -1803209, -1938144, -592395, null));
        Tails.networkWrapper.sendTo(new TailMapMessage(Tails.proxy.getTailMap()), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        //Server doesn't save tails so we discard
        Tails.proxy.removeTailInfo(event.player.getGameProfile().getId());
    }
}
