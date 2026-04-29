package com.akashiro.tails.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

public final class TripleTintTexture {

    private TripleTintTexture() {
    }

    public static DynamicTexture create(
            ResourceManager manager,
            ResourceLocation baseLocation,
            int tint1,
            int tint2,
            int tint3
    ) throws IOException {
        try (InputStream stream = manager.open(baseLocation);
             NativeImage original = NativeImage.read(stream)) {
            return new DynamicTexture(applyTint(original, tint1, tint2, tint3));
        }
    }

    private static NativeImage applyTint(NativeImage src, int tint1, int tint2, int tint3) {
        int width = src.getWidth();
        int height = src.getHeight();
        NativeImage dst = new NativeImage(NativeImage.Format.RGBA, width, height, false);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int abgr = src.getPixelRGBA(x, y);
                int a = (abgr >> 24) & 0xFF;
                int b = (abgr >> 16) & 0xFF;
                int g = (abgr >> 8) & 0xFF;
                int r = abgr & 0xFF;

                int tinted = colourise(r, tint1, g, tint2, b, tint3, a);
                dst.setPixelRGBA(x, y, tinted);
            }
        }

        return dst;
    }

    private static int colourise(
            int brightness,
            int tint1,
            int greenWeight,
            int tint2,
            int blueWeight,
            int tint3,
            int alpha
    ) {
        double tint2Mix = (greenWeight / 255.0D) * (1.0D - (blueWeight / 255.0D));
        double tint3Mix = blueWeight / 255.0D;
        double tint1Mix = 1.0D - tint2Mix - tint3Mix;

        double tint1Red = scale(channel(tint1, 16), 22) / 255.0D;
        double tint1Green = scale(channel(tint1, 8), 22) / 255.0D;
        double tint1Blue = scale(channel(tint1, 0), 22) / 255.0D;

        double tint2Red = scale(channel(tint2, 16), 22) / 255.0D;
        double tint2Green = scale(channel(tint2, 8), 22) / 255.0D;
        double tint2Blue = scale(channel(tint2, 0), 22) / 255.0D;

        double tint3Red = scale(channel(tint3, 16), 22) / 255.0D;
        double tint3Green = scale(channel(tint3, 8), 22) / 255.0D;
        double tint3Blue = scale(channel(tint3, 0), 22) / 255.0D;

        int outRed = (int) Math.floor(brightness * (tint1Red * tint1Mix + tint2Red * tint2Mix + tint3Red * tint3Mix));
        int outGreen = (int) Math.floor(brightness * (tint1Green * tint1Mix + tint2Green * tint2Mix + tint3Green * tint3Mix));
        int outBlue = (int) Math.floor(brightness * (tint1Blue * tint1Mix + tint2Blue * tint2Mix + tint3Blue * tint3Mix));

        return compose(outRed, outGreen, outBlue, alpha);
    }

    private static int scale(int value, int minBrightness) {
        return minBrightness + (int) Math.floor(value * (255 - minBrightness) / 255.0D);
    }

    private static int compose(int red, int green, int blue, int alpha) {
        int value = alpha;
        value = (value << 8) + blue;
        value = (value << 8) + green;
        value = (value << 8) + red;
        return value;
    }

    private static int channel(int colour, int shift) {
        return (colour >> shift) & 0xFF;
    }
}
