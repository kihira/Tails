// 경로: src/main/java/com/akashiro/tails/common/data/PartInfo.java
package com.akashiro.tails.common.data;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

/**
 * 파츠 하나의 상세 정보를 담는 클래스.
 * 1.12.2의 PartInfo를 1.20.1 API에 맞게 이식.
 * ResourceLocation은 Gson에서 직렬화하지 않고 런타임에만 사용.
 */
public class PartInfo implements Cloneable {

    // ──────────────────────────────────────────────
    // 직렬화 필드 (Gson @Expose)
    // ──────────────────────────────────────────────

    /** 파츠가 실제로 장착되어 있는가 */
    @Expose
    public boolean hasPart;

    /** 파츠 종류 인덱스 (예: 꼬리 중 fluffy = 0, dragon = 1, ...) */
    @Expose
    public int typeid;

    /** 서브타입 인덱스 (예: fluffy 0=Fluffy, 1=Twin, 2=Nine) */
    @Expose
    public int subid;

    /** 틴트 색상 3개 (RGB int 배열) */
    @Expose
    public int[] tints = new int[]{0xFFFFFF, 0xFFFFFF, 0xFFFFFF};

    /** 텍스처 인덱스 (RenderPart의 textureNames 배열 인덱스) */
    @Expose
    public int textureID;

    /** 파츠 타입 (TAIL/EARS/WINGS/MUZZLE) */
    @Expose
    public PartsData.PartType partType;

    /** 스케일 (머즐 크기 조절 등에 사용) */
    @Expose
    public float scale = 1.0f;

    // ──────────────────────────────────────────────
    // 런타임 전용 필드 (직렬화 안 함)
    // ──────────────────────────────────────────────

    /** 현재 사용 중인 텍스처 위치 (generateTexture 결과 캐시) */
    public transient ResourceLocation texture;

    /** 텍스처를 다시 생성해야 하는지 여부 */
    public transient boolean needsTextureCompile = true;

    // ──────────────────────────────────────────────
    // 생성자
    // ──────────────────────────────────────────────

    public PartInfo() {}

    public PartInfo(boolean hasPart, int typeid, int subid, int[] tints,
                    PartsData.PartType partType, float scale, ResourceLocation texture) {
        this.hasPart  = hasPart;
        this.typeid   = typeid;
        this.subid    = subid;
        this.tints    = tints;
        this.partType = partType;
        this.scale    = scale;
        this.texture  = texture;
    }

    /**
     * 해당 파츠 타입에 대한 "장착 없음" PartInfo 생성.
     * 1.12.2의 PartInfo.none(PartType) 에 해당.
     */
    public static PartInfo none(PartsData.PartType partType) {
        PartInfo info = new PartInfo();
        info.hasPart  = false;
        info.partType = partType;
        info.tints = new int[]{0xFF0000, 0x00FF00, 0x0000FF};
        return info;
    }

    // ──────────────────────────────────────────────
    // 텍스처 관리
    // ──────────────────────────────────────────────

    public ResourceLocation getTexture() {
        return texture;
    }

    public void setTexture(ResourceLocation texture) {
        if (texture != null && this.texture != null && this.texture.equals(texture)) {
            this.needsTextureCompile = false;
            return;
        }

        if (this.texture != null) {
            try {
                Minecraft.getInstance().getTextureManager().release(this.texture);
            } catch (Exception ignored) {
            }
        }

        this.needsTextureCompile = true;
        this.texture = texture;
        if (texture == null) {
            return;
        }

        this.needsTextureCompile = false;
    }

    // ──────────────────────────────────────────────
    // 깊은 복사 (Gson 우회 방식)
    // ──────────────────────────────────────────────

    public PartInfo deepCopy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), PartInfo.class);
    }

    // ──────────────────────────────────────────────
    // equals / hashCode / toString
    // ──────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartInfo p = (PartInfo) o;
        return hasPart   == p.hasPart
                && typeid    == p.typeid
                && subid     == p.subid
                && textureID == p.textureID
                && partType  == p.partType
                && Float.compare(scale, p.scale) == 0
                && Arrays.equals(tints, p.tints);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(hasPart);
        result = 31 * result + typeid;
        result = 31 * result + subid;
        result = 31 * result + Arrays.hashCode(tints);
        result = 31 * result + textureID;
        result = 31 * result + (partType != null ? partType.hashCode() : 0);
        result = 31 * result + Float.hashCode(scale);
        return result;
    }

    @Override
    public String toString() {
        return "PartInfo{hasPart=" + hasPart
                + ", typeid=" + typeid
                + ", subid=" + subid
                + ", tints=" + Arrays.toString(tints)
                + ", textureID=" + textureID
                + ", partType=" + partType
                + ", texture=" + texture
                + "}";
    }
}
