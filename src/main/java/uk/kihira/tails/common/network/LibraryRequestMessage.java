package uk.kihira.tails.common.network;

import io.netty.buffer.ByteBuf;
import uk.kihira.tails.common.Tails;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class LibraryRequestMessage implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<LibraryRequestMessage, LibraryEntriesMessage> {

        @Override
        public LibraryEntriesMessage onMessage(LibraryRequestMessage message, MessageContext ctx) {
            return new LibraryEntriesMessage(Tails.proxy.getLibraryManager().libraryEntries, false);
        }
    }
}
