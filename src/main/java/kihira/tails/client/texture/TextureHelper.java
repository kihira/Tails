/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.texture;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import kihira.tails.client.PartRegistry;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import kihira.tails.common.network.PlayerDataMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Point;

import java.awt.image.BufferedImage;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class TextureHelper {

    private static final int switch1Colour = 0xFFFF10F0;
    private static final int switch2Colour = 0xFFB8E080;

    private static final Point[] dataPoints = new Point[] {new Point(58,16), new Point(58,17), new Point(58,18)};
    private static final Point[][] switchPoints = new Point[][]{
            new Point[]{new Point(56,16), new Point(57,16)},
            new Point[]{new Point(56,17), new Point(57,17)},
            new Point[]{new Point(56,18), new Point(57,18)}
    };
    private static final Point[][] tintPoints = new Point[][] {
            new Point[] {new Point(59,16), new Point(60,16), new Point(61,16)},
            new Point[] {new Point(59,17), new Point(60,17), new Point(61,17)},
            new Point[] {new Point(59,18), new Point(60,18), new Point(61,18)}
    };

    /**
     * Returns if the player has any PartInfo encoded onto the skin file no matter the type.
     * @param player The player
     * @return Has part info(s).
     */
    public static boolean hasSkinData(AbstractClientPlayer player) {
        BufferedImage image = kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player);
        if (image != null) {
            for (PartsData.PartType partType : PartsData.PartType.values()) {
                int ordinal = partType.ordinal();
                int scol1 = image.getRGB(switchPoints[ordinal][0].getX(), switchPoints[ordinal][0].getY());
                int scol2 = image.getRGB(switchPoints[ordinal][1].getX(), switchPoints[ordinal][1].getY());

                if (scol1 == switch1Colour && scol2 == switch2Colour) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
	public static void buildPlayerPartsData(AbstractClientPlayer player) {
		GameProfile profile = player.getGameProfile();
		UUID uuid = profile.getId();
        BufferedImage image = kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player);
        if (image != null) {
            //Players part data
            PartsData partsData = Tails.proxy.getPartsData(uuid);
            if (partsData == null) {
                partsData = new PartsData();
            }

            //Load part data from skin
            for (PartsData.PartType partType : PartsData.PartType.values()) {
                int ordinal = partType.ordinal();
                int scol1 = image.getRGB(switchPoints[ordinal][0].getX(), switchPoints[ordinal][0].getY());
                int scol2 = image.getRGB(switchPoints[ordinal][1].getX(), switchPoints[ordinal][1].getY());

                PartInfo tailInfo;
                if (scol1 == switch1Colour && scol2 == switch2Colour) {
                    tailInfo = buildPartInfoFromSkin(partType, image, player.getUniqueID());
                }
                else {
                    tailInfo = new PartInfo(false, 0, 0, 0, 0, 0, 0, null, partType);
                }
                partsData.setPartInfo(partType, tailInfo);
            }

            Tails.proxy.addPartsData(uuid, partsData);

            //If local player, send our skin info the server.
            if (player == Minecraft.getMinecraft().thePlayer) {
                Tails.setLocalPartsData(partsData);
                Tails.networkWrapper.sendToServer(new PlayerDataMessage(UUIDTypeAdapter.fromString(Minecraft.getMinecraft().getSession().getPlayerID()), partsData, false));
            }
        }
	}

    public static BufferedImage writePartsDataToSkin(PartsData partsData, AbstractClientPlayer player) {
        BufferedImage image = kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player);

        //Check we have the players skin
        if (image != null) {
            for (PartsData.PartType partType : PartsData.PartType.values()) {
                PartInfo partInfo = partsData.getPartInfo(partType);
                int ordinal = partType.ordinal();
                int switch1 = 0x00000000, switch2 = 0x00000000;
                if (partInfo != null) {
                    if (partInfo.hasPart) {
                        switch1 = switch1Colour;
                        switch2 = switch2Colour;
                    }

                    //Type, subtype and texture
                    int dataColour = 0xFF000000;
                    dataColour = dataColour | partInfo.typeid << 16;
                    dataColour = dataColour | partInfo.subid << 8;
                    dataColour = dataColour | partInfo.textureID;
                    image.setRGB(dataPoints[ordinal].getX(), dataPoints[ordinal].getY(), dataColour);
                    //Tints
                    image.setRGB(tintPoints[ordinal][0].getX(), tintPoints[ordinal][0].getY(), partInfo.tints[0]);
                    image.setRGB(tintPoints[ordinal][1].getX(), tintPoints[ordinal][1].getY(), partInfo.tints[1]);
                    image.setRGB(tintPoints[ordinal][2].getX(), tintPoints[ordinal][2].getY(), partInfo.tints[2]);
                }
                //Switch colours
                image.setRGB(switchPoints[ordinal][0].getX(), switchPoints[ordinal][0].getY(), switch1);
                image.setRGB(switchPoints[ordinal][1].getX(), switchPoints[ordinal][1].getY(), switch2);
            }
        }
        else Tails.logger.warn("Attempted to write PartInfo to skin but player doesn't have a skin!");

        return image;
    }

	private static PartInfo buildPartInfoFromSkin(PartsData.PartType partType, BufferedImage skin, UUID uuid) {
        int ordinal = partType.ordinal();
		int data = skin.getRGB(dataPoints[ordinal].getX(), dataPoints[ordinal].getY());
		int typeid = (data >> 16) & 0xFF;
        int subtype = (data >> 8) & 0xFF;
        int textureid = (data) & 0xFF;
		String[] textures = PartRegistry.getRenderPart(partType, typeid).getTextureNames(subtype);

		textureid = textureid >= textures.length ? 0 : textureid;
		
		int tint1 = skin.getRGB(tintPoints[ordinal][0].getX(), tintPoints[ordinal][0].getY());
		int tint2 = skin.getRGB(tintPoints[ordinal][1].getX(), tintPoints[ordinal][1].getY());
		int tint3 = skin.getRGB(tintPoints[ordinal][2].getX(), tintPoints[ordinal][2].getY());
		
		ResourceLocation tailTexture = generateTexture(uuid, partType, typeid, subtype, textureid, new int[] {tint1, tint2, tint3});
		
		return new PartInfo(true, typeid, subtype, 0, tint1, tint2, tint3, tailTexture, partType);
	}

    /**
     * Creates and loads the tail texture into memory based upon the provided params
     * @param uuid
     * @param partType
     * @param typeid The type ID
     * @param subid The subtype ID
     * @param textureID The texture ID
     * @param tints An array of int[3]     @return A resource location for the generated texture
     */
    private static ResourceLocation generateTexture(UUID uuid, PartsData.PartType partType, int typeid, int subid, int textureID, int[] tints) {
        String[] textures = PartRegistry.getRenderPart(partType, typeid).getTextureNames(subid);
        textureID = textureID >= textures.length ? 0 : textureID;
        String texturePath = "texture/" + partType.name().toLowerCase() + "/"+textures[textureID]+".png";

        //Add UUID to prevent deleting similar textures.
        ResourceLocation tailTexture = new ResourceLocation("tails_"+uuid+"_"+partType.name()+"_"+typeid+"_"+subid+"_"+textureID+"_"+tints[0]+"_"+tints[1]+"_"+tints[2]);
        Minecraft.getMinecraft().getTextureManager().loadTexture(tailTexture, new TripleTintTexture("tails", texturePath, tints[0], tints[1], tints[2]));

        return tailTexture;
    }

    public static ResourceLocation generateTexture(UUID uuid, PartInfo partInfo) {
        return generateTexture(uuid, partInfo.partType, partInfo.typeid, partInfo.subid, partInfo.textureID, partInfo.tints);
    }
	
	public static boolean needsBuild(EntityPlayer player) {
		return !Tails.proxy.hasPartsData(player.getPersistentID()) && player.getGameProfile().getProperties().containsKey("textures");
	}
}
