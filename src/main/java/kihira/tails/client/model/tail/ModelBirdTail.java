package kihira.tails.client.model.tail;

import kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class ModelBirdTail extends ModelPartBase {
    final ModelRenderer center;
    final ModelRenderer left0;
    final ModelRenderer left1;
    final ModelRenderer left2;
    final ModelRenderer right0;
    final ModelRenderer right2;
    final ModelRenderer right1;

    public ModelBirdTail() {
        center = new ModelRenderer(this, 0, 0);
        center.addBox(-1.5F, -.5F, -0.5F, 3, 9, 1);
        center.setRotationPoint(0F, 0F, 1F);
        center.setTextureSize(64, 32);
        setRotationDegrees(center, 55F, 0F, 0F);

        left0 = new ModelRenderer(this, 0, 10);
        left0.addBox(-1F, 0F, -0.5F, 2, 8, 1);
        left0.setRotationPoint(-1F, .5F, 0F);
        left0.setTextureSize(64, 32);
        setRotationDegrees(left0, -2, -8, 11);

        left1 = new ModelRenderer(this, 0, 19);
        left1.addBox(-1F, 0F, -0.5F, 2, 7, 1);
        left1.setRotationPoint(-1.5F, 0F, 0F);
        left1.setTextureSize(64, 32);
        setRotationDegrees(left1, 0, -6, 0);

        left2 = new ModelRenderer(this, 6, 19);
        left2.addBox(-0.5F, 0F, -0.5F, 1, 6, 1);
        left2.setRotationPoint(-.5F, 0F, 0F);
        left2.setTextureSize(64, 32);
        setRotationDegrees(left2, 0, -6, 15);

        right0 = new ModelRenderer(this, 0, 10);
        right0.mirror = true;
        right0.addBox(-1F, 0F, -0.5F, 2, 8, 1);
        right0.setRotationPoint(1F, .5F, 0F);
        right0.setTextureSize(64, 32);
        setRotationDegrees(right0, -2, 8, -11);

        right1 = new ModelRenderer(this, 0, 19);
        right1.addBox(-1F, 0F, -0.5F, 2, 7, 1);
        right1.setRotationPoint(1.5F, 0F, 0F);
        right1.setTextureSize(64, 32);
        setRotationDegrees(right1, 0, 6, 0);

        right2 = new ModelRenderer(this, 6, 19);
        right2.addBox(-0.5F, 0F, -0.5F, 1, 6, 1);
        right2.setRotationPoint(.5F, 0, 0F);
        right2.setTextureSize(64, 32);
        setRotationDegrees(right2, 0, 6, -15);

        right1.addChild(right2);
        right0.addChild(right1);
        left1.addChild(left2);
        left0.addChild(left1);
        center.addChild(left0);
        center.addChild(right0);
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float partialTicks, Entity entity) {
        float timestep = getAnimationTime(8000, entity);
        double xAngleOffset = 0;
        double zAngleOffset = 0;

        if (!entity.isRiding()) {
            if (entity instanceof EntityPlayer) {
                double[] angles = getMotionAngles((EntityPlayer) entity, partialTicks);
                xAngleOffset = angles[0];
                zAngleOffset = angles[2];

                xAngleOffset -= Math.cos(timestep - 1) / 15F;
                zAngleOffset -= Math.cos(timestep - 1) / 25F;
                xAngleOffset = MathHelper.clamp_double(xAngleOffset * 0.6D, -1D, 0.45D);
                zAngleOffset = MathHelper.clamp_double(zAngleOffset * 0.5D, -0.5D, 0.5D);
            }
        }
        //Mounted
        else {
            xAngleOffset = Math.toRadians(60F);
        }

        setRotationRadians(center, Math.toRadians(50) + xAngleOffset, -zAngleOffset, 0);
        setRotationRadians(left0, Math.toRadians(-2F), Math.toRadians(-5), Math.toRadians(11) + xAngleOffset / 10F);
        setRotationRadians(left1, Math.toRadians(-2F), Math.toRadians(-7), xAngleOffset / 10F);
        setRotationRadians(left2, Math.toRadians(-2F), Math.toRadians(-10), Math.toRadians(10) + xAngleOffset / 10F);
        setRotationRadians(right0, Math.toRadians(-2F), Math.toRadians(5), Math.toRadians(-11) - xAngleOffset / 10F);
        setRotationRadians(right1, Math.toRadians(-2F), Math.toRadians(7), -xAngleOffset / 10F);
        setRotationRadians(right2, Math.toRadians(-2F), Math.toRadians(10), Math.toRadians(-10) - xAngleOffset / 10F);
    }

    @Override
    public void render(EntityLivingBase theEntity, int subtype, float partialTicks) {
        this.setRotationAngles(0, 0, 0, 0, 0, partialTicks, theEntity);
        this.center.render(.0625f);
    }
}