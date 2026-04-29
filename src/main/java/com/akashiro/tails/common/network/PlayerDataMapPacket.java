// 경로: src/main/java/com/akashiro/tails/common/network/PlayerDataMapPacket.java
package com.akashiro.tails.common.network;

import com.akashiro.tails.Tails;
import com.akashiro.tails.common.data.PartsData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 서버 → 클라이언트: 로그인 시 서버에 있는 모든 플레이어의 파츠 데이터를 한 번에 전송.
 * 1.12.2 PlayerDataMapMessage → 1.20.1 PlayerDataMapPacket
 */
public class PlayerDataMapPacket {

    private final Map<UUID, PartsData> partsDataMap;

    public PlayerDataMapPacket(Map<UUID, PartsData> partsDataMap) {
        this.partsDataMap = partsDataMap;
    }

    // ──────────────────────────────────────────────
    // 직렬화 / 역직렬화
    // ──────────────────────────────────────────────

    public static void encode(PlayerDataMapPacket packet, FriendlyByteBuf buf) {
        Gson gson = Tails.GSON;
        buf.writeUtf(gson.toJson(packet.partsDataMap));
    }

    public static PlayerDataMapPacket decode(FriendlyByteBuf buf) {
        String json = buf.readUtf(32767);
        Type type   = new TypeToken<Map<UUID, PartsData>>(){}.getType();
        Map<UUID, PartsData> map = Tails.GSON.fromJson(json, type);
        return new PlayerDataMapPacket(map);
    }

    // ──────────────────────────────────────────────
    // 핸들러 (항상 클라이언트에서 수신)
    // ──────────────────────────────────────────────

    public static void handle(PlayerDataMapPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (packet.partsDataMap == null) return;
            for (Map.Entry<UUID, PartsData> entry : packet.partsDataMap.entrySet()) {
                Tails.PROXY.addPartsData(entry.getKey(), entry.getValue());
            }
            Tails.LOGGER.debug("Received parts data map with {} entries", packet.partsDataMap.size());
        });
        ctx.get().setPacketHandled(true);
    }

    public Map<UUID, PartsData> getPartsDataMap() { return partsDataMap; }
}