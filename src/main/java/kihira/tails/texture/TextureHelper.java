package kihira.tails.texture;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import kihira.tails.EventHandler;
import kihira.tails.TailInfo;
import kihira.tails.render.RenderTail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Point;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.UUID;

public class TextureHelper {

    private static RenderTail[] tailTypes = EventHandler.tailTypes;
	
	private static final Point switch1Pixel = new Point(56,16);
	private static final Point switch2Pixel = new Point(57,16);
	private static final Point dataPixel = new Point(58,16);
	private static final Point tint1Pixel = new Point(59,16);
	private static final Point tint2Pixel = new Point(60,16);
	private static final Point tint3Pixel = new Point(61,16);
	
	private static final int switch1Colour = 0xFFFF10F0;
	private static final int switch2Colour = 0xFFB8E080;
	
	@SuppressWarnings("rawtypes")
	public static void buildPlayerInfo(EntityPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		GameProfile profile = player.getGameProfile();
		UUID id = profile.getId();
		
		Map map = mc.func_152342_ad().func_152788_a(profile);
		
		if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            ResourceLocation skinloc = mc.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            ITextureObject skintex = mc.getTextureManager().getTexture(skinloc);
            
            if (skintex instanceof ThreadDownloadImageData) {
            	ThreadDownloadImageData imagedata = (ThreadDownloadImageData)skintex;
            	BufferedImage image = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imagedata, "bufferedImage", "field_110560_d", "bpj.g");
            	
            	if (image == null) { return; }
            	
            	int scol1 = image.getRGB(switch1Pixel.getX(), switch1Pixel.getY());
            	int scol2 = image.getRGB(switch2Pixel.getX(), switch2Pixel.getY());
            	
            	if (scol1 == switch1Colour && scol2 == switch2Colour) {
            		EventHandler.TailMap.put(id, buildTailInfo(id, image));
            	}
            	else {
            		EventHandler.TailMap.put(id, new TailInfo(id, false, 0, 0, 0, 0, 0, null));
            	}
            }
        }
	}

	private static TailInfo buildTailInfo(UUID id, BufferedImage skin) {
		int data = skin.getRGB(dataPixel.getX(), dataPixel.getY());
		
		int typeid = (data >> 16) & 0xFF;
		typeid = typeid >= tailTypes.length ? 0 : typeid;
		
		RenderTail type = tailTypes[typeid];
		String[] textures = type.getTextureNames();
		
		int subtype = (data >> 8) & 0xFF;
		int textureid = (data) & 0xFF;
		textureid = textureid >= textures.length ? 0 : textureid;
		
		int tint1 = skin.getRGB(tint1Pixel.getX(), tint1Pixel.getY());
		int tint2 = skin.getRGB(tint2Pixel.getX(), tint2Pixel.getY());
		int tint3 = skin.getRGB(tint3Pixel.getX(), tint3Pixel.getY());
		
		String texturepath = "texture/"+textures[textureid]+".png";
		
		//System.out.println("data: "+Integer.toHexString(data)+", type: "+typeid+", sub: "+subtype+", tid: "+textureid+", path: "+texturepath);
		
		ResourceLocation tailtexture = new ResourceLocation("tails_"+id.toString()+"_"+type+"_"+subtype+"_"+textureid);
		Minecraft.getMinecraft().getTextureManager().loadTexture(tailtexture, new TripleTintTexture("tails", texturepath, tint1, tint2, tint3));
		
		return new TailInfo(id, true, typeid, subtype, tint1, tint2, tint3, tailtexture);
	}
	
	public static void clearTailInfo(EntityPlayer player) {
		UUID id = player.getGameProfile().getId();
		
		if (EventHandler.TailMap.containsKey(id)) {
			EventHandler.TailMap.remove(id);
		}
	}
	
	public static boolean needsBuild(EntityPlayer player) {
		return !EventHandler.TailMap.containsKey(player.getGameProfile().getId()) &&
				player.getGameProfile().getProperties().containsKey("textures");
	}
}
