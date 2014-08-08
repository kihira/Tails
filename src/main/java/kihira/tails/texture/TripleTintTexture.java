package kihira.tails.texture;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;

import scala.Console;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class TripleTintTexture extends AbstractTexture {

	private String namespace;
	private String texturename;
	private int tint1;
	private int tint2;
	private int tint3;
	
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
        BufferedImage texture = null;

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
                	
                	pixeldata[i] = colourise(r, g, this.tint1, b, this.tint2, a, this.tint3);
                	
                	if (i%200==0) {
                		System.out.println("pixel "+i+":  c: "+Integer.toHexString((int)c)+", a: "+a+", r: "+r+", g: "+g+", b: "+b+", end: "+Integer.toHexString(pixeldata[i]));
                	}
                }
                
                texture.setRGB(0, 0, w, h, pixeldata, 0, w);
            }
        }
        catch (IOException ioexception)
        {
            //logger.error("Couldn\'t load layered image", ioexception);
        	LogManager.getLogger().error("Couldn\'t load tripe tint texture image", ioexception);
            return;
        }

        TextureUtil.uploadTextureImage(this.getGlTextureId(), texture);
	}

	private int colourise(int tone, int weight1, int c1, int weight2, int c2, int weight3, int c3) {
		double w1 = weight1/255.0;
		double w2 = weight2/255.0;
		double w3 = weight3/255.0;
		
		w2 *= (1.0 - w3);
		w1 *= (1.0 - (w3+w2));
		
		double wr = 1.0 - (w1+w2+w3);
		
		//System.out.println(w1+", "+w2+", "+w3+", "+wr);
		
		double r1 = scale(red(c1), MINBRIGHTNESS) / 255.0;
		double g1 = scale(green(c1), MINBRIGHTNESS) / 255.0;
		double b1 = scale(blue(c1), MINBRIGHTNESS) / 255.0;
		
		double r2 = scale(red(c2), MINBRIGHTNESS) / 255.0;
		double g2 = scale(green(c2), MINBRIGHTNESS) / 255.0;
		double b2 = scale(blue(c2), MINBRIGHTNESS) / 255.0;
		
		double r3 = scale(red(c3), MINBRIGHTNESS) / 255.0;
		double g3 = scale(green(c3), MINBRIGHTNESS) / 255.0;
		double b3 = scale(blue(c3), MINBRIGHTNESS) / 255.0;
		
		int rfinal = (int)Math.floor(tone * (r1*w1 + r2*w2 + r3*w3 + wr));
		int gfinal = (int)Math.floor(tone * (g1*w1 + g2*w2 + g3*w3 + wr));
		int bfinal = (int)Math.floor(tone * (b1*w1 + b2*w2 + b3*w3 + wr));
		
		//System.out.println(rfinal+", "+gfinal+", "+bfinal);
		
		return (int)compose(rfinal, gfinal, bfinal, 255);
	}
	
	private int compose(int r, int g, int b, int a) {
		int rgb = a;
		rgb = (rgb << 8) + r;
		rgb = (rgb << 8) + g;
		rgb = (rgb << 8) + b;
		return rgb;
	}
	
	private int alpha(int c) {
		return (c >> 24) & 0xFF;
	}
	
	private int red(int c) {
		return (c >> 16) & 0xFF;
	}
	
	private int green(int c) {
		return (c >> 8) & 0xFF;
	}
	
	private int blue(int c) {
		return (c) & 0xFF;
	}
	
	private int scale(int c, int min) {
		return min + (int)Math.floor(c * ((255-min)/255.0));
	}
}
