package uk.kihira.tails.client.model.wings;

import net.minecraft.client.renderer.GlStateManager;
import uk.kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ModelMetalWings extends ModelPartBase {

    final ModelRenderer wing;

    public ModelMetalWings() {
        textureWidth = 64;
        textureHeight = 32;

        wing = new ModelRenderer(this, 0, 0);
        wing.addBox(0F, 0F, 0.5F, 20, 29, 1);
        wing.setTextureSize(32, 32);
        wing.mirror = true;
    }

    @Override
    public void render(EntityLivingBase theEntity, float partialTicks) {
        GlStateManager.translate(0, -7F * SCALE, 1F * SCALE * 2);

        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.rotate(90, 0, 0, 1);

        boolean isFlying = theEntity instanceof EntityPlayer && ((EntityPlayer) theEntity).capabilities.isFlying && theEntity.isAirBorne || theEntity.fallDistance > 0F;
        float timestep = getAnimationTime(isFlying ? 500 : 6000, theEntity);
        float angle = (float) Math.sin(timestep) * (isFlying ? 20F : 6F);

        GlStateManager.translate(0F, -0.5F * SCALE, 0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, 0F, 2F * SCALE);
        GlStateManager.rotate(30F - angle, 1F, 0F, 0F);
        GlStateManager.translate(0F, 0F, -1F * SCALE);
        wing.render(SCALE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, 0F, -2F * SCALE);
        GlStateManager.rotate(-30F + angle, 1F, 0F, 0F);
        GlStateManager.translate(0F, 0F, -1F * SCALE);
        wing.render(SCALE);
        GlStateManager.translate(0F, 0F, 1F * SCALE);
        GlStateManager.popMatrix();
    }
}
