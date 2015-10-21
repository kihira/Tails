/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.proxy;

import kihira.tails.client.ClientEventHandler;
import kihira.tails.common.LibraryManager;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import kihira.tails.common.network.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        registerMessages();
        registerHandlers();
        libraryManager = new LibraryManager.ClientLibraryManager();
    }

    @Override
    public void addPartsData(UUID uuid, PartsData partsData) {
        if (hasPartsData(uuid)) {
            this.partsData.get(uuid).clearTextures();
        }

        super.addPartsData(uuid, partsData);
    }

    @Override
    public void removePartsData(UUID uuid) {
        if (hasPartsData(uuid)) {
            this.partsData.get(uuid).clearTextures();
        }
        super.removePartsData(uuid);
    }

    @Override
    public void clearAllPartsData() {
        for (PartsData partInfo : this.partsData.values()) {
            partInfo.clearTextures();
        }
        super.clearAllPartsData();
    }

    @Override
    public void registerMessages() {
        Tails.networkWrapper.registerMessage(PlayerDataMessage.Handler.class, PlayerDataMessage.class, 0, Side.CLIENT);
        Tails.networkWrapper.registerMessage(PlayerDataMapMessage.Handler.class, PlayerDataMapMessage.class, 1, Side.CLIENT);
        Tails.networkWrapper.registerMessage(LibraryEntriesMessage.Handler.class, LibraryEntriesMessage.class, 2, Side.CLIENT);
        Tails.networkWrapper.registerMessage(LibraryRequestMessage.Handler.class, LibraryRequestMessage.class, 3, Side.CLIENT);
        Tails.networkWrapper.registerMessage(ServerCapabilitiesMessage.Handler.class, ServerCapabilitiesMessage.class, 4, Side.CLIENT);
        super.registerMessages();
    }

    @Override
    public void registerHandlers() {
        ClientEventHandler eventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);

        //super.registerHandlers();
    }
}
