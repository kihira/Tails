package uk.kihira.tails.common.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import uk.kihira.tails.common.Tails;

@Mod.EventBusSubscriber(modid = Tails.MOD_ID, bus = Bus.MOD)
public final class TailsPacketHandler 
{
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(Tails.MOD_ID)
                .versioned(PROTOCOL_VERSION)
                .optional();

/*        registrar.play(PlayerDataMessage.ID, PlayerDataMessage::new, handler -> handler
                .client(PlayerDataMessage::handleDataClient)
                .server(PlayerDataMessage::handleDataServer));
        registrar.play(PlayerDataMapMessage.ID, PlayerDataMapMessage::new, handler -> handler
                .client(PlayerDataMapMessage::handleDataClient));*/
    }
}
