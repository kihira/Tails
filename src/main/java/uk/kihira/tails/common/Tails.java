package uk.kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.proxy.CommonProxy;

import java.util.Map;

@Mod(Tails.MOD_ID)
public class Tails 
{
    public static final String MOD_ID = "tails";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder().create();
    public static final boolean DEBUG = true;

    public static boolean hasRemote;

    @SidedProxy(clientSide = "uk.kihira.tails.proxy.ClientProxy", serverSide = "uk.kihira.tails.proxy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance(value = "tails")
    public static Tails instance;

    public Tails()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        proxy.preInit();
        proxy.postInit();

        PartRegistry.loadAllPartsFromResources();
    }

    private void setupClient(final FMLClientSetupEvent event)
    {
        Config.loadConfig();
    }

    @SubscribeEvent
    public void onConfigChange(ModConfigEvent event) 
    {
        if (event.getConfig().getModId().equals(Tails.MOD_ID)) 
        {
            Config.loadConfig();
        }
    }

    public static void setLocalOutfit(Outfit outfit) 
    {
        Config.localOutfit.set(outfit);
        Config.configuration.save();
    }
}
