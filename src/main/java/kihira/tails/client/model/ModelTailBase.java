package kihira.tails.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;

/**
 * A base class that all tails extend
 */
public abstract class ModelTailBase extends ModelBase {

    /**
     * Renders the tail with the optional parts list provided
     * @param theEntity The owner of the tail
     * @param subtype The subtype
     * @param partialTicks
     */
    public abstract void render(EntityLivingBase theEntity, int subtype, float partialTicks);

    /**
     * Sets the rotation on a model where the provided params are in radians
     * @param model The model
     * @param x The x angle
     * @param y The y angle
     * @param z The z angle
     */
    protected void setRotationRadians(ModelRenderer model, double x, double y, double z) {
        model.rotateAngleX = (float) x;
        model.rotateAngleY = (float) y;
        model.rotateAngleZ = (float) z;
    }

    /**
     * Sets the rotation on a model where the provided params are in degrees
     * @param model The model
     * @param x The x angle
     * @param y The y angle
     * @param z The z angle
     */
    protected void setRotationDegrees(ModelRenderer model, float x, float y, float z) {
        this.setRotationRadians(model, (float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
    }
}
