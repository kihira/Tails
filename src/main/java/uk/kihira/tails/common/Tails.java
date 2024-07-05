package uk.kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.proxy.CommonProxy;

import java.util.concurrent.CompletableFuture;

@Mod(Tails.MOD_ID)
public class Tails 
{
    public static final String MOD_ID = "tails";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder().create();
    public static final boolean DEBUG = true;

    public static boolean hasRemote;

    public static CommonProxy proxy = new CommonProxy();

    public Tails(IEventBus modEventBus)
    {
        modEventBus.addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        //PartRegistry.loadAllPartsFromResources();
    }

    public static void setLocalOutfit(final Outfit outfit)
    {
        Config.localOutfit = outfit;
        Config.SPEC.save();
    }
}
