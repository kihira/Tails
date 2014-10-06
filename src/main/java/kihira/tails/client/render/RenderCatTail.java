/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import kihira.tails.client.model.ModelCatTail;

public class RenderCatTail extends RenderTail {

    private String[] skinNames = {"tabbyTail", "tigerTail"};

    public RenderCatTail() {
        super("cat", new ModelCatTail());
    }

    @Override
    public String[] getTextureNames(int subid) {
        return skinNames;
    }

    @Override
    public int getAvailableSubTypes() {
        return 0;
    }
}
