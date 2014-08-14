/*
 * Copyright (C) 2014  Kihira
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package kihira.tails.common;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import kihira.tails.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mod(modid = Tails.MOD_ID, name = "Tails", version = "$version", guiFactory = "kihira.tails.client.gui.TailsGuiFactory")
public class Tails {

    public static final String MOD_ID = "Tails";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static Configuration configuration;
    public static List<String> userList;
    public static boolean hasRemote;

    @SidedProxy(clientSide = "kihira.tails.proxy.ClientProxy", serverSide = "kihira.tails.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static Tails instance;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        Tails.proxy.registerHandlers();
        Tails.proxy.registerMessages();

        if (e.getSide().isClient()) {
            Tails.configuration = new Configuration(e.getSuggestedConfigurationFile());
            loadConfig();
        }
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(Tails.MOD_ID)) {
            loadConfig();
        }
    }

    @NetworkCheckHandler
    public boolean checkRemoteVersions(Map<String, String> versions, Side side) {
        if (side.isClient()) {
            if (versions.containsKey(MOD_ID)) hasRemote = true;
        }
        return true;
    }

    public void loadConfig() {
        Tails.userList = Arrays.asList(Tails.configuration.getStringList("usernames", Configuration.CATEGORY_GENERAL, new String[] {"Kihira"} , "List of players that have tails"));

        if (Tails.configuration.hasChanged()) {
            Tails.configuration.save();
        }
    }
}
