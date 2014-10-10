/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kihira.tails.common.PartsData;
import kihira.tails.common.ServerEventHandler;
import kihira.tails.common.Tails;
import kihira.tails.common.network.PlayerDataMapMessage;
import kihira.tails.common.network.PlayerDataMessage;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public class CommonProxy {

    protected Hashtable<UUID, PartsData> partsData = new Hashtable<UUID, PartsData>();

    public void registerMessages() {
        Tails.networkWrapper.registerMessage(PlayerDataMessage.Handler.class, PlayerDataMessage.class, 0, Side.SERVER);
        Tails.networkWrapper.registerMessage(PlayerDataMapMessage.Handler.class, PlayerDataMapMessage.class, 1, Side.SERVER);
    }

    public void registerHandlers() {
        FMLCommonHandler.instance().bus().register(new ServerEventHandler());
    }

    public void addPartsData(UUID uuid, PartsData partsData) {
        if (uuid != null) {
            this.partsData.put(uuid, partsData);
        }
    }

    public void removePartsData(UUID uuid) {
        if (hasPartsData(uuid)) {
            if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
                //Tell client to remove textures
                Tails.networkWrapper.sendToAll(new PlayerDataMessage(this.partsData.get(uuid), true));
            }
            this.partsData.remove(uuid);
        }
    }

    public void clearAllPartsData() {
        this.partsData.clear();
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
}