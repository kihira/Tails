package kihira.tails.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.TailInfo;
import kihira.tails.model.ModelFoxTail;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderFoxTail extends RenderTail {

	private String[] skinNames = {"foxTail"};
	
    private ModelFoxTail modelFoxTail = new ModelFoxTail();
    //private ResourceLocation tailTexture = new ResourceLocation("tails", "texture/foxTail.png");

    @Override
    public void render(EntityPlayer player, TailInfo info) {
        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(info.texture);
        if (!player.isSneaking()) GL11.glTranslatef(0F, 0.65F, 0.1F);
        else GL11.glTranslatef(0F, 0.55F, 0.4F);
        GL11.glScalef(0.8F, 0.8F, 0.8F);
        this.modelFoxTail.render(player, 0, 0, 0, 0, 0, 0.0625F);
        GL11.glPopMatrix();
    }
    
    @Override
	public String[] getTextureNames() {
		return skinNames;
	}
}
