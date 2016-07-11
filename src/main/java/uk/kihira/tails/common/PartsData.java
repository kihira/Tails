/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

public class PartsData {

    @Expose
    public Map<PartType, PartInfo> partInfoMap = new HashMap<PartType, PartInfo>(PartType.values().length);

    public PartsData() {}

    public void setPartInfo(PartType partType, PartInfo partInfo) {
        partInfoMap.put(partType, partInfo);
    }

    public PartInfo getPartInfo(PartType partType) {
        return partInfoMap.get(partType);
    }

    public boolean hasPartInfo(PartType partType) {
        return partInfoMap.containsKey(partType) && partInfoMap.get(partType).hasPart;
    }

    public void clearTextures() {
        for (PartInfo partInfo : partInfoMap.values()) {
            if (partInfo != null) partInfo.setTexture(null);
        }
    }

    public PartsData deepCopy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), PartsData.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartsData partsData = (PartsData) o;

        return partInfoMap != null ? partInfoMap.equals(partsData.partInfoMap) : partsData.partInfoMap == null;

    }

    @Override
    public int hashCode() {
        return partInfoMap != null ? partInfoMap.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PartsData{" +
                "partInfoMap=" + partInfoMap +
                '}';
    }

    //NOTE: We rely on the order of this, don't re-arrange, only append! Order is for legacy reasons
    public enum PartType {
        TAIL,
        EARS,
        WINGS,
        MUZZLE
    }
}
