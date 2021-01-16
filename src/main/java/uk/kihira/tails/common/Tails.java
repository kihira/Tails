package uk.kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.proxy.ClientProxy;
import uk.kihira.tails.proxy.CommonProxy;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod(Tails.MOD_ID)
public class Tails 
{
    public static final String MOD_ID = "tails";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder().create();
    public static final boolean DEBUG = true;

    public static boolean hasRemote;

    public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public Tails()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigChange);
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListener);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        proxy.preInit();
        Config.loadConfig();

        PartRegistry.loadAllPartsFromResources();
    }

    private void addReloadListener(final AddReloadListenerEvent event)
    {
        event.addListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                CompletableFuture.runAsync(() -> proxy.onResourceManagerReload()));
    }

    public void onConfigChange(final ModConfigEvent event)
    {
        if (event.getConfig().getModId().equals(Tails.MOD_ID)) 
        {
            Config.loadConfig();
        }
    }

    public static void setLocalOutfit(final Outfit outfit)
    {
        Config.localOutfit.set(outfit);
        Config.configuration.save();
    }
}
