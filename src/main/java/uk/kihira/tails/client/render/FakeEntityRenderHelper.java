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
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.common.PartInfo;
import net.minecraft.entity.EntityLivingBase;


public class FakeEntityRenderHelper implements IRenderHelper {

    @Override
    public void onPreRenderTail(EntityLivingBase entity, OutfitPart outfitPart) {
        switch (outfitPart.mountPoint) {
            case CHEST: { GlStateManager.translate(0F, 0.65F, 0F);
                GlStateManager.scale(0.9F, 0.9F, 0.9F);
                break;
            }
            // todo fake head using players skin?
            case HEAD:
                GlStateManager.translate(0.2F, 1.25F, 0F);
                GlStateManager.rotate(180F, 0F, 1F, 0F);
                GlStateManager.rotate(-45F, 0F, 1F, 0F);
                GlStateManager.rotate(25F, 1F, 0F, 0F);
                break;
        }
    }
}
