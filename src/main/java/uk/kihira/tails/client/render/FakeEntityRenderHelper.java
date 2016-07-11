/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.common.PartInfo;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class FakeEntityRenderHelper implements IRenderHelper {

    @Override
    public void onPreRenderTail(EntityLivingBase entity, RenderPart tail, PartInfo info, double x, double y, double z) {
        switch (info.partType) {
            case TAIL: {
                //Nine tails
                if (info.typeid == 0 && info.subid == 2) {
                    GL11.glTranslatef(0F, 0.85F, 0F);
                }
                else GL11.glTranslatef(0F, 0.65F, 0F);
                GL11.glScalef(0.9F, 0.9F, 0.9F);
                break;
            }
            // todo fake head using players skin?
            case MUZZLE:
                GL11.glTranslatef(0.2F, 1.25F, 0F);
                GL11.glRotatef(180F, 0F, 1F, 0F);
                GL11.glRotatef(-45F, 0F, 1F, 0F);
                GL11.glRotatef(25F, 1F, 0F, 0F);
                break;
            case EARS: {
                GL11.glRotatef(180F, 0F, 1F, 0F);
                GL11.glTranslatef(0F, 1.4F, 0F);
                break;
            }
            case WINGS: {
                GL11.glTranslatef(0F, 0.9F, 0F);
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                break;
            }
        }
    }
}
