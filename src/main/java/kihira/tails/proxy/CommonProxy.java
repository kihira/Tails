/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.proxy;

import kihira.tails.common.LibraryManager;
import kihira.tails.common.PartsData;
import kihira.tails.common.ServerEventHandler;
import kihira.tails.common.Tails;
import kihira.tails.common.network.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonProxy {

    protected final HashMap<UUID, PartsData> partsData = new HashMap<UUID, PartsData>();
    protected LibraryManager libraryManager;

    public void init() {
        registerMessages();
        registerHandlers();
        libraryManager = new LibraryManager();
    }

    public void registerMessages() {
        Tails.networkWrapper.registerMessage(PlayerDataMessage.Handler.class, PlayerDataMessage.class, 0, Side.SERVER);
        Tails.networkWrapper.registerMessage(PlayerDataMapMessage.Handler.class, PlayerDataMapMessage.class, 1, Side.SERVER);
        Tails.networkWrapper.registerMessage(LibraryEntriesMessage.Handler.class, LibraryEntriesMessage.class, 2, Side.SERVER);
        Tails.networkWrapper.registerMessage(LibraryRequestMessage.Handler.class, LibraryRequestMessage.class, 3, Side.SERVER);
        Tails.networkWrapper.registerMessage(ServerCapabilitiesMessage.Handler.class, ServerCapabilitiesMessage.class, 4, Side.SERVER);
    }

    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }

    public void registerRenderers() {}

    public void addPartsData(UUID uuid, PartsData partsData) {
        if (uuid != null) {
            this.partsData.put(uuid, partsData);
            Tails.logger.debug(String.format("Added part data for %s: %s", uuid.toString(), partsData));
        }
        else Tails.logger.warn(String.format("Attempted to add part data with null UUID! %s", partsData));
    }

    public void removePartsData(UUID uuid) {
        if (hasPartsData(uuid)) {
            if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
                //Tell client to remove textures
                //Tails.networkWrapper.sendToAll(new PlayerDataMessage(uuid, this.partsData.get(uuid), true));
            }
            this.partsData.remove(uuid);
            Tails.logger.debug(String.format("Removed part data for %s", uuid.toString()));
        }
    }

    public void clearAllPartsData() {
        this.partsData.clear();
        Tails.logger.debug("Clearing parts data");
    }

    public boolean hasPartsData(UUID uuid) {
        return uuid != null && this.partsData.containsKey(uuid);
    }

    public PartsData getPartsData(UUID uuid) {
        return this.partsData.get(uuid);
    }

    public Map<UUID, PartsData> getPartsData() {
        return this.partsData;
    }

    public LibraryManager getLibraryManager() {
        return libraryManager;
    }
}