/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.common.network;

import com.google.gson.JsonSyntaxException;
import com.mojang.util.UUIDTypeAdapter;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;

import java.util.UUID;

public class PlayerDataMessage implements IMessage {

    private UUID uuid;
    private PartsData partsData;
    private boolean shouldRemove;

    public PlayerDataMessage() {}
    public PlayerDataMessage(UUID uuid, PartsData partsData, boolean shouldRemove) {
        this.uuid = uuid;
        this.partsData = partsData;
        this.shouldRemove = shouldRemove;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        uuid = UUIDTypeAdapter.fromString(ByteBufUtils.readUTF8String(buf));
        String tailInfoJson = ByteBufUtils.readUTF8String(buf);
        try {
            partsData = Tails.gson.fromJson(tailInfoJson, PartsData.class);
        } catch (JsonSyntaxException e) {
            Tails.logger.warn(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, UUIDTypeAdapter.fromUUID(uuid));
        String tailInfoJson = Tails.gson.toJson(this.partsData);
        ByteBufUtils.writeUTF8String(buf, tailInfoJson);
    }

    public static class Handler implements IMessageHandler<PlayerDataMessage, IMessage> {

        @Override
        public IMessage onMessage(PlayerDataMessage message, MessageContext ctx) {
            if (message.partsData != null) {
                if (message.shouldRemove) Tails.proxy.removePartsData(message.uuid);
                else {
                    Tails.proxy.addPartsData(message.uuid, message.partsData);
                    //Tell other clients about the change
                    if (ctx.side.isServer()) {
                        Tails.networkWrapper.sendToAll(new PlayerDataMessage(message.uuid, message.partsData, false));
                    }
                }
            }
            return null;
        }
    }
}
