/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import uk.kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;

public class ModelCatEars extends ModelPartBase {

    final ModelRenderer leftEarBottom;
    final ModelRenderer leftEarRearTop;
    final ModelRenderer leftEarRearLayer1;
    final ModelRenderer leftEarRearBottom;
    final ModelRenderer leftEarLayer1;
    final ModelRenderer leftEarTop;
    final ModelRenderer leftEarLayer3;
    final ModelRenderer leftEarLayer2;
    final ModelRenderer rightEarBottom;
    final ModelRenderer rightEarLayer1;
    final ModelRenderer rightEarRearTop;
    final ModelRenderer rightEarRearLayer1;
    final ModelRenderer rightEarRearBottom;
    final ModelRenderer rightEarLayer2;
    final ModelRenderer rightEarTop;
    final ModelRenderer rightEarLayer3;

    public ModelCatEars() {
        textureWidth = 64;
        textureHeight = 32;

        leftEarBottom = new ModelRenderer(this, 0, 0);
        leftEarBottom.addBox(0F, 0F, 0F, 1, 1, 1);
        leftEarBottom.setRotationPoint(4F, -8F, 0F);
        leftEarBottom.setTextureSize(64, 32);
        leftEarBottom.mirror = true;

        leftEarRearTop = new ModelRenderer(this, 0, 16);
        leftEarRearTop.addBox(0F, -3F, 1F, 1, 1, 1);
        leftEarRearTop.setRotationPoint(4F, -8F, 0F);
        leftEarRearTop.setTextureSize(64, 32);
        leftEarRearTop.mirror = true;

        leftEarRearLayer1 = new ModelRenderer(this, 0, 14);
        leftEarRearLayer1.addBox(-1F, -2F, 1F, 2, 1, 1);
        leftEarRearLayer1.setRotationPoint(4F, -8F, 0F);
        leftEarRearLayer1.setTextureSize(64, 32);
        leftEarRearLayer1.mirror = true;

        leftEarRearBottom = new ModelRenderer(this, 0, 12);
        leftEarRearBottom.addBox(-2F, -1F, 1F, 3, 1, 1);
        leftEarRearBottom.setRotationPoint(4F, -8F, 0F);
        leftEarRearBottom.setTextureSize(64, 32);
        leftEarRearBottom.mirror = true;

        leftEarLayer1 = new ModelRenderer(this, 0, 2);
        leftEarLayer1.addBox(-3F, -1F, 0F, 5, 1, 1);
        leftEarLayer1.setRotationPoint(4F, -8F, 0F);
        leftEarLayer1.setTextureSize(64, 32);
        leftEarLayer1.mirror = true;

        leftEarTop = new ModelRenderer(this, 0, 8);
        leftEarTop.addBox(0F, -4F, 0F, 1, 1, 1);
        leftEarTop.setRotationPoint(4F, -8F, 0F);
        leftEarTop.setTextureSize(64, 32);
        leftEarTop.mirror = true;

        leftEarLayer3 = new ModelRenderer(this, 0, 6);
        leftEarLayer3.addBox(-1F, -3F, 0F, 3, 1, 1);
        leftEarLayer3.setRotationPoint(4F, -8F, 0F);
        leftEarLayer3.setTextureSize(64, 32);
        leftEarLayer3.mirror = true;

        leftEarLayer2 = new ModelRenderer(this, 0, 4);
        leftEarLayer2.addBox(-2F, -2F, 0F, 4, 1, 1);
        leftEarLayer2.setRotationPoint(4F, -8F, 0F);
        leftEarLayer2.setTextureSize(64, 32);
        leftEarLayer2.mirror = true;

        rightEarBottom = new ModelRenderer(this, 13, 0);
        rightEarBottom.addBox(-1F, 0F, 0F, 1, 1, 1);
        rightEarBottom.setRotationPoint(-4F, -8F, 0F);
        rightEarBottom.setTextureSize(64, 32);
        rightEarBottom.mirror = true;

        rightEarLayer1 = new ModelRenderer(this, 13, 2);
        rightEarLayer1.addBox(-2F, -1F, 0F, 5, 1, 1);
        rightEarLayer1.setRotationPoint(-4F, -8F, 0F);
        rightEarLayer1.setTextureSize(64, 32);
        rightEarLayer1.mirror = true;

        rightEarRearTop = new ModelRenderer(this, 13, 16);
        rightEarRearTop.addBox(-1F, -3F, 1F, 1, 1, 1);
        rightEarRearTop.setRotationPoint(-4F, -8F, 0F);
        rightEarRearTop.setTextureSize(64, 32);
        rightEarRearTop.mirror = true;

        rightEarRearLayer1 = new ModelRenderer(this, 13, 14);
        rightEarRearLayer1.addBox(-1F, -2F, 1F, 2, 1, 1);
        rightEarRearLayer1.setRotationPoint(-4F, -8F, 0F);
        rightEarRearLayer1.setTextureSize(64, 32);
        rightEarRearLayer1.mirror = true;

        rightEarRearBottom = new ModelRenderer(this, 13, 12);
        rightEarRearBottom.addBox(-1F, -1F, 1F, 3, 1, 1);
        rightEarRearBottom.setRotationPoint(-4F, -8F, 0F);
        rightEarRearBottom.setTextureSize(64, 32);
        rightEarRearBottom.mirror = true;

        rightEarLayer2 = new ModelRenderer(this, 13, 4);
        rightEarLayer2.addBox(-2F, -2F, 0F, 4, 1, 1);
        rightEarLayer2.setRotationPoint(-4F, -8F, 0F);
        rightEarLayer2.setTextureSize(64, 32);
        rightEarLayer2.mirror = true;

        rightEarTop = new ModelRenderer(this, 13, 8);
        rightEarTop.addBox(-1F, -4F, 0F, 1, 1, 1);
        rightEarTop.setRotationPoint(-4F, -8F, 0F);
        rightEarTop.setTextureSize(64, 32);
        rightEarTop.mirror = true;

        rightEarLayer3 = new ModelRenderer(this, 13, 6);
        rightEarLayer3.addBox(-2F, -3F, 0F, 3, 1, 1);
        rightEarLayer3.setRotationPoint(-4F, -8F, 0F);
        rightEarLayer3.setTextureSize(64, 32);
        rightEarLayer3.mirror = true;
    }
    
    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        leftEarBottom.render(0.0635F);
        leftEarRearTop.render(0.0635F);
        leftEarRearLayer1.render(0.0635F);
        leftEarRearBottom.render(0.0635F);
        leftEarLayer1.render(0.0635F);
        leftEarTop.render(0.0635F);
        leftEarLayer3.render(0.0635F);
        leftEarLayer2.render(0.0635F);
        rightEarBottom.render(0.0635F);
        rightEarLayer1.render(0.0635F);
        rightEarRearTop.render(0.0635F);
        rightEarRearLayer1.render(0.0635F);
        rightEarRearBottom.render(0.0635F);
        rightEarLayer2.render(0.0635F);
        rightEarTop.render(0.0635F);
        rightEarLayer3.render(0.0635F);
    }
}
