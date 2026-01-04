package uk.kihira.tails;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.proxy.CommonProxy;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Mod(Tails.MOD_ID)
@EventBusSubscriber(modid = Tails.MOD_ID)
public class Tails 
{
    public static final String MOD_ID = "tails";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder().create();
    public static final boolean DEBUG = true;

    public static boolean hasRemote;

    public static CommonProxy proxy = new CommonProxy();

    public Tails(IEventBus modEventBus, ModContainer container)
    {
        container.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC);
    }

    @SubscribeEvent
    public static void onClientStarting(ClientStartedEvent event)
    {
        try
        {
            PartRegistry.loadAllPartsFromResources().get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        // todo temp
        var outfit = new Outfit();
        outfit.parts.add(new OutfitPart(PartRegistry.getPart(UUID.fromString("b783d4b9-dd0e-41bb-8aa3-87efac967c19")).get()));
        Config.CONFIG.setLocalOutfit(outfit);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        //PartRegistry.loadAllPartsFromResources();
    }
}
