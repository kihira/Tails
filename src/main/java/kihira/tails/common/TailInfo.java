package kihira.tails.common;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.UUID;

public class TailInfo {

	public final UUID uuid;
	public final boolean hastail;
	public final int typeid;
	public final int subid;
    public final int[] tints;
    public final int textureID = 0;
	private ResourceLocation texture;
    public boolean needsTextureCompile;
	
	public TailInfo(UUID uuid, boolean hastail, int type, int subtype, int[] tints, ResourceLocation texture) {
		this.uuid = uuid;
		this.hastail = hastail;
		this.typeid = type;
		this.subid = subtype;
        this.tints = tints;
        this.texture = texture;
	}

    public TailInfo(UUID uuid, boolean hastail, int type, int subtype, int tint1, int tint2, int tint3, ResourceLocation texture) {
        this(uuid, hastail, type, subtype, new int[] {tint1, tint2, tint3}, texture);
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public void setTexture(ResourceLocation texture) {
        if (this.texture == null || !this.texture.equals(texture)) {
            try {
                Minecraft.getMinecraft().renderEngine.deleteTexture(this.texture); //TODO this won't work on servers
            } catch (Exception ignored) {}

            this.needsTextureCompile = true;
        }
        this.texture = texture;
    }

    @Override
    public String toString() {
        return "TailInfo{" +
                "uuid=" + uuid +
                ", hastail=" + hastail +
                ", typeid=" + typeid +
                ", subid=" + subid +
                ", tints=" + Arrays.toString(tints) +
                ", textureID=" + textureID +
                ", texture=" + texture +
                ", needsTextureCompile=" + needsTextureCompile +
                '}';
    }
}
