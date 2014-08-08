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

package kihira.tails;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.util.Arrays;
import java.util.List;

@Mod(modid = Tails.MOD_ID, name = "Tails", version = "$version", guiFactory = "kihira.tails.TailsGuiFactory", acceptableRemoteVersions = "*")
public class Tails {

    public static final String MOD_ID = "Tails";

    public static Configuration configuration;
    public static List<String> userList;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        if (e.getSide().isClient()) {
            EventHandler eventHandler = new EventHandler();
            MinecraftForge.EVENT_BUS.register(eventHandler);
            FMLCommonHandler.instance().bus().register(eventHandler);
            FMLCommonHandler.instance().bus().register(this);

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
