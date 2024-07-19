package uk.kihira.tails.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4fStack;
import uk.kihira.gltf.GltfModel;

import java.util.function.Function;

public class GltfPartModel extends PartModel
{
    private final GltfModel model;

    public GltfPartModel(GltfModel model)
    {
        super(RenderType::entityCutoutNoCull);
        this.model = model;
    }

    @Override
    public void setupAnim(Entity entity, float pLimbSwing, float pLimbSwingAmount, float partialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {

    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha)
    {
        // todo need to actually do this properly
        var stack = new Matrix4fStack();
        stack.pushMatrix().set(pPoseStack.last().pose());
        this.model.render(stack);
    }
}
