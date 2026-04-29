// 경로: src/main/java/com/akashiro/tails/proxy/CommonProxy.java
package com.akashiro.tails.proxy;

import com.akashiro.tails.Tails;
import com.akashiro.tails.common.LibraryManager;
import com.akashiro.tails.common.TailAbilityHandler;
import com.akashiro.tails.common.data.PartsData;
import com.akashiro.tails.common.network.PlayerDataMapPacket;
import com.akashiro.tails.common.network.ServerCapabilitiesPacket;
import com.akashiro.tails.common.network.TailsNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 서버/클라이언트 공통 로직 담당 프록시.
 * 1.12.2 CommonProxy 이식.
 *
 * 주요 변경:
 *   @SidedProxy 시스템 제거 → DistExecutor.safeRunWhenOn() 방식으로 대체
 *   FMLCommonHandler → LogicalSide
 */
public class CommonProxy {

    /** UUID → PartsData 맵 (서버: 접속 중인 모든 플레이어, 클라: 렌더링 범위 내 플레이어) */
    protected final Map<UUID, PartsData> partsData = new HashMap<>();

    protected LibraryManager libraryManager;

    public void init() {
        libraryManager = new LibraryManager();
        registerHandlers();
    }

    protected void registerHandlers() {
        // 서버 이벤트 등록
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new TailAbilityHandler());
    }

    public void registerRenderers() {
        // 클라이언트에서만 사용 — ClientProxy에서 오버라이드
    }

    // ──────────────────────────────────────────────
    // 플레이어 데이터 관리
    // ──────────────────────────────────────────────

    public void addPartsData(UUID uuid, PartsData data) {
        if (uuid == null) {
            Tails.LOGGER.warn("Attempted to add part data with null UUID! {}", data);
            return;
        }
        partsData.put(uuid, data);
        Tails.LOGGER.debug("Added part data for {}: {}", uuid, data);
    }

    public void removePartsData(UUID uuid) {
        partsData.remove(uuid);
        Tails.LOGGER.debug("Removed part data for {}", uuid);
    }

    public boolean hasPartsData(UUID uuid) {
        return partsData.containsKey(uuid);
    }

    public PartsData getPartsData(UUID uuid) {
        return partsData.get(uuid);
    }

    public Map<UUID, PartsData> getAllPartsData() {
        return partsData;
    }

    public void clearAllPartsData() {
        Tails.LOGGER.debug("Clearing parts data");
        partsData.clear();
    }

    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    // ──────────────────────────────────────────────
    // 서버 이벤트 핸들러
    // 1.12.2 ServerEventHandler의 @SubscribeEvent 이식
    // ──────────────────────────────────────────────

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // 현재 서버에 있는 모든 플레이어 데이터를 새 플레이어에게 전송
        TailsNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new PlayerDataMapPacket(getAllPartsData())
        );

        // 서버 기능 정보 전송
        TailsNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new ServerCapabilitiesPacket(Tails.libraryEnabled)
        );

        Tails.LOGGER.debug("Sent tail data of size {} to {}",
                partsData.size(), player.getName().getString());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        removePartsData(uuid);
    }
}
