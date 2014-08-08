package kihira.tails.render;

import kihira.tails.model.ModelRacoonTail;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderRacoonTail extends RenderTail {

    private ModelRacoonTail modelRacoonTail = new ModelRacoonTail();
    private ResourceLocation tailTexture = new ResourceLocation("tails", "texture/racoonTail.png");

    @Override
    public void render(EntityPlayer player) {
        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(this.tailTexture);
        if (!player.isSneaking()) GL11.glTranslatef(0F, 0.65F, 0.1F);
        else GL11.glTranslatef(0F, 0.55F, 0.4F);
        GL11.glScalef(0.8F, 0.8F, 0.8F);
        this.modelRacoonTail.render(player, 0, 0, 0, 0, 0, 0.0625F);
        GL11.glPopMatrix();
    }
}
