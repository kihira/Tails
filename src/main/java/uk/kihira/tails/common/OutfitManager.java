package uk.kihira.tails.common;

import net.neoforged.neoforge.network.PacketDistributor;
import uk.kihira.tails.Tails;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.network.PlayerDataMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OutfitManager
{
    private static final HashMap<UUID, Outfit> activeOutfits = new HashMap<>();

    public static void setActiveOutfit(UUID uuid, Outfit outfit)
    {
        activeOutfits.put(uuid, outfit);
        Tails.LOGGER.debug("Added outfit for {}: {}", uuid.toString(), outfit);
    }

    public static void removeActiveOutfit(UUID uuid)
    {
        if (hasActiveOutfit(uuid))
        {
            //if (FMLCommonHandler.instance().getEffectiveSide().isServer())
            {
                //todo Tell client to remove textures
                PacketDistributor.sendToAllPlayers(new PlayerDataMessage(uuid, activeOutfits.get(uuid), true));
            }
            activeOutfits.remove(uuid);
            Tails.LOGGER.debug("Removed part data for {}", uuid.toString());
        }
    }

    public static void clearAllPartsData()
    {
        activeOutfits.clear();
        Tails.LOGGER.debug("Clearing parts data");
    }

    public static boolean hasActiveOutfit(UUID uuid)
    {
        return activeOutfits.containsKey(uuid);
    }

    public static Outfit getActiveOutfit(UUID uuid)
    {
        return activeOutfits.get(uuid);
    }

    public static Map<UUID, Outfit> getActiveOutfits()
    {
        return activeOutfits;
    }
}
