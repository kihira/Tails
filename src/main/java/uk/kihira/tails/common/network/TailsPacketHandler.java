package uk.kihira.tails.common.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import uk.kihira.tails.Tails;

@EventBusSubscriber(modid = Tails.MOD_ID)
public final class TailsPacketHandler 
{
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event)
    {
        final var registrar = event
                .registrar(PROTOCOL_VERSION)
                .optional();

        registrar.playBidirectional(PlayerDataMessage.TYPE, PlayerDataMessage.STREAM_CODEC, PlayerDataMessage::handleDataCommon);
        registrar.playToClient(PlayerDataMapMessage.TYPE, PlayerDataMapMessage.STREAM_CODEC, PlayerDataMapMessage::handleDataClient);
    }

    // todo move to client only class
    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event)
    {
        event.register(PlayerDataMessage.TYPE, PlayerDataMessage::handleDataCommon);
        //event.register(PlayerDataMapMessage.TYPE, PlayerDataMapMessage::handleDataClient);
    }
}
