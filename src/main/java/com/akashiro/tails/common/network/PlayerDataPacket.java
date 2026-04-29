// 경로: src/main/java/com/akashiro/tails/common/network/PlayerDataPacket.java
package com.akashiro.tails.common.network;

import com.akashiro.tails.Tails;
import com.akashiro.tails.common.data.PartsData;
import com.google.gson.Gson;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 플레이어 한 명의 PartsData를 서버↔클라이언트 간에 전송.
 * shouldRemove=true 이면 해당 UUID의 데이터를 삭제 요청.
 *
 * 1.12.2 PlayerDataMessage → 1.20.1 PlayerDataPacket
 */
public class PlayerDataPacket {

    private final UUID uuid;
    private final PartsData partsData;
    private final boolean shouldRemove;

    public PlayerDataPacket(UUID uuid, PartsData partsData, boolean shouldRemove) {
        this.uuid         = uuid;
        this.partsData    = partsData;
        this.shouldRemove = shouldRemove;
    }

    // ──────────────────────────────────────────────
    // 직렬화 / 역직렬화
    // ──────────────────────────────────────────────

    public static void encode(PlayerDataPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.uuid);
        buf.writeBoolean(packet.shouldRemove);
        if (!packet.shouldRemove) {
            Gson gson = Tails.GSON;
            buf.writeUtf(gson.toJson(packet.partsData));
        }
    }

    public static PlayerDataPacket decode(FriendlyByteBuf buf) {
        UUID uuid         = buf.readUUID();
        boolean remove    = buf.readBoolean();
        PartsData data    = null;
        if (!remove) {
            String json = buf.readUtf(32767);
            data = Tails.GSON.fromJson(json, PartsData.class);
        }
        return new PlayerDataPacket(uuid, data, remove);
    }

    // ──────────────────────────────────────────────
    // 핸들러
    // ──────────────────────────────────────────────

    public static void handle(PlayerDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkEvent.Context context = ctx.get();

            if (context.getDirection().getReceptionSide().isServer()) {
                // 클라이언트 → 서버: 플레이어가 파츠 데이터를 보냄
                ServerPlayer sender = context.getSender();
                if (sender == null) return;

                if (packet.shouldRemove) {
                    Tails.PROXY.removePartsData(packet.uuid);
                } else {
                    Tails.PROXY.addPartsData(packet.uuid, packet.partsData);
                }

                // 다른 모든 클라이언트에게 브로드캐스트
                // (서버가 해당 플레이어 근처의 클라이언트에 전파)
                TailsNetwork.CHANNEL.send(
                        net.minecraftforge.network.PacketDistributor.ALL.noArg(),
                        packet
                );
            } else {
                // 서버 → 클라이언트: 다른 플레이어 데이터 수신
                if (packet.shouldRemove) {
                    Tails.PROXY.removePartsData(packet.uuid);
                } else {
                    Tails.PROXY.addPartsData(packet.uuid, packet.partsData);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // ──────────────────────────────────────────────
    // Getter
    // ──────────────────────────────────────────────

    public UUID getUuid()         { return uuid; }
    public PartsData getPartsData() { return partsData; }
    public boolean isShouldRemove() { return shouldRemove; }
}