package uk.kihira.tails.common;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import uk.kihira.tails.Tails;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.PlayerDataMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OutfitManager
{
    private static final HashMap<UUID, Outfit> activeOutfits = new HashMap<>();

    public static void setOutfitForPlayer(UUID uuid, Outfit outfit)
    {
        setOutfitForPlayer(uuid, outfit, false);
    }

    public static void setOutfitForPlayer(UUID uuid, Outfit outfit, boolean preview)
    {
        // TODO a ClientOutfitManager might be better
        if (FMLEnvironment.getDist().isClient() && !preview)
        {
            Config.CONFIG.setLocalOutfit(outfit);
            if (Minecraft.getInstance().getCurrentServer() != null)
            {
                ClientPacketDistributor.sendToServer(new PlayerDataMessage(Minecraft.getInstance().getGameProfile().id(), outfit));
            }
        }

        if (FMLEnvironment.getDist().isDedicatedServer())
        {
            PacketDistributor.sendToAllPlayers(new PlayerDataMessage(uuid, activeOutfits.get(uuid)));
        }

        activeOutfits.put(uuid, outfit);
        Tails.LOGGER.debug("Added outfit for {}: {}", uuid.toString(), outfit);
    }

    public static void removeOutfitForPlayer(UUID uuid)
    {
        if (hasOutfit(uuid))
        {
            if (FMLEnvironment.getDist().isDedicatedServer())
            {
                PacketDistributor.sendToAllPlayers(new PlayerDataMessage(uuid, activeOutfits.get(uuid)));
            }
            activeOutfits.remove(uuid);
            Tails.LOGGER.debug("Removed outfit for {}", uuid.toString());
        }
    }

    public static void clearAllOutfits()
    {
        activeOutfits.clear();
        Tails.LOGGER.debug("Clearing all outfits");
    }

    public static boolean hasOutfit(UUID uuid)
    {
        return activeOutfits.containsKey(uuid);
    }

    public static Outfit getOutfitForPlayer(UUID uuid)
    {
        return activeOutfits.get(uuid);
    }

    public static Map<UUID, Outfit> getAllOutfits()
    {
        return activeOutfits;
    }

    @EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
    public static class ClientOutfitManagerEventHandler
    {
        @SubscribeEvent
        public static void onConnectToServer(ClientPlayerNetworkEvent.LoggingIn event)
        {
            // Send our local outfit to the server
            var playerUUID = event.getPlayer().getUUID();
            ClientPacketDistributor.sendToServer(new PlayerDataMessage(playerUUID, OutfitManager.getOutfitForPlayer(playerUUID)));
        }

        @SubscribeEvent
        public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut e)
        {
            // If we don't have a connection, it's likely we're starting up an integrated server
            if (e.getConnection() != null)
            {
                // TODO don't clear local player outfit
                OutfitManager.clearAllOutfits();
            }
        }
    }

    @EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.DEDICATED_SERVER)
    public static class ServerOutfitManagerEventHandler
    {
        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
        {
            //Send current known outfits to client
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new PlayerDataMapMessage(Tails.GSON.toJson(OutfitManager.getAllOutfits())));
            Tails.LOGGER.debug("Sent {} outfits of to {} ", OutfitManager.getAllOutfits().size(), event.getEntity().getName());
        }

        @SubscribeEvent
        public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
        {
            //Server doesn't save outfits so we discard
            // TODO server should save outfits if configured to do so; useful for rendering player heads etc
            OutfitManager.removeOutfitForPlayer(event.getEntity().getGameProfile().id());
        }
    }
}
