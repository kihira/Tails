// 경로: src/main/java/com/akashiro/tails/client/PartRegistry.java
package com.akashiro.tails.client;

import com.akashiro.tails.client.model.ears.*;
import com.akashiro.tails.client.model.muzzle.ModelMuzzle;
import com.akashiro.tails.client.model.tail.*;
import com.akashiro.tails.client.model.wings.ModelMetalWings;
import com.akashiro.tails.client.render.RenderPart;
import com.akashiro.tails.client.render.RenderWings;
import com.akashiro.tails.common.data.PartsData.PartType;
import com.google.common.collect.ArrayListMultimap;

import java.util.List;

/**
 * 모든 파츠를 등록하고 관리하는 레지스트리.
 * 1.12.2 PartRegistry 이식 — ArrayListMultimap 구조 유지.
 *
 * 등록 순서가 typeid 인덱스가 되므로 절대 바꾸면 안 됨!
 */
public class PartRegistry {

    private static final ArrayListMultimap<PartType, RenderPart> REGISTRY =
            ArrayListMultimap.create();

    /** 모드 초기화 시 한 번만 호출 */
    public static void register() {
        // ─────────────────────────────────────────────────────
        // TAIL  (typeid 0~7)
        // ─────────────────────────────────────────────────────
        registerPart(PartType.TAIL, new RenderPart(
                "tail.fluffy", 3, new ModelFluffyTail(), null,
                new String[]{"fox_tail"})
                .setAuthor("@TTFTCUTS", 0));

        registerPart(PartType.TAIL, new RenderPart(
                "tail.dragon", 2, new ModelDragonTail(), null,
                new String[]{"dragon_tail", "dragon_tail_striped"}));

        registerPart(PartType.TAIL, new RenderPart(
                "tail.raccoon", 1, new ModelRaccoonTail(), null,
                new String[]{"racoon_tail"}));

        registerPart(PartType.TAIL, new RenderPart(
                "tail.devil", 2, new ModelDevilTail(), null,
                new String[]{"devil_tail"}));

        registerPart(PartType.TAIL, new RenderPart(
                "tail.cat", 1, new ModelCatTail(), null,
                new String[]{"tabby_tail", "tiger_tail"}));

        registerPart(PartType.TAIL, new RenderPart(
                "tail.bird", 1, new ModelBirdTail(), "@blusunrize",
                new String[]{"bird_tail"}));

        registerPart(PartType.TAIL, new RenderPart(
                "tail.shark", 1, new ModelSharkTail(), null,
                new String[]{"access_denied", "shark_tail"})
                .setAuthor("@TTFTCUTS", 0, 0));

        registerPart(PartType.TAIL, new RenderPart(
                "tail.bunny", 1, new ModelBunnyTail(), "@carrotcodes",
                new String[]{"bunny_tail"}));

        // ─────────────────────────────────────────────────────
        // EARS  (typeid 0~3)
        // ─────────────────────────────────────────────────────
        registerPart(PartType.EARS, new RenderPart(
                "ears.fox", 2, new ModelFoxEars(), "@Adeon",
                new String[]{"fox_ears"}));

        registerPart(PartType.EARS, new RenderPart(
                "ears.cat", 1, new ModelCatEars(), null,
                new String[]{"cat_ears"}));

        registerPart(PartType.EARS, new RenderPart(
                "ears.panda", 1, new ModelPandaEars(), null,
                new String[]{"panda_ears"}));

        registerPart(PartType.EARS, new RenderPart(
                "ears.cat_small", 1, new ModelCatSmallEars(), null,
                new String[]{"cat_small_ears"}));

        // ─────────────────────────────────────────────────────
        // WINGS (typeid 0)
        // ─────────────────────────────────────────────────────
        registerPart(PartType.WINGS, new RenderWings(
                "wings.big", 2, "@littlechippie",
                new ModelMetalWings(),
                new String[]{"big_wings", "metal_wings", "dragon_wings", "dragon_boneless_wings"})
                .setAuthor("Dracyoshi", 0, 2)
                .setAuthor("Dracyoshi", 0, 3));

        // ─────────────────────────────────────────────────────
        // MUZZLE (typeid 0~2, subid 0~4 = 크기 5단계)
        // ─────────────────────────────────────────────────────

        // Standard 머즐 — 5단계 크기
        registerPart(PartType.MUZZLE, new RenderPart(
                "muzzle.standard", 5, new ModelMuzzle(-2f, -3f, -9f, 4, 3, 5), null,
                new String[]{"standard_muzzle", "alt_muzzle"}));

        // Slim 머즐
        registerPart(PartType.MUZZLE, new RenderPart(
                "muzzle.slim", 5, new ModelMuzzle(-2f, -2f, -9f, 4, 2, 5), null,
                new String[]{"standard_muzzle", "alt_muzzle"}));

        // Thin 머즐
        registerPart(PartType.MUZZLE, new RenderPart(
                "muzzle.thin", 5, new ModelMuzzle(-1.5f, -2f, -9f, 3, 2, 5, 0, 9), null,
                new String[]{"standard_muzzle", "alt_muzzle"}));
    }

    // ──────────────────────────────────────────────
    // 내부 등록 / 조회 메서드
    // ──────────────────────────────────────────────

    private static void registerPart(PartType type, RenderPart part) {
        REGISTRY.put(type, part);
    }

    /**
     * 특정 파츠 타입의 등록된 파츠 목록 반환.
     * 1.12.2: getParts(PartType)
     */
    public static List<RenderPart> getParts(PartType type) {
        return REGISTRY.get(type);
    }

    /**
     * typeid 로 파츠 조회.
     * 1.12.2: getRenderPart(PartType, int)
     */
    public static RenderPart getRenderPart(PartType type, int typeid) {
        List<RenderPart> parts = REGISTRY.get(type);
        if (typeid < 0 || typeid >= parts.size()) return null;
        return parts.get(typeid);
    }
}
