package uk.kihira.tails.client;

import java.util.UUID;

/**
 * Represents an immutable part
 */
public class Part {
    public final MountPoint mountPoint;
    public final float[] defaultMountOffset;
    public final float[] defaultRotation;
    public final float[] defaultScale;
    public final int[] defaultTints;

    // Non render details
    public final UUID id; // A UUID for a file that contains the model and texture. Also the UUID for the part
    public final String author;
    public final String name;
    //public final String[] tags;
    //public final int category;

    public Part(UUID id, MountPoint mountPoint, float[] defaultMountOffset, float[] defaultRotation, float[] defaultScale, int[] defaultTints, String author, String name) {
        this.id = id;
        this.mountPoint = mountPoint;
        this.defaultMountOffset = defaultMountOffset;
        this.defaultRotation = defaultRotation;
        this.defaultScale = defaultScale;
        this.defaultTints = defaultTints;
        this.author = author;
        this.name = name;
    }
}
