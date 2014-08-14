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
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import kihira.tails.client.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

@Mod(modid = Tails.MOD_ID, name = "Tails", version = "$version", guiFactory = "kihira.tails.client.gui.TailsGuiFactory", acceptableRemoteVersions = "*")
public class Tails {

    public static final String MOD_ID = "Tails";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public static Configuration configuration;
    public static List<String> userList;

    @Mod.Instance
    public static Tails instance;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        if (e.getSide().isClient()) {
            ClientEventHandler eventHandler = new ClientEventHandler();
            MinecraftForge.EVENT_BUS.register(eventHandler);
            FMLCommonHandler.instance().bus().register(eventHandler);
            FMLCommonHandler.instance().bus().register(this);
            NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

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

    public void loadConfig() {
        Tails.userList = Arrays.asList(Tails.configuration.getStringList("usernames", Configuration.CATEGORY_GENERAL, new String[] {"Kihira"} , "List of players that have tails"));

        if (Tails.configuration.hasChanged()) {
            Tails.configuration.save();
        }
    }
}
