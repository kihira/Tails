package uk.kihira.tails.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.apache.logging.log4j.LogManager;
import uk.kihira.tails.client.outfit.Tint;

import java.io.IOException;

/**
 * A tinted texture that has 3 different tints, each tint defined in a different RGB channel
 */
public class ThreeTintTexture extends DynamicTexture
{
    private final Identifier baseTexture;
    private Tint tint1;
    private Tint tint2;
    private Tint tint3;

    private static final int MINBRIGHTNESS = 22;

    private ThreeTintTexture(String label, Identifier baseTexture, NativeImage baseTextureImage, Tint tint1, Tint tint2, Tint tint3)
    {
        super(() -> label, baseTextureImage.getWidth(), baseTextureImage.getHeight(), false);
        this.baseTexture = baseTexture;
        this.tint1 = tint1;
        this.tint2 = tint2;
        this.tint3 = tint3;

        this.upload();
    }

    public static ThreeTintTexture create(String label, Identifier baseTexture, Tint tint1, Tint tint2, Tint tint3)
    {
        var baseTextureImage = loadBaseTexture(Minecraft.getInstance().getResourceManager(), baseTexture);
        return new ThreeTintTexture(label, baseTexture, baseTextureImage, tint1, tint2, tint3);
    }

    public void setTints(Tint tint1, Tint tint2, Tint tint3)
    {
        this.tint1 = tint1;
        this.tint2 = tint2;
        this.tint3 = tint3;

        this.upload();
    }

    @Override
    public void upload()
    {
        var resourceManager = Minecraft.getInstance().getResourceManager();
        var textureResource = resourceManager.getResource(baseTexture);

        if (textureResource.isEmpty())
        {
            return;
        }

        try (var textureStream = textureResource.get().open())
        {
            var image = NativeImage.read(textureStream); // Don't capture this stream as we'll set it in setPixels later and it'll be owned by this instance
            int c, r, g, b, a;

            for (int x = 0; x < image.getWidth(); x++)
            {
                for (int y = 0; y < image.getHeight(); y++)
                {
                    c = image.getPixel(x, y);
                    a = ARGB.alpha(c);
                    r = ARGB.red(c);
                    g = ARGB.green(c);
                    b = ARGB.blue(c);

                    image.setPixel(x, y, colourise(r, this.tint1, g, this.tint2, b, this.tint3, a));
                }
            }

            this.setPixels(image);
        }
        catch (IOException ioexception)
        {
            LogManager.getLogger().error("Couldn't load tripe tint texture image", ioexception);
        }

        super.upload();
    }

    private static NativeImage loadBaseTexture(ResourceManager resourceManager, Identifier baseTexture)
    {
        var textureResource = resourceManager.getResource(baseTexture);

        if (textureResource.isEmpty())
        {
            return null;
        }

        try (var textureStream = textureResource.get().open())
        {
            return NativeImage.read(textureStream); // Don't capture this stream as we'll set it in setPixels later and it'll be owned by this instance
        }
        catch (IOException ioexception)
        {
            LogManager.getLogger().error("Couldn't load tripe tint texture image", ioexception);
            return null;
        }
    }

    /**
     * Colourises a pixel that has the color model TYPE_INT_ARGB
     *
     * @param tone
     * @param c1
     * @param weight1
     * @param c2
     * @param weight2
     * @param c3
     * @param a       Alpha
     * @return The colorised pixel
     */
    private int colourise(int tone, Tint c1, int weight1, Tint c2, int weight2, Tint c3, int a)
    {
        double w2 = weight1 / 255.0;
        double w3 = weight2 / 255.0;

        w2 *= (1.0 - (w3));

        double w1 = 1.0 - (w2 + w3);

        double r1 = scale(c1.redInt(), MINBRIGHTNESS) / 255.0;
        double g1 = scale(c1.greenInt(), MINBRIGHTNESS) / 255.0;
        double b1 = scale(c1.blueInt(), MINBRIGHTNESS) / 255.0;

        double r2 = scale(c2.redInt(), MINBRIGHTNESS) / 255.0;
        double g2 = scale(c2.greenInt(), MINBRIGHTNESS) / 255.0;
        double b2 = scale(c2.blueInt(), MINBRIGHTNESS) / 255.0;

        double r3 = scale(c3.redInt(), MINBRIGHTNESS) / 255.0;
        double g3 = scale(c3.greenInt(), MINBRIGHTNESS) / 255.0;
        double b3 = scale(c3.blueInt(), MINBRIGHTNESS) / 255.0;

        int rfinal = (int) Math.floor(tone * (r1 * w1 + r2 * w2 + r3 * w3));
        int gfinal = (int) Math.floor(tone * (g1 * w1 + g2 * w2 + g3 * w3));
        int bfinal = (int) Math.floor(tone * (b1 * w1 + b2 * w2 + b3 * w3));

        //System.out.println(rfinal+", "+gfinal+", "+bfinal);

        return compose(rfinal, gfinal, bfinal, a);
    }

    /**
     * Composes the provided values into an int that in the the TYPE_INT_ARGB colour model
     *
     * @param r The red value
     * @param g The green value
     * @param b The blue value
     * @param a The alpha value
     * @return The TYPE_INT_ARGB colour
     */
    private int compose(int r, int g, int b, int a)
    {
        int rgb = a;
        rgb = (rgb << 8) + r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;
        return rgb;
    }

    private int scale(int c, int min)
    {
        return min + (int) Math.floor(c * ((255 - min) / 255.0));
    }
}