/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.texture;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * A tinted texture that has 3 different tints, each tint defined in a different RGB channel
 */
public class TripleTintTexture extends AbstractTexture {

	private final String namespace;
	private final String texturename;
	private final int tint1;
	private final int tint2;
	private final int tint3;
	
	private static final int MINBRIGHTNESS = 22;
	
	public TripleTintTexture(String namespace, String texturename, int tint1, int tint2, int tint3) {
		this.namespace = namespace;
		this.texturename = texturename; 
		this.tint1 = tint1;
		this.tint2 = tint2;
		this.tint3 = tint3;
	}
	
	@Override
	public void loadTexture(IResourceManager p_110551_1_) throws IOException {
		this.deleteGlTexture();
        BufferedImage texture;

        try
        {
            if (texturename != null)
            {
                InputStream inputstream = p_110551_1_.getResource(new ResourceLocation(namespace, texturename)).getInputStream();
                texture = ImageIO.read(inputstream);

                int w = texture.getWidth();
                int h = texture.getHeight();
                int length = w*h;
                int[] pixeldata = new int[w*h];
                
                texture.getRGB(0, 0, w, h, pixeldata, 0, w);
                
                int c,r,g,b,a;
                
                for (int i=0; i<length; i++) {
                	c = pixeldata[i];
                	a = alpha(c);
                	r = red(c);
                	g = green(c);
                	b = blue(c);
                	
                	pixeldata[i] = colourise(r, this.tint1, g, this.tint2, b, this.tint3, a);
                }
                
                texture.setRGB(0, 0, w, h, pixeldata, 0, w);
                TextureUtil.uploadTextureImage(this.getGlTextureId(), texture);
            }
        }
        catch (IOException ioexception)
        {
        	LogManager.getLogger().error("Couldn\'t load tripe tint texture image", ioexception);
		}
	}

    /**
     * Colourises a pixel that has the color model TYPE_INT_ARGB
     * @param tone
     * @param c1
     * @param weight1
     * @param c2
     * @param weight2
     * @param c3
     * @param a Alpha
     * @return The colorised pixel
     */
	private int colourise(int tone, int c1, int weight1, int c2, int weight2, int c3, int a) {
		double w2 = weight1/255.0;
		double w3 = weight2/255.0;
		
		w2 *= (1.0 - (w3));
		
		double w1 = 1.0 - (w2+w3);
		
		double r1 = scale(red(c1), MINBRIGHTNESS) / 255.0;
		double g1 = scale(green(c1), MINBRIGHTNESS) / 255.0;
		double b1 = scale(blue(c1), MINBRIGHTNESS) / 255.0;
		
		double r2 = scale(red(c2), MINBRIGHTNESS) / 255.0;
		double g2 = scale(green(c2), MINBRIGHTNESS) / 255.0;
		double b2 = scale(blue(c2), MINBRIGHTNESS) / 255.0;
		
		double r3 = scale(red(c3), MINBRIGHTNESS) / 255.0;
		double g3 = scale(green(c3), MINBRIGHTNESS) / 255.0;
		double b3 = scale(blue(c3), MINBRIGHTNESS) / 255.0;
		
		int rfinal = (int)Math.floor(tone * (r1*w1 + r2*w2 + r3*w3));
		int gfinal = (int)Math.floor(tone * (g1*w1 + g2*w2 + g3*w3));
		int bfinal = (int)Math.floor(tone * (b1*w1 + b2*w2 + b3*w3));
		
		//System.out.println(rfinal+", "+gfinal+", "+bfinal);
		
		return compose(rfinal, gfinal, bfinal, a);
	}

    /**
     * Composes the provided values into an int that in the the TYPE_INT_ARGB colour model
     * @param r The red value
     * @param g The green value
     * @param b The blue value
     * @param a The alpha value
     * @return The TYPE_INT_ARGB colour
     */
	private int compose(int r, int g, int b, int a) {
		int rgb = a;
		rgb = (rgb << 8) + r;
		rgb = (rgb << 8) + g;
		rgb = (rgb << 8) + b;
		return rgb;
	}

    /**
     * Gets the alpha value from a value that has the colour model TYPE_INT_ARGB
     * @param c The colour value
     * @return The alpha value
     */
	private int alpha(int c) {
		return (c >> 24) & 0xFF;
	}

    /**
     * Gets the red value from a value that has the colour model TYPE_INT_ARGB
     * @param c The colour value
     * @return The red value
     */
	private int red(int c) {
		return (c >> 16) & 0xFF;
	}

    /**
     * Gets the green value from a value that has the colour model TYPE_INT_ARGB
     * @param c The colour value
     * @return The green value
     */
	private int green(int c) {
		return (c >> 8) & 0xFF;
	}

    /**
     * Gets the blue value from a value that has the colour model TYPE_INT_ARGB
     * @param c The colour value
     * @return The blue value
     */
	private int blue(int c) {
		return (c) & 0xFF;
	}
	
	private int scale(int c, int min) {
		return min + (int)Math.floor(c * ((255-min)/255.0));
	}
}
