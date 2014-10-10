/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.UUID;

public class PartInfo implements Cloneable {

	@Expose public final UUID uuid;
    @Expose public final boolean hasPart;
    @Expose public final int typeid;
    @Expose public final int subid;
    @Expose public final int[] tints;
    @Expose public final int textureID;
	private ResourceLocation texture;
    public boolean needsTextureCompile = true;
	
	public PartInfo(UUID uuid, boolean hasPart, int type, int subtype, int textureID, int[] tints, ResourceLocation texture) {
		this.uuid = uuid;
		this.hasPart = hasPart;
		this.typeid = type;
		this.subid = subtype;
        this.textureID = textureID;
        this.tints = tints;
        this.texture = texture;
	}

    public PartInfo(UUID uuid, boolean hasPart, int type, int subtype, int textureID, int tint1, int tint2, int tint3, ResourceLocation texture) {
        this(uuid, hasPart, type, subtype, textureID, new int[] {tint1, tint2, tint3}, texture);
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
        return "PartInfo{" +
                "uuid=" + uuid +
                ", hasPart=" + hasPart +
                ", typeid=" + typeid +
                ", subid=" + subid +
                ", tints=" + Arrays.toString(tints) +
                ", textureID=" + textureID +
                ", texture=" + texture +
                ", needsTextureCompile=" + needsTextureCompile +
                '}';
    }

    public PartInfo deepCopy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), PartInfo.class);
    }
}
