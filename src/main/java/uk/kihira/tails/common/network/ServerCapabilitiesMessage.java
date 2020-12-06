package uk.kihira.tails.common.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import uk.kihira.tails.common.Tails;

public class ServerCapabilitiesMessage 
{

    private boolean library;

    public ServerCapabilitiesMessage() {}
    public ServerCapabilitiesMessage(boolean library) 
    {
        this.library = library;
    }

    public ServerCapabilitiesMessage(PacketBuffer buf) 
    {
        library = buf.readBoolean();
    }

    public void encode(PacketBuffer buf) 
    {
        buf.writeBoolean(library);
    }

    public void handle(Supplier<Context> ctx) 
    {
        ctx.get().enqueueWork(() ->
        {
            Tails.libraryEnabled = this.library;
        });
        ctx.get().setPacketHandled(true);
    }
}
