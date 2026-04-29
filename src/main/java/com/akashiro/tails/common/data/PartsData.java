// 경로: src/main/java/com/akashiro/tails/common/data/PartsData.java
package com.akashiro.tails.common.data;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 한 명이 장착한 모든 파츠 정보를 담는 컨테이너.
 * TAIL, EARS, WINGS, MUZZLE 네 가지 슬롯을 Map으로 관리.
 */
public class PartsData {

    /** 파츠 종류 — 1.12.2 PartsData$PartType 그대로 이식 */
    public enum PartType {
        TAIL,
        EARS,
        WINGS,
        MUZZLE
    }

    /** Gson 직렬화 대상 필드 */
    @Expose
    private final Map<PartType, PartInfo> partInfoMap = new HashMap<>();

    public PartsData() {
        // 모든 슬롯을 "없음" 상태로 초기화
        for (PartType type : PartType.values()) {
            partInfoMap.put(type, PartInfo.none(type));
        }
    }

    // ──────────────────────────────────────────────
    // CRUD
    // ──────────────────────────────────────────────

    public void setPartInfo(PartType partType, PartInfo partInfo) {
        partInfoMap.put(partType, partInfo);
    }

    public PartInfo getPartInfo(PartType partType) {
        return partInfoMap.get(partType);
    }

    public boolean hasPartInfo(PartType partType) {
        return partInfoMap.containsKey(partType);
    }

    /** 파츠가 실제로 장착되어 있는지 (hasPart == true) */
    public boolean hasPart(PartType partType) {
        PartInfo info = partInfoMap.get(partType);
        return info != null && info.hasPart;
    }

    /** 모든 파츠의 텍스처 ResourceLocation 캐시를 초기화 */
    public void clearTextures() {
        for (PartInfo info : partInfoMap.values()) {
            info.setTexture(null);
        }
    }

    /** 깊은 복사 (GUI 편집 시 원본 보존용) */
    public PartsData deepCopy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), PartsData.class);
    }

    // ──────────────────────────────────────────────
    // equals / hashCode / toString
    // ──────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartsData pd = (PartsData) o;
        return partInfoMap.equals(pd.partInfoMap);
    }

    @Override
    public int hashCode() {
        return partInfoMap.hashCode();
    }

    @Override
    public String toString() {
        return "PartsData{partInfoMap=" + partInfoMap + "}";
    }
}