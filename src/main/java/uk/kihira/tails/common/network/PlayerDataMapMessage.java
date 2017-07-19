/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.network;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;

import java.util.Map;
import java.util.UUID;

public class PlayerDataMapMessage implements IMessage {

    private Map<UUID, Outfit> outfitMap;

    public PlayerDataMapMessage() {}
    @SuppressWarnings("unchecked")
    public PlayerDataMapMessage(Map outfitMap) {
        this.outfitMap = outfitMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fromBytes(ByteBuf buf) {
        String tailInfoJson = ByteBufUtils.readUTF8String(buf);
        try {
            this.outfitMap = Tails.gson.fromJson(tailInfoJson, new TypeToken<Map<UUID, PartsData>>() {}.getType());
        } catch (JsonSyntaxException e) {
            Tails.logger.catching(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String tailInfoJson = Tails.gson.toJson(this.outfitMap);
        ByteBufUtils.writeUTF8String(buf, tailInfoJson);
    }

    public static class Handler implements IMessageHandler<PlayerDataMapMessage, IMessage> {

        @Override
        public IMessage onMessage(PlayerDataMapMessage message, MessageContext ctx) {
            for (Map.Entry<UUID, Outfit> entry : message.outfitMap.entrySet()) {
                Tails.proxy.setActiveOutfit(entry.getKey(), entry.getValue());
            }
            return null;
        }
    }
}
