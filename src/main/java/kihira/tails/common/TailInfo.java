package kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.UUID;

public class TailInfo implements Cloneable {

	@Expose public final UUID uuid;
    @Expose public final boolean hastail;
    @Expose public final int typeid;
    @Expose public final int subid;
    @Expose public final int[] tints;
    @Expose public final int textureID = 0;
	private ResourceLocation texture;
    public boolean needsTextureCompile = true;
	
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
                Minecraft.getMinecraft().renderEngine.deleteTexture(this.texture);
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

    public TailInfo deepCopy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), TailInfo.class);
    }
}
