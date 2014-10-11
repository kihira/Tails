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
import kihira.tails.client.model.ears.ModelFoxEars;
import kihira.tails.client.model.tail.*;
import kihira.tails.client.render.RenderPart;

import java.util.UUID;

public class PartsData {

    @Expose public final UUID uuid;
    //Keeping these as fields vs a map should prove better performance wise
    @Expose private PartInfo tailInfo;
    @Expose private PartInfo earsInfo;

    public PartsData(UUID uuid) {
        this.uuid = uuid;
    }

    public void setPartInfo(PartType partType, PartInfo partInfo) {
        switch (partType) {
            case EARS: {
                earsInfo = partInfo;
                break;
            }
            case TAIL: {
                tailInfo = partInfo;
                break;
            }
        }
    }

    public PartInfo getPartInfo(PartType partType) {
        switch (partType) {
            case EARS: {
                return earsInfo;
            }
            case TAIL: {
                return tailInfo;
            }
        }
        return null;
    }

    public boolean hasPartInfo(PartType partType) {
        PartInfo partInfo = null;
        switch (partType) {
            case EARS: {
                partInfo = earsInfo;
                break;
            }
            case TAIL: {
                partInfo = tailInfo;
                break;
            }
        }
        return partInfo != null && partInfo.hasPart;
    }

    public void clearTextures() {
        if (earsInfo != null) earsInfo.setTexture(null);
        if (tailInfo != null) tailInfo.setTexture(null);
    }

    public PartsData deepCopy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), PartsData.class);
    }

    public enum PartType {
        EARS(new RenderPart("fox.ear", 0, new ModelFoxEars(), "foxear")),
        TAIL(new RenderPart("fluffy", 2, new ModelFluffyTail(), "foxTail"),
             new RenderPart("dragon", 1, new ModelDragonTail(), "dragonTail", "dragonTailStriped"),
             new RenderPart("raccoon", 0, new ModelRaccoonTail(), "racoonTail"),
             new RenderPart("devil", 1, new ModelDevilTail(), "devilTail"),
             new RenderPart("cat", 0, new ModelCatTail(), "tabbyTail", "tigerTail"),
             new RenderPart("bird", 0, new ModelBirdTail(), "birdTail"));
        //WINGS();

        public final RenderPart[] renderParts;

        PartType(RenderPart ... renderParts) {
            this.renderParts = renderParts;
        }
    }
}
