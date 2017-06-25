/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.render.*;
import uk.kihira.tails.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Map;

@Mod(modid = Tails.MOD_ID, name = "Tails", version = "@VERSION@")
public class Tails {

    public static final String MOD_ID = "tails";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
    public static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(PartsData.class, new PartsDataDeserializer())
            .create();

    public static Configuration configuration;
    public static boolean libraryEnabled;
    public static boolean hasRemote;

    @SidedProxy(clientSide = "uk.kihira.tails.proxy.ClientProxy", serverSide = "uk.kihira.tails.proxy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance(value = "tails")
    public static Tails instance;

    public static Outfit localOutfit;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        Tails.proxy.init();

        if (e.getSide().isClient()) {
            Tails.configuration = new Configuration(e.getSuggestedConfigurationFile());
            loadConfig();

            LegacyPartRenderer.registerRenderHelper(EntityPlayer.class, new PlayerRenderHelper());
            LegacyPartRenderer.registerRenderHelper(FakeEntity.class, new FakeEntityRenderHelper());
        }
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent e) {
        // todo
/*        if (e.getSide() == Side.CLIENT && Loader.isModLoaded("Botania") && VersionParser.parseRange("[r1.7-205,)").containsVersion(Loader.instance().getIndexedModList().get("Botania").getProcessedVersion())) {
            logger.debug(String.format("Botania (%s) found, loading Foxtato renderer", Loader.instance().getIndexedModList().get("Botania").getProcessedVersion().getVersionString()));
            MinecraftForge.EVENT_BUS.register(new FoxtatoRender());
            LegacyPartRenderer.registerRenderHelper(FoxtatoRender.FoxtatoFakeEntity.class, (entity, tail, info, x, y, z) -> {
                switch (info.partType) {
                    case TAIL: {
                        GlStateManager.translate(0F, 1.325F, 0.125F);
                        GlStateManager.scale(0.25F, 0.25F, 0.25F);
                        break;
                    }
                    case EARS: {
                        GlStateManager.translate(0F, 1.375F, -0.1F);
                        GlStateManager.scale(0.5F, 0.5F, 0.5F);
                        break;
                    }
                }
            });
        }
        else {
            logger.debug("Valid Botania not found, skipping Foxtato renderer");
        }*/

        proxy.registerRenderers(Loader.isModLoaded("SmartMoving"));
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tails.MOD_ID)) {
            loadConfig();
        }
    }

    @NetworkCheckHandler
    public boolean checkRemoteVersions(Map<String, String> versions, Side side) {
        if (versions.containsKey(MOD_ID)) {
            String clientVer = Loader.instance().getReversedModObjectList().get(this).getVersion();
            if (!VersionParser.parseRange("[" + clientVer + ",)").containsVersion(new DefaultArtifactVersion(versions.get(MOD_ID)))) {
                logger.warn(String.format("Remote version not in acceptable version bounds! Local is %s, Remote (%s) is %s", clientVer, side.toString(), versions.get(MOD_ID)));
            }
            else {
                logger.debug(String.format("Remote version is in acceptable version bounds. Local is %s, Remote (%s) is %s", clientVer, side.toString(), versions.get(MOD_ID)));
                hasRemote = true;
            }
        }
        return true;
    }

    public void loadConfig() {
        //Load local player info
        try {
            //Load Player Data
            localOutfit = gson.fromJson(Tails.configuration.getString("Local Player Data",
                    Configuration.CATEGORY_GENERAL, "{}", "Local Players data. Delete to remove all customisation data. Do not try to edit manually"), PartsData.class);

            //Load old tail info if exists
            PartInfo tailInfo = null;
            if (Tails.configuration.hasKey(Configuration.CATEGORY_GENERAL, "Local Tail Info")) {
                tailInfo = gson.fromJson(Tails.configuration.getString("Local Tail Info",
                        Configuration.CATEGORY_GENERAL, "DEPRECIATED. CAN SAFELY REMOVE", ""), PartInfo.class);
            }
            if (tailInfo != null) {
                if (localOutfit == null) localOutfit = new PartsData();
                tailInfo.partType = PartsData.PartType.TAIL;
                localOutfit.setPartInfo(PartsData.PartType.TAIL, tailInfo);

                //Delete old info
                Property prop = Tails.configuration.get(Configuration.CATEGORY_GENERAL, "Local Tail Info", "");
                prop.set("");

                //Force save
                setLocalOutfit(localOutfit);
            }

            //Load default if none exists
            if (localOutfit == null) {
                localOutfit = new PartsData();
                for (PartsData.PartType partType : PartsData.PartType.values()) {
                    localOutfit.setPartInfo(partType, PartInfo.none(partType));
                }
                setLocalOutfit(localOutfit);
            }
        } catch (JsonSyntaxException e) {
            Tails.configuration.getCategory(Configuration.CATEGORY_GENERAL).remove("Local Player Data");
            Tails.logger.error("Failed to load local player data: Invalid JSON syntax! Invalid data being removed");
        }

        libraryEnabled = configuration.getBoolean("Enable Library", Configuration.CATEGORY_GENERAL, true, "Whether to enable the library system for sharing tails. This mostly matters on servers.");

        if (Tails.configuration.hasChanged()) {
            Tails.configuration.save();
        }
    }

    public static void setLocalOutfit(PartsData partsData) {
        localOutfit = partsData;

        Property prop = Tails.configuration.get(Configuration.CATEGORY_GENERAL, "Local Player Data", "");
        prop.set(gson.toJson(localOutfit));

        Tails.configuration.save();
    }
}
