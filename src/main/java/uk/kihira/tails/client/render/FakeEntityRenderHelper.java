package uk.kihira.tails.client.render;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.entity.LivingEntity;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.outfit.OutfitPart;


public class FakeEntityRenderHelper implements IRenderHelper {

    @Override
    public void onPreRenderPart(LivingEntity entity, OutfitPart outfitPart) {
        switch (outfitPart.mountPoint) {
            case CHEST: { 
                GlStateManager.translatef(0F, 0.65F, 0F);
                GlStateManager.scalef(0.9F, 0.9F, 0.9F);
                break;
            }
            // todo fake head using players skin?
            case HEAD:
                GlStateManager.translatef(0.2F, 1.25F, 0F);
                GlStateManager.rotatef(180F, 0F, 1F, 0F);
                GlStateManager.rotatef(-45F, 0F, 1F, 0F);
                GlStateManager.rotatef(25F, 1F, 0F, 0F);
                break;
        }
    }
}
