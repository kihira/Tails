package uk.kihira.tails.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4fStack;
import uk.kihira.gltf.GltfModel;

public class GltfPartModel extends PartModel
{
    private final GltfModel model;

    public GltfPartModel(GltfModel model)
    {
        super(RenderTypes::entityCutoutNoCull);
        this.model = model;
    }

    @Override
    public void setupAnim(Entity entity, float pLimbSwing, float pLimbSwingAmount, float partialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {

    }

    // todo can no longer override this, will likely need to override ModelPart render method instead
/*    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha)
    {
        // todo need to actually do this properly
        var stack = new Matrix4fStack();
        stack.pushMatrix().set(pPoseStack.last().pose());
        this.model.render(stack);
    }*/
}
