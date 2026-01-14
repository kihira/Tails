package uk.kihira.tails;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.common.Config;

import java.util.concurrent.ExecutionException;

@Mod(Tails.MOD_ID)
@EventBusSubscriber(modid = Tails.MOD_ID)
public class Tails 
{
    public static final String MOD_ID = "tails";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder().create();
    public static final boolean DEBUG = true;

    public Tails(ModContainer container)
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
    }
}
