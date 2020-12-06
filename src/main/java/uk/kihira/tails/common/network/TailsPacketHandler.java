package uk.kihira.tails.common.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import uk.kihira.tails.common.Tails;

public final class TailsPacketHandler 
{
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel networkWrapper = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Tails.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        TailsPacketHandler::versionCheck);

    private static boolean versionCheck(String serverVersion) 
    {
        if (serverVersion.equals(NetworkRegistry.ABSENT) || serverVersion.equals(NetworkRegistry.ACCEPTVANILLA))
        {
            Tails.LOGGER.info("Connecting to vanilla server, or mod is missing from server");
            Tails.instance.hasRemote = false; // TODO better pattern
        }
        else if (serverVersion.equals(PROTOCOL_VERSION))
        {
            Tails.LOGGER.info("Connecting to server that has Tails mod installed and acceptable version");
            Tails.instance.hasRemote = true;
        }
        else
        {
            Tails.LOGGER.warn("Unknown server version %s!", serverVersion);
        }

        return true;
    }
}
