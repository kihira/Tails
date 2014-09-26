/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import kihira.tails.client.model.ModelRaccoonTail;
import kihira.tails.common.TailInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public class RenderRaccoonTail extends RenderTail {

    private String[] skinNames = {"racoonTail"};

    private ModelRaccoonTail modelRaccoonTail = new ModelRaccoonTail();

    public RenderRaccoonTail() {
        super("raccoon");
    }

    @Override
    public void doRender(EntityLivingBase entity, TailInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        this.modelRaccoonTail.render(entity, info.subid, partialTicks);
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
