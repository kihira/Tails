package kihira.tails.client.render;

import kihira.tails.client.model.ModelBirdTail;
import kihira.tails.common.TailInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public class RenderBirdTail extends RenderTail {

    private String[] skinNames = { "birdTail" };

    private ModelBirdTail modelBirdTail = new ModelBirdTail();

    public RenderBirdTail() {
        super("bird");
    }

    @Override
    public void doRender(EntityLivingBase entity, TailInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        this.modelBirdTail.render(entity, info.subid, partialTicks);
    }

    @Override
    public String[] getTextureNames(int subid) {
        return skinNames;
    }

    @Override
    public int getAvailableSubTypes() {
        return 0;
    }
}
