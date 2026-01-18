package uk.kihira.tails.client;

import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.outfit.Tint;

import java.util.UUID;

/**
 * Represents a Part that has a name, author, model and various details about how it should render
 */
public final class Part
{
    // Non render details
    public final @NonNull UUID id; // A UUID for a file that contains the model and texture. Also the UUID for the part
    public final String author;
    public final @NonNull String name;
    public final String[] tags = new String[]{};
    public final int category = 0;

    public final @NonNull MountPoint mountPoint;
    public final Vector3f mountOffset;
    public final Vector3f rotation;
    public final Vector3f scale;
    public final Tint[] tint;
    public final @NonNull PartTexture[] textures;

    private transient PartModel model;

    public Part(UUID id, String name, String author, MountPoint mountPoint, Vector3f defaultMountOffset, Vector3f defaultRotation, Vector3f defaultScale, Tint[] defaultTints, PartTexture[] textures)
    {
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
     * Returns the {@link PartModel} associated with this part.
     * If the model is not yet loaded, it will begin loading it.
     * Returns null if model is not loaded
     *
     * @return The model if loaded
     */
    @Nullable
    public PartModel getModel()
    {
        if (this.model == null)
        {
            this.model = PartRegistry.getModel(this.id).orElse(null);
        }
        return this.model;
    }
}
