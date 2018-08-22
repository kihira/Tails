package uk.kihira.tails.client;

import uk.kihira.gltf.Model;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents a Part that has a name, author, model and various details about how it should render
 */
@Nullable
public class Part {
    // Non render details
    public final UUID id; // A UUID for a file that contains the model and texture. Also the UUID for the part
    public final String author;
    public final String name;
    public final String[] tags = new String[]{};
    public final int category = 0;

    public final MountPoint mountPoint;
    public final float[] mountOffset;
    public final float[] rotation;
    public final float[] scale;
    public final float[][] tint;
    public final UUID[] textures;

    private transient Model model;

    public Part(UUID id, String name, String author, MountPoint mountPoint, float[] defaultMountOffset, float[] defaultRotation, float[] defaultScale, float[][] defaultTints, UUID[] textures) {
        this.id = id;
        this.mountPoint = mountPoint;
        this.mountOffset = defaultMountOffset;
        this.rotation = defaultRotation;
        this.scale = defaultScale;
        this.tint = defaultTints;
        this.author = author;
        this.name = name;
        this.textures = textures;
    }

    /**
     * Returns the {@link Model} associated with this part.
     * If the model is not yet loaded, it will begin loading it.
     * Returns null if model is not loaded
     *
     * @return The model if loaded
     */
    @Nullable
    public Model getModel() {
        if (model == null) {
            model = PartRegistry.getModel(id);
        }
        return model;
    }
}
