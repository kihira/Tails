package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.IDisposable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

/**
 * Represents a part that is in an outfit
 */
@ParametersAreNonnullByDefault
public class OutfitPart implements IDisposable {
    public final UUID basePart;
    public MountPoint mountPoint;
    public float[] mountOffset;
    public float[] rotation;
    public float[] scale;
    public int[] tint;
    public UUID texture;

    // Client only fields
    private transient ResourceLocation compiledTexture;
    private transient Part part;

    public OutfitPart(UUID basePart) {
            this.basePart = basePart;
        // todo copy base part data
    }

    public OutfitPart(Part part) {
        this.basePart = part.id;
        this.mountPoint = part.mountPoint;
        this.mountOffset = part.mountOffset;
        this.rotation = part.rotation;
        this.scale = part.scale;
        this.tint = part.tint;
        this.texture = part.textures[0];

        setCompiledTexture(TextureHelper.generateTexture(this));
    }

    @Nonnull
    public ResourceLocation getCompiledTexture() {
        return compiledTexture;
    }

    public void setCompiledTexture(ResourceLocation texture) {
        dispose();
        compiledTexture = texture;
    }

    /**
     * Gets the {@link Part} that has the ID for {@link #basePart}
     * @return The part
     */
    @Nullable
    public Part getPart() {
        if (part == null) {
            part = PartRegistry.getPart(basePart);
        }
        return part;
    }

    public void dispose() {
        if (compiledTexture != null) {
            Minecraft.getMinecraft().renderEngine.deleteTexture(compiledTexture);
        }
    }
}
