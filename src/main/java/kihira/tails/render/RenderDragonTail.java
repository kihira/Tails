package kihira.tails.render;

import kihira.tails.model.ModelDragonTail;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderDragonTail extends RenderTail {

    private ModelDragonTail modelDragonTail = new ModelDragonTail();
    private ResourceLocation dragonTailTexture = new ResourceLocation("tails", "texture/dragonTail.png");

    @Override
    public void render(EntityPlayer player) {
        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(dragonTailTexture);
        if (!player.isSneaking()) GL11.glTranslatef(0F, 0.7F, 0.1F);
        else GL11.glTranslatef(0F, 0.6F, 0.35F);
        GL11.glScalef(0.8F, 0.8F, 0.8F);
        modelDragonTail.render(player, 0, 0, 0, 0, 0, 0.0625F);
        GL11.glPopMatrix();
    }
}
