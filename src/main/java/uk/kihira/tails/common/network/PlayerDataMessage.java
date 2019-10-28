package uk.kihira.tails.common.network;

import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import com.mojang.util.UUIDTypeAdapter;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import java.util.UUID;

public class PlayerDataMessage implements IMessage {

    private UUID uuid;
    private Outfit outfit;
    private boolean shouldRemove;

    public PlayerDataMessage() {}
    public PlayerDataMessage(UUID uuid, Outfit outfit, boolean shouldRemove) {
        this.uuid = uuid;
        this.outfit = outfit;
        this.shouldRemove = shouldRemove;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        uuid = UUIDTypeAdapter.fromString(ByteBufUtils.readUTF8String(buf));
        String tailInfoJson = ByteBufUtils.readUTF8String(buf);
        if (!Strings.isNullOrEmpty(tailInfoJson)) {
            try {
                outfit = Tails.gson.fromJson(tailInfoJson, Outfit.class);
            } catch (JsonSyntaxException e) {
                Tails.logger.warn(e);
            }
        }
        else outfit = null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, UUIDTypeAdapter.fromUUID(uuid));
        String tailInfoJson = outfit == null ? "" : Tails.gson.toJson(this.outfit);
        ByteBufUtils.writeUTF8String(buf, tailInfoJson);
    }

    public static class Handler implements IMessageHandler<PlayerDataMessage, IMessage> {

        @Override
        public IMessage onMessage(PlayerDataMessage message, MessageContext ctx) {
            if (message.shouldRemove) Tails.proxy.removeActiveOutfit(message.uuid);
            else if (message.outfit != null) {
                Tails.proxy.setActiveOutfit(message.uuid, message.outfit);
                //Tell other clients about the change
                if (ctx.side.isServer()) {
                    Tails.networkWrapper.sendToAll(new PlayerDataMessage(message.uuid, message.outfit, false));
                }
            }
            return null;
        }
    }
}
