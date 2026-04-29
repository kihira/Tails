// 경로: src/main/java/com/akashiro/tails/common/network/TailsNetwork.java
package com.akashiro.tails.common.network;

import com.akashiro.tails.Tails;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 모든 네트워크 패킷을 한 곳에서 등록하고 채널을 관리.
 * 1.12.2의 SimpleNetworkWrapper → 1.20.1 SimpleChannel 로 교체.
 */
public class TailsNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Tails.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        // 패킷 ID 순서가 클라이언트/서버 양쪽에서 동일해야 함
        CHANNEL.registerMessage(packetId++,
                PlayerDataPacket.class,
                PlayerDataPacket::encode,
                PlayerDataPacket::decode,
                PlayerDataPacket::handle);

        CHANNEL.registerMessage(packetId++,
                PlayerDataMapPacket.class,
                PlayerDataMapPacket::encode,
                PlayerDataMapPacket::decode,
                PlayerDataMapPacket::handle);

        CHANNEL.registerMessage(packetId++,
                ServerCapabilitiesPacket.class,
                ServerCapabilitiesPacket::encode,
                ServerCapabilitiesPacket::decode,
                ServerCapabilitiesPacket::handle);

        CHANNEL.registerMessage(packetId++,
                LibraryRequestPacket.class,
                LibraryRequestPacket::encode,
                LibraryRequestPacket::decode,
                LibraryRequestPacket::handle);

        CHANNEL.registerMessage(packetId++,
                LibraryEntriesPacket.class,
                LibraryEntriesPacket::encode,
                LibraryEntriesPacket::decode,
                LibraryEntriesPacket::handle);
    }
}