package kihira.tails.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import kihira.tails.common.Tails;

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
