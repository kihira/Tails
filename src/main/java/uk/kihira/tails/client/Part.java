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

    public Part(UUID id, String name, String author, MountPoint mountPoint, int[] defaultTints, float[] defaultMountOffset, float[] defaultRotation, float[] defaultScale) {
        this.id = id;
        this.mountPoint = mountPoint;
        this.defaultMountOffset = defaultMountOffset;
        this.defaultRotation = defaultRotation;
        this.defaultScale = defaultScale;
        this.defaultTints = defaultTints;
        this.author = author;
        this.name = name;
    }

    public Part(UUID id, String name, String author, MountPoint mountPoint, int[] defaultTints, float[] defaultMountOffset, float[] defaultRotation) {
        this(id, name, author, mountPoint, defaultTints, defaultMountOffset, defaultRotation, new float[]{1.f, 1.f, 1.f});
    }

    public Part(UUID id, String name, String author, MountPoint mountPoint, int[] defaultTints, float[] defaultMountOffset) {
        this(id, name, author, mountPoint, defaultTints, defaultMountOffset, new float[]{0.f, 0.f, 0.f}, new float[]{1.f, 1.f, 1.f});
    }

    public Part(UUID id, String name, String author, MountPoint mountPoint, int[] defaultTints) {
        this(id, name, author, mountPoint, defaultTints, new float[]{0.f, 0.f, 0.f}, new float[]{0.f, 0.f, 0.f}, new float[]{1.f, 1.f, 1.f});
    }
}
