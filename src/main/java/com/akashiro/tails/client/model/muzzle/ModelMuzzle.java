package com.akashiro.tails.client.model.muzzle;

import com.akashiro.tails.client.model.LegacyModelRenderer;
import com.akashiro.tails.client.model.ModelPartBase;
import net.minecraft.world.entity.LivingEntity;

public class ModelMuzzle extends ModelPartBase {
    private final LegacyModelRenderer stubMuzzle;
    private final LegacyModelRenderer tinyMuzzle;

    private final LegacyModelRenderer muzzle;

    public ModelMuzzle(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize, int xTex, int yTex) {
        textureWidth = textureHeight = 32;

        muzzle = new LegacyModelRenderer(this, xTex, yTex);
        muzzle.addBox(xOffset, yOffset, zOffset, xSize, ySize, zSize);

        stubMuzzle = new LegacyModelRenderer(this);
        stubMuzzle.addBox(-2f, -4f, -7f, 4, 4, 3);

        tinyMuzzle = new LegacyModelRenderer(this);
        tinyMuzzle.addBox(-2f, -2f, -5f, 4, 2, 1);
    }

    public ModelMuzzle(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize) {
        this(xOffset, yOffset, zOffset, xSize, ySize, zSize, 0, 0);
    }

    public void render(LivingEntity theEntity, int subtype, float partialTicks) {
        switch (subtype) {
            case 0: // Very Short
                translate(0f, 0f, 4f / 16f);
                muzzle.render(ModelPartBase.SCALE);
                break;
            case 1: // Short
                translate(0f, 0f, 3f / 16f);
                muzzle.render(ModelPartBase.SCALE);
                break;
            case 2: // Standard
                translate(0f, 0f, 2f / 16f);
                muzzle.render(ModelPartBase.SCALE);
                break;
            case 3: // Long
                translate(0f, 0f, 1f / 16f);
                muzzle.render(ModelPartBase.SCALE);
                break;
            case 4: // Very Long
                muzzle.render(ModelPartBase.SCALE);
                break;
        }
    }
}
