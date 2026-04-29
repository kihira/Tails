package com.akashiro.tails.client.model.ears;

import com.akashiro.tails.client.model.ModelPartBase;
import com.akashiro.tails.client.model.LegacyModelRenderer;
import net.minecraft.world.entity.LivingEntity;

public class ModelCatSmallEars extends ModelPartBase {
    public LegacyModelRenderer leftEarBottom;
    public LegacyModelRenderer leftEarRearLayer1;
    public LegacyModelRenderer leftEarRearBottom;
    public LegacyModelRenderer leftEarLayer1;
    public LegacyModelRenderer leftEarLayer3;
    public LegacyModelRenderer leftEarLayer2;
    public LegacyModelRenderer rightEarBottom;
    public LegacyModelRenderer rightEarLayer1;
    public LegacyModelRenderer rightEarRearLayer1;
    public LegacyModelRenderer rightEarRearBottom;
    public LegacyModelRenderer rightEarLayer2;
    public LegacyModelRenderer rightEarLayer3;

    public ModelCatSmallEars() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.rightEarRearLayer1 = new LegacyModelRenderer(this, 13, 14);
        this.rightEarRearLayer1.setRotationPoint(-3.0F, -8.0F, 0.0F);
        this.rightEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
        this.leftEarRearLayer1 = new LegacyModelRenderer(this, 0, 14);
        this.leftEarRearLayer1.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
        this.rightEarRearBottom = new LegacyModelRenderer(this, 13, 12);
        this.rightEarRearBottom.setRotationPoint(-3.0F, -8.0F, 0.0F);
        this.rightEarRearBottom.addBox(-1.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
        this.leftEarLayer1 = new LegacyModelRenderer(this, 0, 2);
        this.leftEarLayer1.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarLayer1.addBox(-3.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
        this.leftEarLayer2 = new LegacyModelRenderer(this, 0, 4);
        this.leftEarLayer2.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
        this.rightEarLayer1 = new LegacyModelRenderer(this, 13, 2);
        this.rightEarLayer1.setRotationPoint(-3.0F, -8.0F, 0.0F);
        this.rightEarLayer1.addBox(-2.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
        this.rightEarBottom = new LegacyModelRenderer(this, 13, 0);
        this.rightEarBottom.setRotationPoint(-4.0F, -8.0F, 0.0F);
        this.rightEarBottom.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.leftEarRearBottom = new LegacyModelRenderer(this, 0, 12);
        this.leftEarRearBottom.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarRearBottom.addBox(-2.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
        this.rightEarLayer3 = new LegacyModelRenderer(this, 13, 6);
        this.rightEarLayer3.setRotationPoint(-2.0F, -8.0F, 0.0F);
        this.rightEarLayer3.addBox(-2.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F);
        this.leftEarBottom = new LegacyModelRenderer(this, 0, 0);
        this.leftEarBottom.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarBottom.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.rightEarLayer2 = new LegacyModelRenderer(this, 13, 4);
        this.rightEarLayer2.setRotationPoint(-3.0F, -8.0F, 0.0F);
        this.rightEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
        this.leftEarLayer3 = new LegacyModelRenderer(this, 0, 6);
        this.leftEarLayer3.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarLayer3.addBox(-1.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F);
    }

        public void render(LivingEntity theEntity, int subtype, float partialTicks) {
        this.rightEarRearLayer1.render(0.0625f);
        this.leftEarRearLayer1.render(0.0625f);
        this.rightEarRearBottom.render(0.0625f);
        this.leftEarLayer1.render(0.0625f);
        this.leftEarLayer2.render(0.0625f);
        this.rightEarLayer1.render(0.0625f);
        this.rightEarBottom.render(0.0625f);
        this.leftEarRearBottom.render(0.0625f);
        this.rightEarLayer3.render(0.0625f);
        this.leftEarBottom.render(0.0625f);
        this.rightEarLayer2.render(0.0625f);
        this.leftEarLayer3.render(0.0625f);
    }
}
