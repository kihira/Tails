/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model.wings;

import kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;

public class ModelBigWings extends ModelPartBase {

    ModelRenderer rightWing;
    ModelRenderer leftWing;

    public ModelBigWings() {
        textureWidth = 64;
        textureHeight = 32;

        rightWing = new ModelRenderer(this, 0, 0);
        // rightWing = new ModelRenderer(this, 0, 31);
        rightWing.addBox(-6F, 2F, -1F, 17, 30, 1);
        rightWing.setRotationPoint(0F, 0F, 0F);
        rightWing.setTextureSize(32, 32);
        rightWing.mirror = true;
        setRotation(rightWing, 1.047198F, 0F, 1.745329F);
        // setRotation(rightWing, 2.094395F, 0F, -1.396263F);

        leftWing = new ModelRenderer(this, 0, 0);
        leftWing.addBox(-6F, 2F, 0F, 17, 30, 1);
        leftWing.setRotationPoint(0F, 0F, 0F);
        leftWing.setTextureSize(32, 32);
        leftWing.mirror = true;
        setRotation(leftWing, 2.094395F, 0F, 1.396263F);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
/*        if (wingId != 0) {
            GL11.glDisable(GL11.GL_LIGHTING);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        }*/

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(15728880);

        rightWing.render(0.0625F);
        leftWing.render(0.0625F);

/*        if (wingId != 0) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
            GL11.glEnable(GL11.GL_LIGHTING);
        }*/
    }
}
