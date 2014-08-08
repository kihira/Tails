package kihira.tails.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.util.List;

public class ModelDragonTail extends ModelTailBase {

    private ModelRenderer tailBase;
    private ModelRenderer tail1;
    private ModelRenderer tail2;
    private ModelRenderer tail3;

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
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        float seed = (entity.hashCode() + System.nanoTime() / 100000000F) / 5F;

        this.tailBase.rotateAngleY = MathHelper.cos(seed - 1) / 5F;
        this.tail1.rotateAngleY = MathHelper.cos(seed - 2) / 5F;
        this.tail2.rotateAngleY = (MathHelper.cos(seed - 3) / 5F);
        this.tail3.rotateAngleY = (MathHelper.cos(seed - 4) / 5F);
    }

    @Override
    public List<String> getToggleableParts() {
        return null;
    }

    @Override
    public void renderWithParts(EntityPlayer thePlayer, List<String> partsEnabled) {
        this.setRotationAngles(0, 0, 0, 0, 0, 0, thePlayer);

        this.tailBase.render(0.0625F);
    }
}
