/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import kihira.tails.client.model.ModelDevilTail;
import kihira.tails.common.TailInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class RenderDevilTail extends RenderTail {

    private String[] skinNames = {"devilTail"};

    private ModelDevilTail modelDevilTail = new ModelDevilTail();

    public RenderDevilTail() {
        super("devil");
    }

    @Override
    public void doRender(EntityLivingBase entity, TailInfo info, float partialTicks) {
        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        if (!entity.isSneaking()) GL11.glTranslatef(0F, 0.65F, 0.1F);
        else GL11.glTranslatef(0F, 0.55F, 0.4F);
        GL11.glScalef(0.9F, 0.9F, 0.9F);
        this.modelDevilTail.render(entity, info.subid, partialTicks);
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
