/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import kihira.tails.client.model.ModelDevilTail;

public class RenderDevilTail extends RenderTail {

    private String[] skinNames = {"devilTail"};

    public RenderDevilTail() {
        super("devil", new ModelDevilTail());
    }

    @Override
    public String[] getTextureNames(int subid) {
        return skinNames;
    }

    @Override
    public int getAvailableSubTypes() {
        return 1;
    }
}
