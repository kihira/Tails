/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.model.tail.ModelCatTail;
import uk.kihira.tails.client.model.tail.ModelDevilTail;
import uk.kihira.tails.client.model.tail.ModelDragonTail;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class PlayerRenderHelper implements IRenderHelper {

    @Override
    public void onPreRenderTail(EntityLivingBase entity, RenderPart tail, PartInfo info, double x, double y, double z) {
        if (info.partType == PartsData.PartType.EARS || info.partType == PartsData.PartType.MUZZLE || info.partType == PartsData.PartType.WINGS) return;
        if (tail.modelPart instanceof ModelDragonTail) {
            GL11.glTranslatef(0F, 0.68F, 0.1F);
            GL11.glScalef(0.8F, 0.8F, 0.8F);
        }
        else if (tail.modelPart instanceof ModelCatTail || tail.modelPart instanceof ModelDevilTail) {
            GL11.glTranslatef(0F, 0.65F, 0.1F);
            GL11.glScalef(0.9F, 0.9F, 0.9F);
        }
        else {
            if (entity.isSneaking()) GL11.glTranslatef(0f, 0.82f, 0f);
            else GL11.glTranslatef(0F, 0.65F, 0.1F);
            GL11.glScalef(0.8F, 0.8F, 0.8F);
        }
    }
}
