// 경로: src/main/java/com/akashiro/tails/common/network/LibraryRequestPacket.java
package com.akashiro.tails.common.network;

import com.akashiro.tails.Tails;
import com.akashiro.tails.common.data.LibraryEntryData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

/**
 * 클라이언트 → 서버: 라이브러리 엔트리 목록 요청.
 * 서버는 LibraryEntriesPacket으로 응답.
 * 1.12.2 LibraryRequestMessage → 1.20.1 LibraryRequestPacket
 */
public class LibraryRequestPacket {

    public LibraryRequestPacket() {}

    public static void encode(LibraryRequestPacket packet, FriendlyByteBuf buf) {
        // 빈 패킷 — 요청 신호만 보냄
    }

    public static LibraryRequestPacket decode(FriendlyByteBuf buf) {
        return new LibraryRequestPacket();
    }

    public static void handle(LibraryRequestPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;

            if (Tails.libraryEnabled) {
                List<LibraryEntryData> entries = Tails.PROXY.getLibraryManager().loadLibrary();
                TailsNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> sender),
                        new LibraryEntriesPacket(entries, false)
                );
                Tails.LOGGER.debug("Sent {} library entries to {}", entries.size(), sender.getName().getString());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}