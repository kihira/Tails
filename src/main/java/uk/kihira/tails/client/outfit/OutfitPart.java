package uk.kihira.tails.client.outfit;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.Part;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.PartTexture;
import uk.kihira.tails.Tails;
import uk.kihira.tails.client.texture.ThreeTintTexture;

import javax.annotation.Nullable;

import java.util.UUID;

/**
 * Represents a part that is in an outfit
 */
public class OutfitPart implements AutoCloseable
{
    public final UUID basePart;
    public MountPoint mountPoint;
    public Vector3f mountOffset; // [x,y,z]
    public Vector3f rotation; // [x,y,z]
    public Vector3f scale; // [x,y,z]
    private final Tint[] tint; // [[r,g,b],[r,g,b],[r,g,b]]
    private PartTexture texture;

    // Client only fields
    private transient Part part;
    private transient Identifier textureIdentifier;
    private transient ThreeTintTexture tintedTexture;

    public OutfitPart(Part part)
    {
        this.basePart = part.id;
        this.mountPoint = part.mountPoint;
        this.mountOffset = part.mountOffset;
        this.rotation = part.rotation;
        this.scale = part.scale;
        this.tint = part.tint;
        this.texture = part.textures[0];
    }

    public Tint getTint(int index)
    {
        if (this.tint != null && index >= 0 && index < this.tint.length)
        {
            return this.tint[index];
        }
        return null;
    }

    public void setTint(int index, Tint tint)
    {
        if (this.tint != null && index >= 0 && index < this.tint.length)
        {
            this.tint[index] = tint;
            this.tintedTexture.setTints(this.tint[0], this.tint[1], this.tint[2]);
        }
    }

    /**
     * Gets the {@link Part} that has the ID for {@link #basePart}
     * @return The part
     */
    @Nullable
    public Part getPart() 
    {
        if (this.part == null)
        {
            this.part = PartRegistry.getPart(this.basePart).orElse(null);
        }
        return this.part;
    }

    public Identifier getTextureIdentifier()
    {
        // TODO: We're mixing in client usage in what should be a common class.
        // We also don't really want to use Minecraft.getInstance, maybe a builder or factory pattern would be better?
        // We also need to handle about deserialisation as well, hence putting it behind a getter for now.
        if (this.textureIdentifier == null)
        {
            this.textureIdentifier = Identifier.fromNamespaceAndPath(Tails.MOD_ID, String.format("threetint_texture/%s", UUID.randomUUID()));
            var baseTexture = Identifier.fromNamespaceAndPath(Tails.MOD_ID, String.format("texture/parts/%s/%s.png", part.id, this.texture.id()));
            this.tintedTexture = ThreeTintTexture.create(this.textureIdentifier.toString(), baseTexture, this.tint[0], this.tint[1], this.tint[2]);

            Minecraft.getInstance().getTextureManager().register(this.textureIdentifier, this.tintedTexture);
        }

        return this.textureIdentifier;
    }

    @Override
    public void close()
    {
        Minecraft.getInstance().getTextureManager().release(this.textureIdentifier);
        this.tintedTexture.close();
    }
}
