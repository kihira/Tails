package kihira.tails.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ModelDevilTail extends ModelTailBase {

    ModelRenderer tailBase;
    ModelRenderer tail1;
    ModelRenderer tail2;
    ModelRenderer tail3;
    ModelRenderer tail4;
    ModelRenderer tail5;
    ModelRenderer tailTip;

    public ModelDevilTail() {
        tailBase = new ModelRenderer(this, 0, 0);
        tailBase.addBox(-1F, -1F, 0F, 2, 2, 2);
        tailBase.setRotationPoint(0F, 0F, 0F);
        this.setRotationDegrees(this.tailBase, -30F, 0F, 0F);

        tail1 = new ModelRenderer(this, 0, 4);
        tail1.addBox(-0.5F, -0.5F, 0F, 1, 1, 4);
        tail1.setRotationPoint(0F, 0F, 1.8F);
        this.setRotationDegrees(this.tail1, -30F, 0F, 0F);

        tail2 = new ModelRenderer(this, 0, 9);
        tail2.addBox(-0.5F, -0.5F, 0F, 1, 1, 5);
        tail2.setRotationPoint(0F, 0F, 3.8F);
        this.setRotationDegrees(this.tail2, -30F, 0F, 0F);

        tail3 = new ModelRenderer(this, 0, 15);
        tail3.addBox(-0.5F, -0.5F, 0F, 1, 1, 3);
        tail3.setRotationPoint(0F, 0F, 4.8F);
        this.setRotationDegrees(this.tail3, 20F, 0F, 0F);

        tail4 = new ModelRenderer(this, 0, 19);
        tail4.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
        tail4.setRotationPoint(0F, 0F, 2.6F);
        this.setRotationDegrees(this.tail4, 50F, 0F, 0F);

        tail5 = new ModelRenderer(this, 0, 22);
        tail5.addBox(-0.5F, -0.5F, 0F, 1, 1, 2);
        tail5.setRotationPoint(0F, 0F, 1.7F);
        this.setRotationDegrees(this.tail5, 50F, 0F, 0F);

        tailTip = new ModelRenderer(this, 12, 0);
        tailTip.addBox(-2.5F, 0F, 0F, 5, 5, 0);
        tailTip.setRotationPoint(0F, 0F, 1.5F);
        this.setRotationDegrees(this.tailTip, 120F, 0F, 0F);

        this.tail5.addChild(this.tailTip);
        this.tail4.addChild(this.tail5);
        this.tail3.addChild(this.tail4);
        this.tail2.addChild(this.tail3);
        this.tail1.addChild(this.tail2);
        this.tailBase.addChild(this.tail1);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.tailBase.render(f5);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype) {
        this.tailBase.render(0.0625F);
    }
}
