package uk.kihira.tails.client.model.tail;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import uk.kihira.tails.client.model.ModelPartBase;

public class ModelBunnyTail extends ModelPartBase {
    private final ModelRenderer tailBase;

    public ModelBunnyTail() {
        this.tailBase = new ModelRenderer(this);

        this.tailBase.addBox(0.0F, 0.0F, 0.0F, 4, 3, 3, 0.0F);
        this.tailBase.setRotationPoint(-2.0F, -1.5F, 0.0F);
    }

    @Override
    public void render(EntityLivingBase theEntity, float partialTicks) {
        float timestep = getAnimationTime(4000F, theEntity);

        this.setRotationAngles(0, timestep, 1F, 1F, 0, 0, partialTicks, theEntity);

        this.tailBase.render(0.0625F);
    }

    private void setRotationAngles(int subtype, float timestep, float yOffset, float xOffset, float xAngle, float yAngle, float partialTicks, Entity entity) {
        this.setRotationDegrees(this.tailBase, xAngle, yAngle, 0F);
    }

}
