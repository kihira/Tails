package uk.kihira.tails.common.network;

import io.netty.buffer.ByteBuf;
import uk.kihira.tails.common.Tails;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerCapabilitiesMessage implements IMessage {

    private boolean library;

    public ServerCapabilitiesMessage() {}
    public ServerCapabilitiesMessage(boolean library) {
        this.library = library;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        library = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(library);
    }

    public static class Handler implements IMessageHandler<ServerCapabilitiesMessage, IMessage> {

        @Override
        public IMessage onMessage(ServerCapabilitiesMessage message, MessageContext ctx) {
            Tails.libraryEnabled = message.library;
            return null;
        }
    }
}
