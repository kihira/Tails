package kihira.tails.common;

import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class TailInfo {

	public final UUID id;
	public final boolean hastail;
	public final int typeid;
	public final int subid;
    public final int[] tints;
    public final int textureID = 0;
	public final ResourceLocation texture;
	
	public TailInfo(UUID uuid, boolean hastail, int type, int subtype, int tint1, int tint2, int tint3, ResourceLocation texture) {
		this.id = uuid;
		this.hastail = hastail;
		this.typeid = type;
		this.subid = subtype;
        this.tints = new int[] {tint1, tint2, tint3};
        this.texture = texture;
	}
}
