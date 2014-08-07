package kihira.tails.texture;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import kihira.tails.TailInfo;

public class TextureHelper {

	public static Hashtable<UUID, TailInfo> TailMap = new Hashtable<UUID, TailInfo>();
	
	public static void buildPlayerInfo(EntityPlayer player) {
		GameProfile profile = player.getGameProfile();
		UUID id = profile.getId();
		
		System.out.println("Building TailInfo for "+profile.getName());
		
		TailMap.put(id, new TailInfo(id, false, 0,0, new ResourceLocation("tails", "texture/dragonTail.png")));
	}
	
	/*private static BufferedImage getPlayerSkinAsBufferedImage(String name) {
		ResourceLocation skin = AbstractClientPlayer.getLocationSkin(name);
		BufferedImage img = null;
		
		if (skin == null) {
			skin = AbstractClientPlayer.getLocationSkin("default");
		}
		
		ThreadDownloadImageData data = AbstractClientPlayer.getDownloadImageSkin(skin, name);
		img = ReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, data, "bufferedImage");
		
		if (img != null) {
			ColorModel cm = img.getColorModel();
			return new BufferedImage(cm, img.copyData(null), cm.isAlphaPremultiplied(), null);
		}
		return null;
	}*/
	
	public static void clearTailInfo(EntityPlayer player) {
		UUID id = player.getGameProfile().getId();
		
		if (TailMap.containsKey(id)) {
			TailMap.remove(id);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean needsBuild(EntityPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		Map map = mc.func_152342_ad().func_152788_a(player.getGameProfile());
		
		boolean hasskin = map.containsKey(MinecraftProfileTexture.Type.SKIN);
		boolean hasinfo = TailMap.containsKey(player.getGameProfile().getId());
		
		System.out.println("Skin: "+hasskin+", info: "+hasinfo);
		System.out.println(map);
		
		return hasskin && !hasinfo;
	}
}
