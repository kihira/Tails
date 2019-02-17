package uk.kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import uk.kihira.tails.client.ClientEventHandler;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.PartRenderer;
import uk.kihira.tails.client.render.FallbackRenderHandler;
import uk.kihira.tails.client.render.LayerPart;
import uk.kihira.tails.common.network.*;
import uk.kihira.tails.proxy.CommonProxy;

import java.util.Map;

@Mod(Tails.MOD_ID)
public class Tails {

    public static final String MOD_ID = "tails";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final SimpleChannel networkWrapper = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "channel"), () -> "1.0", s -> true, s -> true); // todo properly check versions
    public static final Gson gson = new GsonBuilder().create();

    public static ModConfig configuration;
    public static boolean libraryEnabled;
    public static boolean hasRemote;

    public static Outfit localOutfit;

    public Tails() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }

    private void clientInit(final FMLClientSetupEvent event) {
        Tails.configuration = new ModConfig(event.getSuggestedConfigurationFile());
        loadConfig();

        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        // Setup renderers
        boolean legacyRenderer = Tails.configuration.getBoolean(Configuration.CATEGORY_CLIENT, "ForceLegacyRendering", false, "Forces the legacy renderer which may have better compatibility with other mods");
        if (legacyRenderer) Tails.logger.info("Legacy Renderer has been forced enabled");
        else if (ModList.get().isLoaded("SmartMoving")) {
            Tails.logger.info("Legacy Renderer enabled automatically for mod compatibility");
            legacyRenderer = true;
        }

        partRenderer = new PartRenderer();

        if (legacyRenderer) {
            MinecraftForge.EVENT_BUS.register(new FallbackRenderHandler());

            Map<String, RenderPlayer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
            // Default
            ModelPlayer model = skinMap.get("default").getMainModel();
            model.bipedHead.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.HEAD));
            model.bipedBody.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.CHEST));
            model.bipedLeftArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_ARM));
            model.bipedRightArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_ARM));
            model.bipedLeftLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_LEG));
            model.bipedRightLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_LEG));
            // Slim
            model = skinMap.get("slim").getMainModel();
            model.bipedHead.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.HEAD));
            model.bipedBody.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.CHEST));
            model.bipedLeftArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_ARM));
            model.bipedRightArm.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_ARM));
            model.bipedLeftLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.LEFT_LEG));
            model.bipedRightLeg.addChild(new FallbackRenderHandler.ModelRendererWrapper(model, MountPoint.RIGHT_LEG));
        } else {
            Map<String, RenderPlayer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
            // Default
            RenderPlayer renderPlayer = skinMap.get("default");
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedHead, partRenderer, MountPoint.HEAD));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedBody, partRenderer, MountPoint.CHEST));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedLeftArm, partRenderer, MountPoint.LEFT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedRightArm, partRenderer, MountPoint.RIGHT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedLeftLeg, partRenderer, MountPoint.LEFT_LEG));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedRightLeg, partRenderer, MountPoint.RIGHT_LEG));

            // Slim
            renderPlayer = skinMap.get("slim");
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedHead, partRenderer, MountPoint.HEAD));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedBody, partRenderer, MountPoint.CHEST));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedLeftArm, partRenderer, MountPoint.LEFT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedRightArm, partRenderer, MountPoint.RIGHT_ARM));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedLeftLeg, partRenderer, MountPoint.LEFT_LEG));
            renderPlayer.addLayer(new LayerPart(renderPlayer.getMainModel().bipedRightLeg, partRenderer, MountPoint.RIGHT_LEG));
        }
    }

    private void serverInit(final FMLServerStartingEvent event) {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }

    private void init(final FMLCommonSetupEvent event) {
        proxy.preInit();

        networkWrapper.registerMessage(0, PlayerDataMessage.class,
                PlayerDataMessage::toBytes,
                PlayerDataMessage::new,
                PlayerDataMessage::handle);
        networkWrapper.registerMessage(1, PlayerDataMapMessage.class,
                PlayerDataMapMessage::toBytes,
                PlayerDataMapMessage::new,
                PlayerDataMapMessage::handle);

        proxy.postInit();
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tails.MOD_ID)) {
            loadConfig();
        }
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
