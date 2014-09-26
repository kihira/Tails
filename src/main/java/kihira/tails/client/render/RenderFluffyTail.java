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
import kihira.tails.common.TailInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

@SideOnly(Side.CLIENT)
public class RenderFluffyTail extends RenderTail {

	private String[] skinNames = {"foxTail"};
	
    private ModelFluffyTail modelFluffyTail = new ModelFluffyTail();

    public RenderFluffyTail() {
        super("fluffy");
    }

    @Override
    public void doRender(EntityLivingBase entity, TailInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        this.modelFluffyTail.render(entity, info.subid, partialTicks);
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
