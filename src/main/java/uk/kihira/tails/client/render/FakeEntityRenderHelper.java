/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import net.minecraft.client.renderer.GlStateManager;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.common.PartInfo;
import net.minecraft.entity.EntityLivingBase;


public class FakeEntityRenderHelper implements IRenderHelper {

    @Override
    public void onPreRenderTail(EntityLivingBase entity, RenderPart tail, PartInfo info, double x, double y, double z) {
        switch (info.partType) {
            case TAIL: {
                //Nine tails
                if (info.typeid == 0 && info.subid == 2) {
                    GlStateManager.translate(0F, 0.85F, 0F);
                }
                else GlStateManager.translate(0F, 0.65F, 0F);
                GlStateManager.scale(0.9F, 0.9F, 0.9F);
                break;
            }
            // todo fake head using players skin?
            case MUZZLE:
                GlStateManager.translate(0.2F, 1.25F, 0F);
                GlStateManager.rotate(180F, 0F, 1F, 0F);
                GlStateManager.rotate(-45F, 0F, 1F, 0F);
                GlStateManager.rotate(25F, 1F, 0F, 0F);
                break;
            case EARS: {
                GlStateManager.rotate(180F, 0F, 1F, 0F);
                GlStateManager.translate(0F, 1.4F, 0F);
                break;
            }
            case WINGS: {
                GlStateManager.translate(0F, 0.9F, 0F);
                GlStateManager.scale(0.6F, 0.6F, 0.6F);
                break;
            }
        }
    }
}
