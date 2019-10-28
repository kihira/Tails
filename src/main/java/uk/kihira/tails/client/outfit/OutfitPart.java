package uk.kihira.tails.client.outfit;

import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.Part;
import uk.kihira.tails.client.PartRegistry;

import javax.annotation.Nullable;

import java.util.UUID;

/**
 * Represents a part that is in an outfit
 */
public class OutfitPart {
    public final UUID basePart;
    public MountPoint mountPoint;
    public float[] mountOffset; // [x,y,z]
    public float[] rotation; // [x,y,z]
    public float[] scale; // [x,y,z]
    public float[][] tint; // [[r,g,b],[r,g,b]]
    public UUID texture;

    // Client only fields
    private transient Part part;

    public OutfitPart(Part part) {
        this.basePart = part.id;
        this.mountPoint = part.mountPoint;
        this.mountOffset = part.mountOffset;
        this.rotation = part.rotation;
        this.scale = part.scale;
        this.tint = part.tint;
        this.texture = part.textures[0];
    }

    /**
     * Gets the {@link Part} that has the ID for {@link #basePart}
     * @return The part
     */
    @Nullable
    public Part getPart() 
    {
        if (part == null) 
        {
            part = PartRegistry.getPart(basePart).orElse(null);
        }
        return part;
    }
}
