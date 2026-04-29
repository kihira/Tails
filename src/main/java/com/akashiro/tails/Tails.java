// 경로: src/main/java/com/akashiro/tails/Tails.java
package com.akashiro.tails;

import com.akashiro.tails.common.data.PartsData;
import com.akashiro.tails.common.network.TailsNetwork;
import com.akashiro.tails.proxy.CommonProxy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Tails.MOD_ID)
public class Tails {

    public static final String MOD_ID = "tails";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    /**
     * 전역 Gson 인스턴스.
     *
     * - excludeFieldsWithoutExposeAnnotation()
     *     @Expose 어노테이션이 없는 필드는 직렬화/역직렬화 대상에서 제외.
     *     예: PartInfo.texture(ResourceLocation)처럼 런타임 전용 필드를 JSON에 포함시키지 않음.
     *     주의: TailsConfig 필드에도 반드시 @Expose를 붙여야 저장/로드가 정상 작동함.
     *
     * - enableComplexMapKeySerialization()
     *     Map의 키가 String이 아닌 복잡한 타입(enum 포함)일 때도 올바르게 직렬화/역직렬화.
     *     PartsData.partInfoMap의 키가 PartType enum이기 때문에 반드시 필요.
     *     이 옵션 없이 역직렬화하면 partInfoMap이 비어서
     *     hasPart() → false → 꼬리가 렌더링되지 않는 버그 발생.
     */
    public static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .enableComplexMapKeySerialization()
            .create();

    /** 서버에 Tails 모드가 설치되어 있는지 여부. 멀티플레이 전용 데이터 수신 시 true로 설정됨 */
    public static boolean hasRemote = false;

    /** 라이브러리 기능 활성화 여부 */
    public static boolean libraryEnabled = true;

    /** 싱글플레이 및 Tails 미설치 서버에서 사용하는 로컬 파츠 데이터 */
    public static PartsData localPartsData;

    /**
     * 클라이언트/서버 환경에 따라 다른 구현체가 주입되는 프록시.
     * - 클라이언트: ClientProxy (렌더러 등록, 로그인 이벤트 처리 등)
     * - 서버/공통:  CommonProxy (파츠 데이터 관리, 네트워크 패킷 처리 등)
     */
    public static CommonProxy PROXY;

    public Tails() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 실행 환경(클라이언트/서버)에 맞는 프록시 인스턴스 생성.
        // safeRunForDist를 사용해 서버에서 ClientProxy 클래스가 로드되지 않도록 방지.
        PROXY = DistExecutor.safeRunForDist(
                () -> ClientSetup::createProxy,  // 클라이언트 → ClientProxy
                () -> ServerSetup::createProxy   // 서버/공통  → CommonProxy
        );

        // 공통 셋업 이벤트 등록 (네트워크, 설정 로드 등)
        modBus.addListener(this::onCommonSetup);

