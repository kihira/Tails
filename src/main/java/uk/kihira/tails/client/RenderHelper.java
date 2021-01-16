/*
 * Some code provided by iChun under LGPLv3
 */

package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class RenderHelper
{
    public static void startGlScissor(int x, int y, int width, int height)
    {
        // TODO
//        Minecraft mc = Minecraft.getInstance();
//        ScaledResolution reso = new ScaledResolution(mc);
//
//        double scaleW = (double)mc.displayWidth / reso.getScaledWidth_double();
//        double scaleH = (double)mc.displayHeight / reso.getScaledHeight_double();
//
//        GL11.glEnable(GL11.GL_SCISSOR_TEST);
//        GL11.glScissor((int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.displayHeight - ((double)(y + height) * scaleH)), (int)Math.floor((double)(x + width) * scaleW) - (int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.displayHeight - ((double)y * scaleH)) - (int)Math.floor((double)mc.displayHeight - ((double)(y + height) * scaleH))); //starts from lower left corner (minecraft starts from upper left)
    }

    public static void endGlScissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
