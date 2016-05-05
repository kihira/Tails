/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.common.network;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;
import java.util.UUID;

public class PlayerDataMapMessage implements IMessage {

    private Map<UUID, PartsData> partsDataMap;

    public PlayerDataMapMessage() {}
    @SuppressWarnings("unchecked")
    public PlayerDataMapMessage(Map partsDataMap) {
        this.partsDataMap = partsDataMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fromBytes(ByteBuf buf) {
        String tailInfoJson = ByteBufUtils.readUTF8String(buf);
        try {
            this.partsDataMap = Tails.gson.fromJson(tailInfoJson, new TypeToken<Map<UUID, PartsData>>() {}.getType());
        } catch (JsonSyntaxException e) {
            Tails.logger.catching(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String tailInfoJson = Tails.gson.toJson(this.partsDataMap);
        ByteBufUtils.writeUTF8String(buf, tailInfoJson);
    }

    public static class Handler implements IMessageHandler<PlayerDataMapMessage, IMessage> {

        @Override
        public IMessage onMessage(PlayerDataMapMessage message, MessageContext ctx) {
            for (Map.Entry<UUID, PartsData> entry : message.partsDataMap.entrySet()) {
                //Ignore local player
                if (EntityPlayer.getUUID(Minecraft.getMinecraft().getSession().getProfile()) != entry.getKey()) {
                    Tails.proxy.addPartsData(entry.getKey(), entry.getValue());
                }
            }
            return null;
        }
    }
}