        // 클라이언트에서만 클라이언트 셋업 이벤트 및 키 맵핑 등록
        DistExecutor.safeRunWhenOn(Dist.CLIENT,
                () -> ClientSetup::registerClientListeners);

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Tails mod initialized.");
    }

    // ──────────────────────────────────────────────
    // 클라이언트 전용 팩토리
    // 서버에서 이 클래스 자체가 로드되지 않도록 별도 클래스로 분리.
    // ──────────────────────────────────────────────
    public static final class ClientSetup {
        private ClientSetup() {}

        /** 클라이언트 프록시 인스턴스 생성 */
        public static CommonProxy createProxy() {
            return new com.akashiro.tails.proxy.ClientProxy();
        }

        /** 클라이언트 전용 이벤트 리스너 등록 (셋업, 키 맵핑) */
        public static void registerClientListeners() {
            IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
            modBus.addListener(Tails::onClientSetup);
            modBus.addListener(Tails::onAddLayers);
            modBus.addListener(
                    com.akashiro.tails.client.ClientEventHandler::onRegisterKeyMappings);
        }
    }

    // ──────────────────────────────────────────────
    // 서버/공통 팩토리
    // ──────────────────────────────────────────────
    public static final class ServerSetup {
        private ServerSetup() {}

        /** 공통 프록시 인스턴스 생성 */
        public static CommonProxy createProxy() {
            return new CommonProxy();
        }
    }

    // ──────────────────────────────────────────────
    // 이벤트 핸들러
    // ──────────────────────────────────────────────

    /**
     * 공통 셋업 — 클라이언트/서버 양쪽에서 실행됨.
     * 네트워크 채널 등록, 프록시 초기화, 설정 파일 로드.
     */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        TailsNetwork.register();
        PROXY.init();
        loadConfig();
        LOGGER.info("Tails common setup complete.");
    }

    /**
     * 클라이언트 셋업 — 클라이언트에서만 실행됨.
     * enqueueWork()로 메인 스레드에서 실행해야
     * PlayerRenderer에 레이어가 정상 등록됨.
     */
    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.registerRenderers());
        LOGGER.info("Tails client setup complete.");
    }

    private static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        if (PROXY instanceof com.akashiro.tails.proxy.ClientProxy clientProxy) {
            clientProxy.onAddLayers(event);
        }
    }

    // ──────────────────────────────────────────────
    // 설정 파일 로드 / 저장
    // 저장 위치: config/tails.json
    // ──────────────────────────────────────────────

    /**
     * config/tails.json에서 로컬 파츠 데이터와 라이브러리 설정을 불러옴.
     * 파일이 없으면 기본값으로 초기화 후 저장.
     *
     * 주의: 이 시점에는 mc.player가 null이므로
     *       PROXY.addPartsData() 호출 불가 — 로그인 이벤트(onClientLogin)에서 처리.
     */
    public static void loadConfig() {
        java.io.File configDir  = new java.io.File("config");
        java.io.File configFile = new java.io.File(configDir, "tails.json");
        if (!configDir.exists()) configDir.mkdirs();

        if (configFile.exists()) {
            try (java.io.FileReader reader = new java.io.FileReader(configFile)) {
                TailsConfig config = GSON.fromJson(reader, TailsConfig.class);
                if (config != null) {
                    if (config.localPartsData != null) localPartsData = config.localPartsData;
                    libraryEnabled = config.libraryEnabled;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load tails config: {}", e.getMessage());
                localPartsData = new PartsData();
            }
        } else {
            // 설정 파일 없음 → 기본 PartsData(모든 슬롯 none)로 초기화
            localPartsData = new PartsData();
            saveConfig();
        }
    }

    /**
     * 현재 로컬 파츠 데이터와 라이브러리 설정을 config/tails.json에 저장.
     */
    public static void saveConfig() {
        java.io.File configDir  = new java.io.File("config");
        java.io.File configFile = new java.io.File(configDir, "tails.json");
        if (!configDir.exists()) configDir.mkdirs();

        TailsConfig config = new TailsConfig();
        config.localPartsData = localPartsData;
        config.libraryEnabled = libraryEnabled;

        try (java.io.FileWriter writer = new java.io.FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (Exception e) {
            LOGGER.error("Failed to save tails config: {}", e.getMessage());
        }
    }

    /**
     * 로컬 파츠 데이터를 변경하고 즉시 config 파일에 저장.
     * GUI 에디터 저장 버튼(onSave)에서 호출됨.
     */
    public static void setLocalPartsData(PartsData data) {
        localPartsData = data;
        saveConfig();
    }

    // ──────────────────────────────────────────────
    // 설정 파일 구조체
    // ──────────────────────────────────────────────

    /**
     * tails.json 파일의 루트 구조.
     *
     * @Expose가 반드시 필요한 이유:
     *   GSON에 excludeFieldsWithoutExposeAnnotation()이 설정되어 있어서
     *   @Expose 없는 필드는 직렬화/역직렬화 대상에서 제외됨.
     *   → @Expose 없으면 tails.json이 {}로 저장되고
     *     로드 시 localPartsData가 null이 되어 꼬리가 보이지 않음.
     */
    private static class TailsConfig {
        /** 플레이어의 로컬 파츠 설정 */
        @Expose
        public PartsData localPartsData;

        /** 라이브러리 기능 활성화 여부 (기본값 true) */
        @Expose
        public boolean libraryEnabled = true;
    }
}
