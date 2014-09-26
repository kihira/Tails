/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import kihira.tails.client.model.ModelDragonTail;
import kihira.tails.common.TailInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public class RenderDragonTail extends RenderTail {

	private String[] skinNames = {"dragonTail", "dragonTailStriped"};
	
    private ModelDragonTail modelDragonTail = new ModelDragonTail();

    public RenderDragonTail() {
        super("dragon");
    }

    @Override
    public void doRender(EntityLivingBase entity, TailInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        this.modelDragonTail.render(entity, info.subid, partialTicks);
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
