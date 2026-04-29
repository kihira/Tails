// 경로: src/main/java/com/akashiro/tails/common/network/ServerCapabilitiesPacket.java
package com.akashiro.tails.common.network;

import com.akashiro.tails.Tails;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 서버 → 클라이언트: 서버가 라이브러리 기능을 지원하는지 알림.
 * 1.12.2 ServerCapabilitiesMessage → 1.20.1 ServerCapabilitiesPacket
 */
public class ServerCapabilitiesPacket {

    private final boolean libraryEnabled;

    public ServerCapabilitiesPacket(boolean libraryEnabled) {
        this.libraryEnabled = libraryEnabled;
    }

    public static void encode(ServerCapabilitiesPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.libraryEnabled);
    }

    public static ServerCapabilitiesPacket decode(FriendlyByteBuf buf) {
        return new ServerCapabilitiesPacket(buf.readBoolean());
    }

    public static void handle(ServerCapabilitiesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Tails.hasRemote = packet.libraryEnabled;
            Tails.LOGGER.debug("Server capabilities received: libraryEnabled={}", packet.libraryEnabled);
        });
        ctx.get().setPacketHandled(true);
    }

    public boolean isLibraryEnabled() { return libraryEnabled; }
}