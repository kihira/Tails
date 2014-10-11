/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.texture;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.render.RenderPart;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import kihira.tails.common.network.PlayerDataMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Point;

import java.awt.image.BufferedImage;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class TextureHelper {
	
	private static final Point switch1Pixel = new Point(56,16);
	private static final Point switch2Pixel = new Point(57,16);
	private static final Point dataPixel = new Point(58,16);
	private static final Point tint1Pixel = new Point(59,16);
	private static final Point tint2Pixel = new Point(60,16);
	private static final Point tint3Pixel = new Point(61,16);
	
	private static final int switch1Colour = 0xFFFF10F0;
	private static final int switch2Colour = 0xFFB8E080;

    @SuppressWarnings("rawtypes")
	public static void buildPlayerInfo(AbstractClientPlayer player) {
		GameProfile profile = player.getGameProfile();
		UUID uuid = profile.getId();
        BufferedImage image = kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player);
        if (image != null) {
            int scol1 = image.getRGB(switch1Pixel.getX(), switch1Pixel.getY());
            int scol2 = image.getRGB(switch2Pixel.getX(), switch2Pixel.getY());

            PartInfo tailInfo;
            if (scol1 == switch1Colour && scol2 == switch2Colour) {
                tailInfo = buildTailInfoFromSkin(uuid, image);
            }
            else {
                tailInfo = new PartInfo(uuid, false, 0, 0, 0, 0, 0, 0, null);
            }

            //Players part data
            PartsData partsData = Tails.proxy.getPartsData(uuid);
            if (partsData == null) {
                partsData = new PartsData(uuid);
            }
            partsData.setPartInfo(PartsData.PartType.TAIL, tailInfo);
            Tails.proxy.addPartsData(uuid, partsData);

            //If local player, send our skin info the server.
            if (player == Minecraft.getMinecraft().thePlayer) {
                Tails.setLocalPartsData(partsData);
                Tails.networkWrapper.sendToServer(new PlayerDataMessage(partsData, false));
            }
        }
	}

    public static BufferedImage writeTailInfoToSkin(PartInfo partInfo, AbstractClientPlayer player) {
        BufferedImage image = kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player);

        //Check we have the players skin
        if (image != null) {
            //Switch colours
            image.setRGB(switch1Pixel.getX(), switch1Pixel.getY(), switch1Colour);
            image.setRGB(switch2Pixel.getX(), switch2Pixel.getY(), switch2Colour);
            //Type, subtype and texture
            int dataColour = 0xFF000000;
            dataColour = dataColour | partInfo.typeid << 16;
            dataColour = dataColour | partInfo.subid << 8;
            dataColour = dataColour | partInfo.textureID;
            image.setRGB(dataPixel.getX(), dataPixel.getY(), dataColour);
            //Tints
            image.setRGB(tint1Pixel.getX(), tint1Pixel.getY(), partInfo.tints[0]);
            image.setRGB(tint2Pixel.getX(), tint2Pixel.getY(), partInfo.tints[1]);
            image.setRGB(tint3Pixel.getX(), tint3Pixel.getY(), partInfo.tints[2]);
        }
        else Tails.logger.warn("Attempted to write TailInfo to skin but player doesn't have a skin!");

        return image;
    }

	public static PartInfo buildTailInfoFromSkin(UUID id, BufferedImage skin) {
        PartsData.PartType partType = PartsData.PartType.TAIL;
		int data = skin.getRGB(dataPixel.getX(), dataPixel.getY());
		int typeid = (data >> 16) & 0xFF;
        int subtype = (data >> 8) & 0xFF;
        int textureid = (data) & 0xFF;
		typeid = typeid >= partType.renderParts.length ? 0 : typeid;
		
		RenderPart type = partType.renderParts[typeid];
		String[] textures = type.getTextureNames(subtype);

		textureid = textureid >= textures.length ? 0 : textureid;
		
		int tint1 = skin.getRGB(tint1Pixel.getX(), tint1Pixel.getY());
		int tint2 = skin.getRGB(tint2Pixel.getX(), tint2Pixel.getY());
		int tint3 = skin.getRGB(tint3Pixel.getX(), tint3Pixel.getY());
		
		ResourceLocation tailtexture = generateTexture(id, typeid, subtype, textureid, new int[]{tint1, tint2, tint3});
		
		return new PartInfo(id, true, typeid, subtype, 0, tint1, tint2, tint3, tailtexture);
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
    //TODO need to be able to put wings and ear data on skin
    public static ResourceLocation generateTexture(UUID id, int typeid, int subid, int textureID, int[] tints) {
        PartsData.PartType partType = PartsData.PartType.TAIL;
        RenderPart tail = partType.renderParts[typeid];
        String[] textures = tail.getTextureNames(subid);
        textureID = textureID >= textures.length ? 0 : textureID;
        String texturePath = "texture/tail/"+textures[textureID]+".png";

        ResourceLocation tailtexture = new ResourceLocation("tails_"+id.toString()+"_"+typeid+"_"+subid+"_"+textureID+"_"+tints[0]+"_"+tints[1]+"_"+tints[2]);
        Minecraft.getMinecraft().getTextureManager().loadTexture(tailtexture, new TripleTintTexture("tails", texturePath, tints[0], tints[1], tints[2]));

        return tailtexture;
    }

    public static ResourceLocation generateTexture(PartInfo partInfo) {
        return generateTexture(partInfo.uuid, partInfo.typeid, partInfo.subid, partInfo.textureID, partInfo.tints);
    }
	
	public static boolean needsBuild(EntityPlayer player) {
		return !Tails.proxy.hasPartsData(player.getPersistentID()) && player.getGameProfile().getProperties().containsKey("textures");
	}
}
