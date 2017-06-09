package uk.kihira.tails.client;

import net.minecraft.util.ResourceLocation;

import java.util.UUID;

/**
 * Represents a part that is in an outfit
 */
public class OutfitPart {
    public final UUID basePart;
    public MountPoint mountPoint;
    public float[] mountOffset;
    public float[] rotation;
    public float[] scale;
    public int[] tints;

    // Client only fields
    public transient ResourceLocation compiledTexture;

    public OutfitPart(UUID basePart) {
        this.basePart = basePart;
    }
}
