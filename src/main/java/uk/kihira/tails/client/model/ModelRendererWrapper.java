package uk.kihira.tails.client.model;

import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.render.RenderingHandler;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRendererWrapper extends ModelRenderer {

    private final PartsData.PartType partType;

    public ModelRendererWrapper(ModelBase model, PartsData.PartType partType) {
        super(model);
        this.partType = partType;
        addBox(0, 0, 0, 0, 0, 0); //Adds in a blank box as it's required in certain cases such as rendering arrows in entities
    }

    @Override
    public void render(float scale) {
        if (RenderingHandler.currentEvent != null && RenderingHandler.currentPartsData != null && RenderingHandler.currentPlayerTexture != null) {
            PartInfo info = RenderingHandler.currentPartsData.getPartInfo(partType);
            if (info != null && info.hasPart) {
                PartRegistry.getRenderPart(info.partType, info.typeid).render(RenderingHandler.currentEvent.getEntityPlayer(),
                        info, 0, 0, 0, RenderingHandler.currentEvent.getPartialRenderTick());

                Minecraft.getMinecraft().renderEngine.bindTexture(RenderingHandler.currentPlayerTexture);
            }
        }
    }
}