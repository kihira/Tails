package kihira.tails.client.render;

import kihira.tails.client.model.ModelBirdTail;
import kihira.tails.client.model.ModelRaccoonTail;
import kihira.tails.common.TailInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

public class RenderBirdTail extends RenderTail {

	private String[] skinNames = { "birdTail" };

	private ModelBirdTail modelBirdTail = new ModelBirdTail();

	public RenderBirdTail() {
		super("bird");
	}

	@Override
	public void doRender(EntityLivingBase entity, TailInfo info,
			float partialTicks) {
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
		if (!entity.isSneaking())
			GL11.glTranslatef(0F, 0.65F, 0.1F);
		else
			GL11.glTranslatef(0F, 0.55F, 0.4F);
		GL11.glScalef(0.8F, 0.8F, 0.8F);
		this.modelBirdTail.render(entity, info.subid, partialTicks);
		GL11.glPopMatrix();
	}

	@Override
	public String[] getTextureNames() {
		return skinNames;
	}

	@Override
	public int getAvailableSubTypes() {
		return 0;
	}
}
