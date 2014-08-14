package kihira.tails.common.network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;

import java.util.Map;
import java.util.UUID;

public class TailMapMessage implements IMessage {

    private Map<Object, TailInfo> tailInfoMap;

    public TailMapMessage() {}
    @SuppressWarnings("unchecked")
    public TailMapMessage(Map tailInfoMap) {
        this.tailInfoMap = tailInfoMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fromBytes(ByteBuf buf) {
        String tailInfoJson = ByteBufUtils.readUTF8String(buf);
        try {
            this.tailInfoMap = new Gson().fromJson(tailInfoJson, Map.class);
        } catch (JsonSyntaxException e) {
            Tails.logger.warn(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String tailInfoJson = new Gson().toJson(this.tailInfoMap);
        ByteBufUtils.writeUTF8String(buf, tailInfoJson);
    }

    public static class TailMapMessageHandler implements IMessageHandler<TailMapMessage, IMessage> {

        @Override
        public IMessage onMessage(TailMapMessage message, MessageContext ctx) {
            for (Map.Entry<Object, TailInfo> entry : message.tailInfoMap.entrySet()) {
                UUID uuid;
                //UUID isn't entirely compatible with gson so it gets saved as a String instead of UUID at times
                if (entry.getKey() instanceof String) uuid = UUID.fromString((String) entry.getKey());
                else uuid = (UUID) entry.getKey();
                Tails.proxy.addTailInfo(uuid, entry.getValue());
            }
            return null;
        }
    }
}
