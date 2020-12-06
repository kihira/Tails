package uk.kihira.tails.common.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import uk.kihira.tails.common.Tails;

// TODO Is this still needed?
public class LibraryRequestMessage
{
    public LibraryRequestMessage(PacketBuffer buf) {}

    public void encode(PacketBuffer buf) {}

    public void handle(Supplier<Context> ctx) 
    {
        ctx.get().enqueueWork(() ->
        {
            TailsPacketHandler.networkWrapper.reply(new LibraryEntriesMessage(Tails.proxy.getLibraryManager().libraryEntries, false), ctx.get());
        });
        ctx.get().setPacketHandled(true);
    }
}
