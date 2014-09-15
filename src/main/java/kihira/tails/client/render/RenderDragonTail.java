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
import org.lwjgl.opengl.GL11;

public class RenderDragonTail extends RenderTail {

	private String[] skinNames = {"dragonTail"};
	
    private ModelDragonTail modelDragonTail = new ModelDragonTail();

    public RenderDragonTail() {
        super("dragon");
    }

    @Override
    public void doRender(EntityLivingBase entity, TailInfo info, float partialTicks) {
        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        if (!entity.isSneaking()) GL11.glTranslatef(0F, 0.68F, 0.1F);
        else GL11.glTranslatef(0F, 0.6F, 0.35F);
        GL11.glScalef(0.8F, 0.8F, 0.8F);
        this.modelDragonTail.render(entity, info.subid, partialTicks);
        GL11.glPopMatrix();
    }

	@Override
	public String[] getTextureNames() {
		return skinNames;
	}

    @Override
    public int getAvailableSubTypes() {
        return 1;
    }

}
