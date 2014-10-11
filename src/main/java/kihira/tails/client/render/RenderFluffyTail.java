/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.model.ModelFluffyTail;

@SideOnly(Side.CLIENT)
public class RenderFluffyTail extends RenderPart {

	private String[] skinNames = {"foxTail"};

    public RenderFluffyTail() {
        super("fluffy", new ModelFluffyTail());
    }
    
    @Override
	public String[] getTextureNames(int subid) {
		return skinNames;
	}

    @Override
    public int getAvailableSubTypes() {
        return 2;
    }
}
