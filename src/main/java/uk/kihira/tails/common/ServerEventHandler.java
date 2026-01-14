package uk.kihira.tails.common;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import uk.kihira.tails.Tails;
import uk.kihira.tails.common.network.PlayerDataMapMessage;

@EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.DEDICATED_SERVER)
public class ServerEventHandler 
{
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        //Send current known tails to client
        PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new PlayerDataMapMessage(Tails.GSON.toJson(OutfitManager.getActiveOutfits())));
        Tails.LOGGER.debug(String.format("Sent tail data of size %d to %s ", OutfitManager.getActiveOutfits().size(), event.getEntity().getName()));
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        //Server doesn't save tails so we discard
        OutfitManager.removeActiveOutfit(event.getEntity().getGameProfile().id());
    }
}
