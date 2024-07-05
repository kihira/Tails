package uk.kihira.tails.proxy;

import net.neoforged.neoforge.network.PacketDistributor;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonProxy 
{
    protected final HashMap<UUID, Outfit> activeOutfits = new HashMap<>(); // todo move this out of proxy? Will be removed in cloud save stuff most likely

    public void setActiveOutfit(UUID uuid, Outfit outfit) 
    {
        this.activeOutfits.put(uuid, outfit);
        Tails.LOGGER.debug(String.format("Added outfit for %s: %s", uuid.toString(), outfit));
    }

    public void removeActiveOutfit(UUID uuid) 
    {
        if (hasActiveOutfit(uuid)) 
        {
            //if (FMLCommonHandler.instance().getEffectiveSide().isServer())
            {
                //todo Tell client to remove textures
                PacketDistributor.ALL.noArg().send(new PlayerDataMessage(uuid, this.activeOutfits.get(uuid), true));
            }
            this.activeOutfits.remove(uuid);
            Tails.LOGGER.debug(String.format("Removed part data for %s", uuid.toString()));
        }
    }

    public void clearAllPartsData() 
    {
        this.activeOutfits.clear();
        Tails.LOGGER.debug("Clearing parts data");
    }

    public boolean hasActiveOutfit(UUID uuid) 
    {
        return this.activeOutfits.containsKey(uuid);
    }

    public Outfit getActiveOutfit(UUID uuid) 
    {
        return this.activeOutfits.get(uuid);
    }

    public Map<UUID, Outfit> getActiveOutfits() 
    {
        return this.activeOutfits;
    }
}