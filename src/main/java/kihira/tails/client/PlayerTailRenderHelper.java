/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client;

import kihira.tails.api.ITailRenderHelper;
import kihira.tails.client.render.RenderCatTail;
import kihira.tails.client.render.RenderDevilTail;
import kihira.tails.client.render.RenderDragonTail;
import kihira.tails.client.render.RenderTail;
import kihira.tails.common.PartInfo;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class PlayerTailRenderHelper implements ITailRenderHelper {

    @Override
    public void onPreRenderTail(EntityLivingBase entity, RenderTail tail, PartInfo info, double x, double y, double z) {
        if (tail instanceof RenderDragonTail) {
            if (!entity.isSneaking()) GL11.glTranslatef(0F, 0.68F, 0.1F);
            else GL11.glTranslatef(0F, 0.6F, 0.35F);
            GL11.glScalef(0.8F, 0.8F, 0.8F);
        }
        else if (tail instanceof RenderCatTail || tail instanceof RenderDevilTail) {
            if (!entity.isSneaking()) GL11.glTranslatef(0F, 0.65F, 0.1F);
            else GL11.glTranslatef(0F, 0.55F, 0.4F);
            GL11.glScalef(0.9F, 0.9F, 0.9F);
        }
        else {
            if (!entity.isSneaking()) GL11.glTranslatef(0F, 0.65F, 0.1F);
            else GL11.glTranslatef(0F, 0.55F, 0.4F);
            GL11.glScalef(0.8F, 0.8F, 0.8F);
        }
    }
}
