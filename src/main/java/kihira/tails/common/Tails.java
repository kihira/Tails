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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;
import kihira.tails.client.event.EventGuiTick;
import kihira.tails.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Mod(modid = Tails.MOD_ID, name = "Tails", version = "$version")
public class Tails {

    public static final String MOD_ID = "Tails";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static Configuration configuration;
    public static boolean hasRemote;
    public static final String minFoxlibVersion = "0.4.1.24";

    @SidedProxy(clientSide = "kihira.tails.proxy.ClientProxy", serverSide = "kihira.tails.proxy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance
    public static Tails instance;

    /**
     * This is the {@link TailInfo} for the local player
     */
    public static TailInfo localPlayerTailInfo;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        if (!isFoxlibInstalled()) {
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                removeOudatedFoxlib();
                downloadFoxlib();
                FMLCommonHandler.instance().bus().register(new EventGuiTick());
            } else {
                FMLLog.bigWarning("You need FoxLib to use Tails! You can download it here: " + "http://minecraft.curseforge.com/mc-mods/223291-foxlib/files/latest");
                FMLServerHandler.instance().getServer().initiateShutdown();
            }
            return;
        }

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
        //Load local player info
        try {
            localPlayerTailInfo = new Gson().fromJson(Tails.configuration.getString("Local Tail Info",
                    Configuration.CATEGORY_GENERAL, "Local Players tail info. Delete to remove tail. Do not try to edit manually", ""), TailInfo.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        if (Tails.configuration.hasChanged()) {
            Tails.configuration.save();
        }
    }

    public static void setLocalPlayerTailInfo(TailInfo tailInfo) {
        localPlayerTailInfo = tailInfo;

        Property prop = Tails.configuration.get(Configuration.CATEGORY_GENERAL, "Local Tail Info", "");
        prop.comment = "Local Players tail info. Delete to remove tail. Do not try to edit manually";
        prop.set(new Gson().toJson(localPlayerTailInfo));

        Tails.configuration.save();
    }

    public static boolean isFoxlibInstalled() {
        return Loader.isModLoaded("foxlib@[" + minFoxlibVersion + ",)");
    }
    
    @SideOnly(Side.CLIENT)
    public void removeOudatedFoxlib() {
        try {
            File modsDir =  new File(Minecraft.getMinecraft().mcDataDir + File.separator + "mods" + File.separator);
            File installedMods[] = modsDir.listFiles();
            for (int i = 0; i < installedMods.length; i++) {
                String name = installedMods[i].getName();
                if (name.contains("FoxLib")) {
                    installedMods[i].deleteOnExit();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    public void downloadFoxlib() {
        try {
            FileUtils.copyURLToFile(new URL("http://maven.kihirakreations.co.uk/kihira/foxlib/FoxLib/1.7.10-" + minFoxlibVersion + "/FoxLib-1.7.10-" + minFoxlibVersion ".jar"), new File(Minecraft.getMinecraft().mcDataDir + File.separator + "mods" + File.separator + "FoxLib-1.7.10-" + minFoxlibVersion + ".jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
