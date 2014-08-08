package kihira.tails.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import java.util.List;

public class ModelRacoonTail extends ModelTailBase {

    ModelRenderer tailBase;
    ModelRenderer tail1;
    ModelRenderer tail2;
    ModelRenderer tailTip;

    public ModelRacoonTail() {

        tailBase = new ModelRenderer(this, 12, 16);
        tailBase.addBox(-1F, -1F, 0F, 2, 2, 2);
        tailBase.setRotationPoint(0F, 0F, 0F);

        tail1 = new ModelRenderer(this, 0, 16);
        tail1.addBox(-1.5F, -1.5F, 0F, 3, 3, 3);
        tail1.setRotationPoint(0F, 0F, 1F);
        tail1.rotateAngleX = (float) Math.toRadians(-40F);

        tail2 = new ModelRenderer(this, 0, 0);
        tail2.addBox(-2F, -2F, 0F, 4, 4, 12);
        tail2.setRotationPoint(0F, 0F, 2F);
        tail2.rotateAngleX = (float) Math.toRadians(-30F);

        tailTip = new ModelRenderer(this, 0, 22);
        tailTip.addBox(-1.5F, -1.5F, 0F, 3, 3, 1);
        tailTip.setRotationPoint(0F, 0F, 12F);

        tail2.addChild(tailTip);
        tail1.addChild(tail2);
        tailBase.addChild(tail1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        tailBase.render(f5);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entity) {
        float seed = (entity.hashCode() + System.nanoTime() / 100000000F) / 8F; //Change final number to control speed

        this.tailBase.rotateAngleY = MathHelper.cos(seed - 1) / 20F;
        this.tail1.rotateAngleY = MathHelper.cos(seed - 2) / 20F;
        this.tail2.rotateAngleY = (MathHelper.cos(seed - 3) / 20F);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public List<String> getToggleableParts() {
        return null;
    }
}
