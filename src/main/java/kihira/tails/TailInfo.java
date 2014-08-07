package kihira.tails;

import java.util.UUID;

import net.minecraft.util.ResourceLocation;

public class TailInfo {

	public final UUID id;
	public final boolean hastail;
	public final int typeid;
	public final int subid;
	public final ResourceLocation texture;
	
	public TailInfo(UUID uuid, boolean hastail, int type, int subtype, ResourceLocation texture) {
		this.id = uuid;
		this.hastail = hastail;
		this.typeid = type;
		this.subid = subtype;
		this.texture = texture;
	}
}
