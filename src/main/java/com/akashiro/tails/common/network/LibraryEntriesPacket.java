// 경로: src/main/java/com/akashiro/tails/common/network/LibraryEntriesPacket.java
package com.akashiro.tails.common.network;

import com.akashiro.tails.Tails;
import com.akashiro.tails.common.data.LibraryEntryData;
import com.google.gson.reflect.TypeToken;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;

/**
 * 서버 ↔ 클라이언트: 라이브러리 엔트리 목록 전송.
 * delete=true 이면 전달된 엔트리들을 서버 라이브러리에서 삭제 요청.
 * 1.12.2 LibraryEntriesMessage → 1.20.1 LibraryEntriesPacket
 */
public class LibraryEntriesPacket {

    private final List<LibraryEntryData> entries;
    private final boolean delete;

    public LibraryEntriesPacket(List<LibraryEntryData> entries, boolean delete) {
        this.entries = entries;
        this.delete  = delete;
    }

    // ──────────────────────────────────────────────
    // 직렬화 / 역직렬화
    // ──────────────────────────────────────────────

    public static void encode(LibraryEntriesPacket packet, FriendlyByteBuf buf) {
        Type type = new TypeToken<List<LibraryEntryData>>(){}.getType();
        buf.writeUtf(Tails.GSON.toJson(packet.entries, type));
        buf.writeBoolean(packet.delete);
    }

    public static LibraryEntriesPacket decode(FriendlyByteBuf buf) {
        String json    = buf.readUtf(32767);
        Type type      = new TypeToken<List<LibraryEntryData>>(){}.getType();
        List<LibraryEntryData> entries = Tails.GSON.fromJson(json, type);
        boolean delete = buf.readBoolean();
        return new LibraryEntriesPacket(entries, delete);
    }

    // ──────────────────────────────────────────────
    // 핸들러
    // ──────────────────────────────────────────────

    public static void handle(LibraryEntriesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (packet.entries == null) return;

            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                // 클라이언트 → 서버: 삭제 요청
                if (packet.delete) {
                    for (LibraryEntryData entry : packet.entries) {
                        Tails.PROXY.getLibraryManager().removeEntry(entry);
                    }
                    Tails.PROXY.getLibraryManager().saveLibrary();
                }
            } else {
                // 서버 → 클라이언트: 목록 수신
                Tails.PROXY.getLibraryManager().addEntries(packet.entries);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public List<LibraryEntryData> getEntries() { return entries; }
    public boolean isDelete() { return delete; }
}