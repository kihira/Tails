package kihira.tails.client.texture;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.render.RenderTail;
import kihira.tails.common.TailInfo;
import kihira.tails.common.Tails;
import kihira.tails.common.network.TailInfoMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Point;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
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
		UUID uuid = profile.getId();
		
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

                TailInfo tailInfo;
            	if (scol1 == switch1Colour && scol2 == switch2Colour) {
                    tailInfo = buildTailInfoFromSkin(uuid, image);
            	}
            	else {
            		tailInfo = new TailInfo(uuid, false, 0, 0, 0, 0, 0, null);
            	}
                Tails.proxy.addTailInfo(uuid, tailInfo);
                //If local player, send our skin info the server.
                if (player == Minecraft.getMinecraft().thePlayer) {
                    Tails.setLocalPlayerTailInfo(tailInfo);
                    Tails.networkWrapper.sendToServer(new TailInfoMessage(tailInfo, false));
                }
            }
        }
	}

    public static BufferedImage writeTailInfoToSkin(TailInfo tailInfo, AbstractClientPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        GameProfile profile = player.getGameProfile();
        Map map = mc.func_152342_ad().func_152788_a(profile);
        BufferedImage image = null;

        //Check we have the players skin
        if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            ResourceLocation skinloc = mc.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            ITextureObject skintex = mc.getTextureManager().getTexture(skinloc);

            if (skintex instanceof ThreadDownloadImageData) {
                ThreadDownloadImageData imagedata = (ThreadDownloadImageData)skintex;
                image = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imagedata, "bufferedImage", "field_110560_d", "bpj.g");
                if (image == null) return null;

                //Switch colours
                image.setRGB(switch1Pixel.getX(), switch1Pixel.getY(), switch1Colour);
                image.setRGB(switch2Pixel.getX(), switch2Pixel.getY(), switch2Colour);
                //Type, subtype and texture
                int dataColour = 0xFF;
                dataColour = dataColour | tailInfo.typeid << 16;
                dataColour = dataColour | tailInfo.subid << 8;
                dataColour = dataColour | tailInfo.textureID;
                image.setRGB(dataPixel.getX(), dataPixel.getY(), dataColour);
                //Tints
                image.setRGB(tint1Pixel.getX(), tint1Pixel.getY(), tailInfo.tints[0]);
                image.setRGB(tint2Pixel.getX(), tint2Pixel.getY(), tailInfo.tints[1]);
                image.setRGB(tint3Pixel.getX(), tint3Pixel.getY(), tailInfo.tints[2]);
            }
            Tails.logger.warn("Attempted to write TailInfo to skin but player doesn't have a skin!");
        }
        //Fallback to steve skin
        Tails.logger.warn("Attempted to write TailInfo to skin but player doesn't have a skin!");

        return image;
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
        ResourceLocation tailtexture = new ResourceLocation("tails_"+id.toString()+"_"+typeid+"_"+subid+"_"+textureID+"_"+tints[0]+"_"+tints[1]+"_"+tints[2]);
        RenderTail tail = tailTypes[typeid];
        String texturePath = "texture/"+tail.getTextureNames()[textureID]+".png";
        Minecraft.getMinecraft().getTextureManager().loadTexture(tailtexture, new TripleTintTexture("tails", texturePath, tints[0], tints[1], tints[2]));
        //Tails.logger.info(String.format("Generated texture UUID: %s Type: %s SubType: %s Texture: %s Tints: %s", id, typeid, subid, textureID, Arrays.toString(tints)));
        return tailtexture;
    }

    public static ResourceLocation generateTexture(TailInfo tailInfo) {
        return generateTexture(tailInfo.uuid, tailInfo.typeid, tailInfo.subid, tailInfo.textureID, tailInfo.tints);
    }
	
	public static boolean needsBuild(EntityPlayer player) {
		return !Tails.proxy.hasTailInfo(player.getPersistentID()) && player.getGameProfile().getProperties().containsKey("textures");
	}
}
