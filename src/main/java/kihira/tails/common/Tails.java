/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import kihira.tails.client.FakeEntity;
import kihira.tails.client.render.FakeEntityRenderHelper;
import kihira.tails.client.render.PlayerRenderHelper;
import kihira.tails.client.render.RenderPart;
import kihira.tails.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(modid = Tails.MOD_ID, name = "Tails", version = "@VERSION@")
public class Tails {

    public static final String MOD_ID = "Tails";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static Configuration configuration;
    public static boolean hasRemote;

    @SidedProxy(clientSide = "kihira.tails.proxy.ClientProxy", serverSide = "kihira.tails.proxy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance
    public static Tails instance;

    /**
     * This is the {@link PartInfo} for the local player
     */
    public static PartsData localPartsData;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        if (FoxLibManager.checkFoxlib()) {
            Tails.proxy.registerHandlers();
            Tails.proxy.registerMessages();

            if (e.getSide().isClient()) {
                Tails.configuration = new Configuration(e.getSuggestedConfigurationFile());
                loadConfig();

                PlayerRenderHelper helper = new PlayerRenderHelper();
                RenderPart.registerRenderHelper(EntityClientPlayerMP.class, helper);
                RenderPart.registerRenderHelper(EntityOtherPlayerMP.class, helper);
                RenderPart.registerRenderHelper(FakeEntity.class, new FakeEntityRenderHelper());
            }
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
            //Load Player Data
            localPartsData = new Gson().fromJson(Tails.configuration.getString("Local Player Data",
                    Configuration.CATEGORY_GENERAL, "Local Players data. Delete to remove all customisation data. Do not try to edit manually", ""), PartsData.class);

            //Load old tail info if exists
            PartInfo tailInfo = null;
            if (Tails.configuration.hasKey(Configuration.CATEGORY_GENERAL, "Local Tail Info")) {
                tailInfo = new Gson().fromJson(Tails.configuration.getString("Local Tail Info",
                        Configuration.CATEGORY_GENERAL, "DEPRECIATED. CAN SAFELY REMOVE", ""), PartInfo.class);
            }
            if (tailInfo != null) {
                if (localPartsData == null) localPartsData = new PartsData(Minecraft.getMinecraft().thePlayer.getUniqueID());
                tailInfo.partType = PartsData.PartType.TAIL;
                localPartsData.setPartInfo(PartsData.PartType.TAIL, tailInfo);

                //Delete old info
                Property prop = Tails.configuration.get(Configuration.CATEGORY_GENERAL, "Local Tail Info", "");
                prop.set("");

                //Force save
                setLocalPartsData(localPartsData);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        if (Tails.configuration.hasChanged()) {
            Tails.configuration.save();
        }
    }

    public static void setLocalPartsData(PartsData partsData) {
        localPartsData = partsData;

        Property prop = Tails.configuration.get(Configuration.CATEGORY_GENERAL, "Local Player Data", "");
        prop.set(new Gson().toJson(localPartsData));

        Tails.configuration.save();
    }
}
