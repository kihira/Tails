package kihira.tails.client.render;

import kihira.tails.client.model.ModelDragonTail;
import kihira.tails.common.TailInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class RenderDragonTail extends RenderTail {

	private String[] skinNames = {"dragonTail"};
	
    private ModelDragonTail modelDragonTail = new ModelDragonTail();
    //private ResourceLocation dragonTailTexture = new ResourceLocation("tails", "texture/dragonTail.png");

    @Override
    public void render(EntityLivingBase player, TailInfo info) {
        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        if (!player.isSneaking()) GL11.glTranslatef(0F, 0.7F, 0.1F);
        else GL11.glTranslatef(0F, 0.6F, 0.35F);
        GL11.glScalef(0.8F, 0.8F, 0.8F);
        this.modelDragonTail.render(player, info.subid);
        GL11.glPopMatrix();
    }

	@Override
	public String[] getTextureNames() {
		return skinNames;
	}
    
}
