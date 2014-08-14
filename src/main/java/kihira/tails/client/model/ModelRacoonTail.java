package kihira.tails.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class ModelRacoonTail extends ModelTailBase {

    private ModelRenderer tailBase;
    private ModelRenderer tail1;
    private ModelRenderer tail2;
    private ModelRenderer tailTip;

    public ModelRacoonTail() {
        this.tailBase = new ModelRenderer(this, 12, 16);
        this.tailBase.addBox(-1F, -1F, 0F, 2, 2, 2);
        this.tailBase.setRotationPoint(0F, 0F, 0F);

        this.tail1 = new ModelRenderer(this, 0, 16);
        this.tail1.addBox(-1.5F, -1.5F, 0F, 3, 3, 3);
        this.tail1.setRotationPoint(0F, 0F, 1F);
        this.setRotationDegrees(this.tail1, -40F, 0F, 0F);

        this.tail2 = new ModelRenderer(this, 0, 0);
        this.tail2.addBox(-2F, -2F, 0F, 4, 4, 12);
        this.tail2.setRotationPoint(0F, 0F, 2F);
        this.setRotationDegrees(this.tail2, -30F, 0F, 0F);

        this.tailTip = new ModelRenderer(this, 0, 22);
        this.tailTip.addBox(-1.5F, -1.5F, 0F, 3, 3, 1);
        this.tailTip.setRotationPoint(0F, 0F, 12F);

        this.tail2.addChild(this.tailTip);
        this.tail1.addChild(this.tail2);
        this.tailBase.addChild(this.tail1);
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        float seed = (entity.hashCode() + System.nanoTime() / 100000000F) / 8F; //Change final number to control speed

        this.tailBase.rotateAngleY = MathHelper.cos(seed - 1) / 20F;
        this.tail1.rotateAngleY = MathHelper.cos(seed - 2) / 20F;
        this.tail2.rotateAngleY = (MathHelper.cos(seed - 3) / 20F);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype) {
        this.setRotationAngles(0, 0, 0, 0, 0, 0, theEntity);

        this.tailBase.render(0.0625F);
    }
}