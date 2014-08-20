package kihira.tails.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class ModelDragonTail extends ModelTailBase {

    private ModelRenderer tailBase;
    private ModelRenderer tail1;
    private ModelRenderer tail2;
    private ModelRenderer tail3;

    private ModelRenderer tailSubBase;
    private ModelRenderer tailSub1;
    private ModelRenderer tailSub2;
    private ModelRenderer tailSub3;

    public ModelDragonTail() {
        this.tailBase = new ModelRenderer(this, 22, 0);
        this.tailBase.addBox(-2.5F, -2.5F, -2F, 5, 5, 8);
        this.setRotationDegrees(this.tailBase, -40F, 0F, 0F);

        this.tail1 = new ModelRenderer(this, 0, 0);
        this.tail1.addBox(-2F, -2F, 0F, 4, 4, 7);
        this.tail1.setRotationPoint(0F, 0.3F, 5F);
        this.setRotationDegrees(this.tail1, -8F, 0F, 0F);

        this.tail2 = new ModelRenderer(this, 0, 11);
        this.tail2.addBox(-1.5F, -1.5F, 0F, 3, 3, 8);
        this.tail2.setRotationPoint(0F, 0.2F, 5.5F);
        this.setRotationDegrees(this.tail2, 10F, 0F, 0F);

        this.tail3 = new ModelRenderer(this, 0, 22);
        this.tail3.addBox(-1F, -1F, 0F, 2, 2, 7);
        this.tail3.setRotationPoint(0F, 0.4F, 7.5F);
        this.setRotationDegrees(this.tail3, 20F, 0F, 0F);

        this.tail2.addChild(this.tail3);
        this.tail1.addChild(this.tail2);
        this.tailBase.addChild(this.tail1);


        this.tailSubBase = new ModelRenderer(this, 22, 5);
        this.tailSubBase.addBox(0F, -7.25F, -2F, 0, 5, 8);
        this.setRotationDegrees(this.tailSubBase, -40F, 0F, 0F);

        this.tailSub1 = new ModelRenderer(this, 22, 11);
        this.tailSub1.addBox(0F, -6.75F, 1F, 0, 5, 7);
        this.tailSub1.setRotationPoint(0F, 0.3F, 5F);
        this.setRotationDegrees(this.tailSub1, -8F, 0F, 0F);

        this.tailSub2 = new ModelRenderer(this, 22, 15);
        this.tailSub2.addBox(0F, -6.25F, 1F, 0, 5, 8);
        this.tailSub2.setRotationPoint(0F, 0.2F, 5.5F);
        this.setRotationDegrees(this.tailSub2, 10F, 0F, 0F);

        this.tailSub3 = new ModelRenderer(this, 29, 6);
        this.tailSub3.addBox(0F, -5.75F, 1F, 0, 5, 7);
        this.tailSub3.setRotationPoint(0F, 0.4F, 7.5F);
        this.setRotationDegrees(this.tailSub3, 20F, 0F, 0F);

        this.tailSub2.addChild(this.tailSub3);
        this.tailSub1.addChild(this.tailSub2);
        this.tailSubBase.addChild(this.tailSub1);
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float subtype, float partialTicks, Entity entity) {
        float seed = this.getAnimationTime(4000, entity);

        this.tailBase.rotateAngleY = ((float) Math.cos(seed - 1) / 5F);
        this.tail1.rotateAngleY = (float) Math.cos(seed - 2) / 5F;
        this.tail2.rotateAngleY = (float) Math.cos(seed - 3) / 5F;
        this.tail3.rotateAngleY = (float) Math.cos(seed - 4) / 5F;

        if (subtype == 1) {
            this.tailSubBase.rotateAngleY = (float) Math.cos(seed - 1) / 5F;
            this.tailSub1.rotateAngleY = (float) Math.cos(seed - 2) / 5F;
            this.tailSub2.rotateAngleY = (float) Math.cos(seed - 3) / 5F;
            this.tailSub3.rotateAngleY = (float) Math.cos(seed - 4) / 5F;
        }
    }


    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        this.setRotationAngles(0, 0, 0, 0, subtype, partialTicks, theEntity);

        this.tailBase.render(0.0625F);

        if (subtype == 1) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            this.tailSubBase.render(0.0625F);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }
}
