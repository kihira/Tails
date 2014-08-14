package kihira.tails.client.texture;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.render.RenderTail;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Point;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class TextureHelper {

    private static RenderTail[] tailTypes = ClientEventHandler.tailTypes;
	
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
            		ClientEventHandler.TailMap.put(id, buildTailInfoFromSkin(id, image));
            	}
            	else {
            		ClientEventHandler.TailMap.put(id, new TailInfo(id, false, 0, 0, 0, 0, 0, null));
            	}
            }
        }
	}

	private static TailInfo buildTailInfoFromSkin(UUID id, BufferedImage skin) {
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
		
		ResourceLocation tailtexture = generateTexture(id, typeid, subtype, textureid, new int[]{tint1, tint2, tint3});
		
		return new TailInfo(id, true, typeid, subtype, tint1, tint2, tint3, tailtexture);
	}

    /**
     * Creates and loads the tail texture into memory based upon the provided params
     * @param id The UUID of the owner
     * @param typeid The type ID
     * @param subid The subtype ID
     * @param textureID The texture ID
     * @param tints An array of int[3]
     * @return A resource location for the generated texture
     */
    public static ResourceLocation generateTexture(UUID id, int typeid, int subid, int textureID, int[] tints) {
        ResourceLocation tailtexture = new ResourceLocation("tails_"+id.toString()+"_"+typeid+"_"+subid+"_"+textureID);
        RenderTail tail = tailTypes[typeid];
        String texturePath = "texture/"+tail.getTextureNames()[textureID]+".png";
        Minecraft.getMinecraft().getTextureManager().loadTexture(tailtexture, new TripleTintTexture("tails", texturePath, tints[0], tints[1], tints[2]));
        Tails.logger.debug(String.format("Generated texture UUID: %s Type: %s SubType: %s Texture: %s Tints: %s", id, typeid, subid, textureID, Arrays.toString(tints)));
        return tailtexture;
    }

    public static ResourceLocation generateTexture(TailInfo tailInfo) {
        return generateTexture(tailInfo.id, tailInfo.typeid, tailInfo.subid, tailInfo.textureID, tailInfo.tints);
    }

    /**
     * Removes the {@link kihira.tails.common.TailInfo} for the player as well as deleting the texture from memory
     * @param player The player
     */
	public static void clearTailInfo(EntityPlayer player) {
		UUID id = player.getGameProfile().getId();
		
		if (ClientEventHandler.TailMap.containsKey(id)) {
            TailInfo tailInfo = ClientEventHandler.TailMap.get(id);
            Minecraft.getMinecraft().renderEngine.deleteTexture(tailInfo.texture);
			ClientEventHandler.TailMap.remove(id);
		}
	}
	
	public static boolean needsBuild(EntityPlayer player) {
		return !ClientEventHandler.TailMap.containsKey(player.getGameProfile().getId()) &&
				player.getGameProfile().getProperties().containsKey("textures");
	}
}
