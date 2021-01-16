package uk.kihira.tails.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.ServerEventHandler;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonProxy 
{
    protected final HashMap<UUID, Outfit> activeOutfits = new HashMap<>(); // todo move this out of proxy? Will be removed in cloud save stuff most likely
    protected LibraryManager libraryManager;

    public void preInit() 
    {
        registerMessages();
        registerHandlers();
        libraryManager = new LibraryManager();
    }

    public void onResourceManagerReload() {}

    protected void registerMessages() 
    {
        int packetId = 0;
        TailsPacketHandler.networkWrapper.registerMessage(packetId++, PlayerDataMessage.class, PlayerDataMessage::encode, PlayerDataMessage::new, PlayerDataMessage::handle);
        TailsPacketHandler.networkWrapper.registerMessage(packetId++, PlayerDataMapMessage.class, PlayerDataMapMessage::encode, PlayerDataMapMessage::new, PlayerDataMapMessage::handle);
        TailsPacketHandler.networkWrapper.registerMessage(packetId++, LibraryEntriesMessage.class, LibraryEntriesMessage::encode, LibraryEntriesMessage::new, LibraryEntriesMessage::handle);
        TailsPacketHandler.networkWrapper.registerMessage(packetId++, LibraryRequestMessage.class, LibraryRequestMessage::encode, LibraryRequestMessage::new, LibraryRequestMessage::handle);
        TailsPacketHandler.networkWrapper.registerMessage(packetId++, ServerCapabilitiesMessage.class, ServerCapabilitiesMessage::encode, ServerCapabilitiesMessage::new, ServerCapabilitiesMessage::handle);
    }

    protected void registerHandlers() 
    {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }

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
                TailsPacketHandler.networkWrapper.send(PacketDistributor.ALL.noArg(), new PlayerDataMessage(uuid, this.activeOutfits.get(uuid), true));
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

    public LibraryManager getLibraryManager() 
    {
        return libraryManager;
    }
}