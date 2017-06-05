package uk.kihira.tails.client;

import java.util.UUID;

/**
 * Represents an immutable part
 */
public final class Part {
    public final String[] subModelNames; // todo still use submodel system?
    public final String[] textureNames;
    public final MountPoint mountPoint;
    public final float[] defaultMountOffset;
    public final float[] defaultRotation;
    public final float[] defaultScale;
    public final int[] defaultTints;

    // Non render details
    public final UUID resourceId; // A UUID for a file that contains the model and texture. Also the UUID for the part
    public final String author;
    public final String name;
    public final String[] tags;
    public final int category;

    public Part(UUID resourceId, String[] subModelNames, String[] textureNames, MountPoint mountPoint, float[] defaultMountOffset, float[] defaultRotation, float[] defaultScale, int[] defaultTints, int category, String author, String name, String[] tags) {
        this.resourceId = resourceId;
        this.subModelNames = subModelNames;
        this.textureNames = textureNames;
        this.mountPoint = mountPoint;
        this.defaultMountOffset = defaultMountOffset;
        this.defaultRotation = defaultRotation;
        this.defaultScale = defaultScale;
        this.defaultTints = defaultTints;
        this.category = category;
        this.author = author;
        this.name = name;
        this.tags = tags;
    }
}
