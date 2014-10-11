/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import kihira.tails.client.model.ModelDragonTail;

public class RenderDragonTail extends RenderPart {

	private String[] skinNames = {"dragonTail", "dragonTailStriped"};

    public RenderDragonTail() {
        super("dragon", new ModelDragonTail());
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
