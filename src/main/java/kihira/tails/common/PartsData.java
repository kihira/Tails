/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.UUID;

public class PartsData {

    @Expose public final UUID uuid;
    @Expose private PartInfo[] partInfos = new PartInfo[PartType.values().length];

    public PartsData(UUID uuid) {
        this.uuid = uuid;
    }

    public void setPartInfo(PartType partType, PartInfo partInfo) {
        partInfos[partType.ordinal()] = partInfo;
    }

    public PartInfo getPartInfo(PartType partType) {
        return partInfos[partType.ordinal()];
    }

    public boolean hasPartInfo(PartType partType) {
        PartInfo partInfo = partInfos[partType.ordinal()];
        return partInfo != null && partInfo.hasPart;
    }

    public void clearTextures() {
        for (PartInfo partInfo : partInfos) {
            if (partInfo != null) partInfo.setTexture(null);
        }
    }

    public PartsData deepCopy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), PartsData.class);
    }

    //NOTE: We rely on the order of this, don't re-arrange, only append!
    public enum PartType {
        TAIL,
        EARS,
        WINGS
    }
}
