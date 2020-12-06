package uk.kihira.tails.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.ServerCapabilitiesMessage;
import uk.kihira.tails.common.network.TailsPacketHandler;

public class ServerEventHandler 
{
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) 
    {
        //Send current known tails to client
        TailsPacketHandler.networkWrapper.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new PlayerDataMapMessage(Tails.proxy.getActiveOutfits()));
        TailsPacketHandler.networkWrapper.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new ServerCapabilitiesMessage(Config.libraryEnabled.get()));
        Tails.LOGGER.debug(String.format("Sent tail data of size %d to %s ", Tails.proxy.getActiveOutfits().size(), event.getPlayer().getName()));
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) 
    {
        //Server doesn't save tails so we discard
        Tails.proxy.removeActiveOutfit(PlayerEntity.getUUID(event.getPlayer().getGameProfile()));
    }
}
