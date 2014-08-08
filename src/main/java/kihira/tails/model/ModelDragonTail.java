package kihira.tails.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelDragonTail extends ModelBase {
/*
    ModelRenderer tailBase;
    ModelRenderer tail1;
    ModelRenderer tail2;
    ModelRenderer tail3;

    public ModelDragonTail() {
        textureWidth = 64;
        textureHeight = 32;

        tailBase = new ModelRenderer(this, 0, 0);
        tailBase.addBox(-2.5F, -2.5F, -1F, 5, 5, 7);
        tailBase.setRotationPoint(0F, 0F, 0F);
        tailBase.setTextureSize(64, 32);
        tailBase.mirror = true;
        setRotation(tailBase, -0.4712389F, 0F, 0F);
        tail1 = new ModelRenderer(this, 0, 12);
        //tail1.addBox(-2F, -1.7F, 5.5F, 4, 4, 6);
        tail1.addBox(-2F, -1.7F, 0, 4, 4, 6);
        tail1.setRotationPoint(0F, 0F, 0F);
        tail1.setTextureSize(64, 32);
        tail1.mirror = true;
        setRotation(tail1, -0.4712389F, 0F, 0F);
        tail2 = new ModelRenderer(this, 0, 22);
        tail2.addBox(-1.5F, -0.9F, 11F, 3, 3, 7);
        tail2.setRotationPoint(0F, 0F, 0F);
        tail2.setTextureSize(64, 32);
        tail2.mirror = true;
        setRotation(tail2, -0.4712389F, 0F, 0F);
        tail3 = new ModelRenderer(this, 24, 0);
        tail3.addBox(-1F, -0.2F, 17.5F, 2, 2, 6);
        tail3.setRotationPoint(0F, 0F, 0F);
        tail3.setTextureSize(64, 32);
        tail3.mirror = true;
        setRotation(tail3, -0.4712389F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float mult) {
        this.setRotationAngles(f, f1, f2, f3, f4, mult, entity);

        tailBase.render(mult);
        tail1.render(mult);
        //tail2.render(mult);
        //tail3.render(mult);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entity) {
        float angle = MathHelper.cos((entity.hashCode() + System.nanoTime() / 100000000F) / 5F) / 3.5F;
        //float angle = MathHelper.cos(p_78087_7_.ticksExisted / 10F) / 3.5F;

        this.tailBase.rotateAngleY = angle / 1F;

        float x = (float)((7 * Math.sin(this.tailBase.rotateAngleY)));
        float y = (float)((7 * Math.cos(this.tailBase.rotateAngleY)));

        this.tail1.setRotationPoint(x, 0, y);
        this.tail1.rotateAngleY = 0F;

*//*        this.tail1.rotateAngleY = angle / 5F;
        this.tail2.rotateAngleY = angle / 4F;
        this.tail3.rotateAngleY = angle / 3F;*//*
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }*/
    ModelRenderer tailBase;
    ModelRenderer tail1;
    ModelRenderer tail2;
    ModelRenderer tail3;

    public ModelDragonTail() {

        tailBase = new ModelRenderer(this, 22, 0);
        tailBase.addBox(-2.5F, -2.5F, -2F, 5, 5, 8);
        tailBase.rotateAngleX = (float) Math.toRadians(-40F);

        tail1 = new ModelRenderer(this, 0, 0);
        tail1.addBox(-2F, -2F, 0F, 4, 4, 7);
        tail1.setRotationPoint(0F, 0.3F, 5F);
        tail1.rotateAngleX = (float) Math.toRadians(-8F);

        tail2 = new ModelRenderer(this, 0, 11);
        tail2.addBox(-1.5F, -1.5F, 0F, 3, 3, 8);
        tail2.setRotationPoint(0F, 0.2F, 5.5F);
        tail2.rotateAngleX = (float) Math.toRadians(10F);

        tail3 = new ModelRenderer(this, 0, 22);
        tail3.addBox(-1F, -1F, 0F, 2, 2, 7);
        tail3.setRotationPoint(0F, 0.4F, 7.5F);
        tail3.rotateAngleX = (float) Math.toRadians(20F);

        tail2.addChild(tail3);
        tail1.addChild(tail2);
        tailBase.addChild(tail1);
    }


    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        tailBase.render(f5);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entity) {
        float seed = (entity.hashCode() + System.nanoTime() / 100000000F) / 5F;

        this.tailBase.rotateAngleY = MathHelper.cos(seed - 1) / 5F;
        this.tail1.rotateAngleY = MathHelper.cos(seed - 2) / 5F;
        this.tail2.rotateAngleY = (MathHelper.cos(seed - 3) / 5F);
        this.tail3.rotateAngleY = (MathHelper.cos(seed - 4) / 5F);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
