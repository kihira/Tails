package uk.kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import uk.kihira.tails.proxy.CommonProxy;

import java.util.Map;

@Mod(Tails.MOD_ID)
public class Tails {

    public static final String MOD_ID = "tails";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final SimpleChannel networkWrapper = NetworkRegistry.newSimpleChannel(MOD_ID);
    public static final Gson gson = new GsonBuilder().create();

    public static Configuration configuration;
    public static boolean libraryEnabled;
    public static boolean hasRemote;

    @SidedProxy(clientSide = "uk.kihira.tails.proxy.ClientProxy", serverSide = "uk.kihira.tails.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Outfit localOutfit;

    public Tails() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }

    private void clientInit(final FMLClientSetupEvent event) {
        Tails.configuration = new Configuration(e.getSuggestedConfigurationFile());
        loadConfig();
    }

    private void init(final FMLCommonSetupEvent event) {
        proxy.preInit();

        proxy.postInit();
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
            ArtifactVersion clientVer = ModList.get().getModContainerById(Tails.MOD_ID).get().getModInfo().getVersion();
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
            localOutfit = gson.fromJson(Tails.configuration.getString("Local Player Outfit",
                    Configuration.CATEGORY_GENERAL, "{}", "Local Players outfit. Delete to remove all customisation data. Do not try to edit manually"), Outfit.class);

            //Load default if none exists
            if (localOutfit == null) {
                setLocalOutfit(localOutfit = new Outfit());
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

    public static void setLocalOutfit(Outfit outfit) {
        localOutfit = outfit;

        Property prop = Tails.configuration.get(Configuration.CATEGORY_GENERAL, "Local Player Data", "");
        prop.set(gson.toJson(localOutfit));

        Tails.configuration.save();
    }
}
