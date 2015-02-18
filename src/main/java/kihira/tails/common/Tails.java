/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.relauncher.Side;
import kihira.tails.client.FakeEntity;
import kihira.tails.client.render.FakeEntityRenderHelper;
import kihira.tails.client.render.PlayerRenderHelper;
import kihira.tails.client.render.RenderPart;
import kihira.tails.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(modid = Tails.MOD_ID, name = "Tails", version = "@VERSION@", dependencies = "after:foxlib")
public class Tails {

    public static final String MOD_ID = "Tails";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
    public static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static Configuration configuration;
    public static boolean libraryEnabled;
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
        Tails.proxy.init();

        if (e.getSide().isClient()) {
            Tails.configuration = new Configuration(e.getSuggestedConfigurationFile());
            loadConfig();

            RenderPart.registerRenderHelper(EntityPlayer.class, new PlayerRenderHelper());
            RenderPart.registerRenderHelper(FakeEntity.class, new FakeEntityRenderHelper());
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
        if (versions.containsKey(MOD_ID)) {
            String clientVer = Loader.instance().getReversedModObjectList().get(this).getVersion();
            if (VersionParser.parseRange("[" + clientVer + ",)")
                    .containsVersion(new DefaultArtifactVersion(versions.get(MOD_ID)))) {
                logger.warn(String.format("Remote version not in acceptable version bounds! Local (%s) is %s, Remote is %s", side.toString(), clientVer, versions.get(MOD_ID)));
            }
            else {
                logger.debug(String.format("Remote version is in acceptable version bounds. Local (%s) is %s, Remote is %s", side.toString(), clientVer, versions.get(MOD_ID)));
                hasRemote = true;
            }
        }
        return true;
    }

    public void loadConfig() {
        //Load local player info
        try {
            //Load Player Data
            localPartsData = gson.fromJson(Tails.configuration.getString("Local Player Data",
                    Configuration.CATEGORY_GENERAL, "Local Players data. Delete to remove all customisation data. Do not try to edit manually", ""), PartsData.class);

            //Load old tail info if exists
            PartInfo tailInfo = null;
            if (Tails.configuration.hasKey(Configuration.CATEGORY_GENERAL, "Local Tail Info")) {
                tailInfo = gson.fromJson(Tails.configuration.getString("Local Tail Info",
                        Configuration.CATEGORY_GENERAL, "DEPRECIATED. CAN SAFELY REMOVE", ""), PartInfo.class);
            }
            if (tailInfo != null) {
                if (localPartsData == null) localPartsData = new PartsData();
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

        libraryEnabled = configuration.getBoolean("Enable Library", Configuration.CATEGORY_GENERAL, true, "Whether to enable the library system for sharing tails. This mostly matters on servers.");

        if (Tails.configuration.hasChanged()) {
            Tails.configuration.save();
        }
    }

    public static void setLocalPartsData(PartsData partsData) {
        localPartsData = partsData;

        Property prop = Tails.configuration.get(Configuration.CATEGORY_GENERAL, "Local Player Data", "");
        prop.set(gson.toJson(localPartsData));

        Tails.configuration.save();
    }
}
