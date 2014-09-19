/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.common.network;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.UUID;

public class TailMapMessage implements IMessage {

    private Map<UUID, TailInfo> tailInfoMap;

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
            this.tailInfoMap = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(tailInfoJson, new TypeToken<Map<UUID, TailInfo>>() {}.getType());
        } catch (JsonSyntaxException e) {
            Tails.logger.warn(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String tailInfoJson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this.tailInfoMap);
        ByteBufUtils.writeUTF8String(buf, tailInfoJson);
    }

    public static class TailMapMessageHandler implements IMessageHandler<TailMapMessage, IMessage> {

        @Override
        public IMessage onMessage(TailMapMessage message, MessageContext ctx) {
            for (Map.Entry<UUID, TailInfo> entry : message.tailInfoMap.entrySet()) {
                //Ignore local player
                if (Minecraft.getMinecraft().thePlayer.getPersistentID() != entry.getKey()) {
                    Tails.proxy.addTailInfo(entry.getKey(), entry.getValue());
                }
            }
            return null;
        }
    }
}
