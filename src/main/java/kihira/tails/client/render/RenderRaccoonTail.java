/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import kihira.tails.client.model.ModelRaccoonTail;

public class RenderRaccoonTail extends RenderTail {

    private String[] skinNames = {"racoonTail"};

    public RenderRaccoonTail() {
        super("raccoon", new ModelRaccoonTail());
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
