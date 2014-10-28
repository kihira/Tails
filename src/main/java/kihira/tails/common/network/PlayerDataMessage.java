/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.common.network;

import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;

public class PlayerDataMessage implements IMessage {

    private PartsData partsData;
    private boolean shouldRemove;

    public PlayerDataMessage() {}
    public PlayerDataMessage(PartsData partsData, boolean shouldRemove) {
        this.partsData = partsData;
        this.shouldRemove = shouldRemove;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String tailInfoJson = ByteBufUtils.readUTF8String(buf);
        try {
            this.partsData = Tails.gson.fromJson(tailInfoJson, PartsData.class);
        } catch (JsonSyntaxException e) {
            Tails.logger.warn(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String tailInfoJson = Tails.gson.toJson(this.partsData);
        ByteBufUtils.writeUTF8String(buf, tailInfoJson);
    }

    public static class Handler implements IMessageHandler<PlayerDataMessage, IMessage> {

        @Override
        public IMessage onMessage(PlayerDataMessage message, MessageContext ctx) {
            if (message.partsData != null) {
                if (message.shouldRemove) Tails.proxy.removePartsData(message.partsData.uuid);
                else {
                    Tails.proxy.addPartsData(message.partsData.uuid, message.partsData);
                    //Tell other clients about the change
                    if (ctx.side.isServer()) {
                        Tails.networkWrapper.sendToAll(new PlayerDataMessage(message.partsData, false));
                    }
                }
            }
            return null;
        }
    }
}
