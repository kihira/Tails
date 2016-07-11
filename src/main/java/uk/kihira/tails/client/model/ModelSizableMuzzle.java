package uk.kihira.tails.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class ModelSizableMuzzle extends ModelPartBase {
    private final ModelRenderer stubMuzzle;
    private final ModelRenderer tinyMuzzle;

    private final ModelRenderer muzzle;

    public ModelSizableMuzzle(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize, int xTex, int yTex) {
        textureWidth = textureHeight = 32;

        muzzle = new ModelRenderer(this, xTex, yTex);
        muzzle.addBox(xOffset, yOffset, zOffset, xSize, ySize, zSize);

        stubMuzzle = new ModelRenderer(this);
        stubMuzzle.addBox(-2f, -4f, -7f, 4, 4, 3);

        tinyMuzzle = new ModelRenderer(this);
        tinyMuzzle.addBox(-2f, -2f, -5f, 4, 2, 1);
    }

    public ModelSizableMuzzle(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize) {
        this(xOffset, yOffset, zOffset, xSize, ySize, zSize, 0, 0);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        switch (subtype) {
            case 0: // Very Short
                GlStateManager.translate(0f, 0f, 4f / 16f);
                muzzle.render(ModelPartBase.SCALE);
                break;
            case 1: // Short
                GlStateManager.translate(0f, 0f, 3f / 16f);
                muzzle.render(ModelPartBase.SCALE);
                break;
            case 2: // Standard
                GlStateManager.translate(0f, 0f, 2f / 16f);
                muzzle.render(ModelPartBase.SCALE);
                break;
            case 3: // Long
                GlStateManager.translate(0f, 0f, 1f / 16f);
                muzzle.render(ModelPartBase.SCALE);
                break;
            case 4: // Very Long
                muzzle.render(ModelPartBase.SCALE);
                break;
        }
    }
}
