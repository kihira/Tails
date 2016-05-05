package kihira.tails.client.model.tail;

import kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class ModelSharkTail extends ModelPartBase {

    public ModelRenderer tailBase;
    public ModelRenderer tail1;
    public ModelRenderer fin;
    public ModelRenderer tail2;
    public ModelRenderer tail3;
    public ModelRenderer finBase;
    public ModelRenderer finTop1;
    public ModelRenderer finBot1;
    public ModelRenderer finTop2;
    public ModelRenderer finTop3;
    public ModelRenderer finBot2;
    public ModelRenderer fubBot3;

    public ModelSharkTail() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.finBot1 = new ModelRenderer(this, 26, 27);
        this.finBot1.setRotationPoint(-0.5F, -0.4F, -4.0F);
        this.finBot1.addBox(0.0F, 0.0F, -2.0F, 1, 3, 2, 0.0F);
        this.setRotateAngle(finBot1, 0.091106186954104F, 0.0F, 0.0F);
        this.tail1 = new ModelRenderer(this, 0, 16);
        this.tail1.setRotationPoint(0.0F, 0.0F, 3.5F);
        this.tail1.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 5, 0.0F);
        this.setRotateAngle(tail1, 0.0013962634015954637F, 0.0F, 0.0F);
        this.tailBase = new ModelRenderer(this, 0, 24);
        this.tailBase.setRotationPoint(0.0F, 0.5F, -0.6F);
        this.tailBase.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(tailBase, -0.6522295414702809F, 0.02949606435870417F, 0.0F);
        this.tail2 = new ModelRenderer(this, 0, 9);
        this.tail2.setRotationPoint(0.0F, 0.0F, 4.5F);
        this.tail2.addBox(-1.0F, -1.0F, -0.2F, 2, 2, 5, 0.0F);
        this.setRotateAngle(tail2, 0.278554548618295F, 0.0F, 0.0F);
        this.finTop1 = new ModelRenderer(this, 16, 10);
        this.finTop1.setRotationPoint(0.0F, 6.5F, -0.1F);
        this.finTop1.addBox(-0.5F, 0.0F, -2.9F, 1, 2, 3, 0.0F);
        this.setRotateAngle(finTop1, -0.091106186954104F, 0.0F, 0.0F);
        this.finTop3 = new ModelRenderer(this, 16, 1);
        this.finTop3.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.finTop3.addBox(0.0F, 0.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(finTop3, -0.136659280431156F, 0.0F, 0.0F);
        this.finBase = new ModelRenderer(this, 16, 21);
        this.finBase.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.finBase.addBox(-0.5F, -0.4F, -4.0F, 1, 7, 4, 0.0F);
        this.setRotateAngle(finBase, 2.5953045977155678F, -0.0F, 0.0F);
        this.finBot2 = new ModelRenderer(this, 26, 21);
        this.finBot2.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.finBot2.addBox(0.0F, 0.0F, -3.0F, 1, 3, 3, 0.0F);
        this.setRotateAngle(finBot2, 0.136659280431156F, -0.0F, 0.0F);
/*        this.fin = new ModelRenderer(this, 58, 0);
        this.fin.setRotationPoint(0.0F, -0.5F, 1.5F);
        this.fin.addBox(0.0F, -3.0F, 0.0F, 0, 2, 3, 0.0F);
        this.setRotateAngle(fin, -0.11728612573401893F, -0.0F, 0.0F);*/
        this.tail3 = new ModelRenderer(this, 0, 3);
        this.tail3.setRotationPoint(0.0F, 0.0F, 4.4F);
        this.tail3.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(tail3, 0.22759093446006054F, 0.0F, 0.0F);
        this.finTop2 = new ModelRenderer(this, 16, 4);
        this.finTop2.setRotationPoint(-0.5F, 2.0F, 0.1F);
        this.finTop2.addBox(0.0F, 0.0F, -2.0F, 1, 4, 2, 0.0F);
        this.setRotateAngle(finTop2, -0.136659280431156F, 0.0F, 0.0F);
        this.fubBot3 = new ModelRenderer(this, 26, 17);
        this.fubBot3.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.fubBot3.addBox(0.0F, 0.0F, -2.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(fubBot3, 0.1980948701013564F, -0.0F, 0.0F);
        this.finBase.addChild(this.finBot1);
        this.tailBase.addChild(this.tail1);
        this.tail1.addChild(this.tail2);
        this.finBase.addChild(this.finTop1);
        this.finTop2.addChild(this.finTop3);
        this.tail3.addChild(this.finBase);
        this.finBot1.addChild(this.finBot2);
        //this.tail1.addChild(this.fin);
        this.tail2.addChild(this.tail3);
        this.finTop1.addChild(this.finTop2);
        this.finBot2.addChild(this.fubBot3);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void render(EntityLivingBase entity, int subtype, float partialTicks) {
        double xAngleOffset = 0;
        double yAngleMultiplier = 1; //Used to suppress sway when running
        if (!entity.isRiding()) {
            if (entity instanceof EntityPlayer) {
                double[] angles = getMotionAngles((EntityPlayer) entity, partialTicks);

                xAngleOffset = MathHelper.clamp_double(angles[0] / 5F, -1D, 0.45D);
                yAngleMultiplier = (1 - (xAngleOffset * 2F)); //Used to suppress sway when running
            }
        }
        //Mounted
        else {
            xAngleOffset = Math.toRadians(12F);
            yAngleMultiplier = 0.25F;
        }

        float timestep = getAnimationTime(3000D, entity);
        setRotationRadians(tailBase, -0.6522295414702809F + xAngleOffset * 4F, ((float) Math.cos(timestep - 1) / 5F) * yAngleMultiplier, 0F);
        setRotationRadians(tail1, 0.0013962634015954637F + xAngleOffset * 1F, ((float) Math.cos(timestep - 2) / 5F) * yAngleMultiplier, 0F);
        setRotationRadians(tail2, 0.278554548618295F - xAngleOffset * 2F, ((float) Math.cos(timestep - 3) / 5F) * yAngleMultiplier, 0F);
        setRotationRadians(tail3, 0.22759093446006054F - xAngleOffset, ((float) Math.cos(timestep - 4) / 5F) * yAngleMultiplier, 0F);
        setRotationRadians(finBase, 2.5953045977155678F, ((float) Math.cos(timestep - 10) / 5F) * yAngleMultiplier, 0F);

        tailBase.render(0.0625F);
    }
}
