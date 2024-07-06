package uk.kihira.tails.common;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import uk.kihira.tails.common.network.PlayerDataMapMessage;

@Mod.EventBusSubscriber(modid = Tails.MOD_ID, bus = Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class ServerEventHandler 
{
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        //Send current known tails to client
        PacketDistributor.PLAYER.with((ServerPlayer) event.getEntity()).send(new PlayerDataMapMessage(Tails.proxy.getActiveOutfits()));
        Tails.LOGGER.debug(String.format("Sent tail data of size %d to %s ", Tails.proxy.getActiveOutfits().size(), event.getEntity().getName()));
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        //Server doesn't save tails so we discard
        Tails.proxy.removeActiveOutfit(event.getEntity().getGameProfile().getId());
    }
}
