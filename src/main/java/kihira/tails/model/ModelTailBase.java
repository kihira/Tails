package kihira.tails.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * A base class that all tails extend
 */
public abstract class ModelTailBase extends ModelBase {

    /**
     * Returns a list of parts that can be enabled or disabled by the owner of the tail
     * @return List of parts
     */
    public abstract List<String> getToggleableParts();

    /**
     * Renders the tail with the optional parts list provided
     * @param thePlayer The owner of the tail
     * @param partsEnabled The parts enabled
     */
    public abstract void renderWithParts(EntityPlayer thePlayer, List<String> partsEnabled);

    /**
     * Sets the rotation on a model where the provided params are in radians
     * @param model The model
     * @param x The x angle
     * @param y The y angle
     * @param z The z angle
     */
    protected void setRotationRadians(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
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

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        if (entity instanceof EntityPlayer) this.renderWithParts((EntityPlayer) entity, null);
    }
}
