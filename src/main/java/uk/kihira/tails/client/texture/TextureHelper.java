package uk.kihira.tails.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartRegistry;
import uk.kihira.tails.common.PartsData;

import java.awt.image.BufferedImage;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class TextureHelper {

    /**
     * Creates and loads the tail texture into memory based upon the provided params
     * @param uuid
     * @param partType
     * @param typeid The type ID
     * @param subid The subtype ID
     * @param textureID The texture ID
     * @param tints An array of int[3]     @return A resource location for the generated texture
     */
    // todo convert
    private static ResourceLocation generateTexture(UUID uuid, PartsData.PartType partType, int typeid, int subid, int textureID, int[] tints) {
        String[] textures = PartRegistry.getRenderer(typeid).getTextureNames(subid);
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

    private static void uploadTexture(ITextureObject textureObject, BufferedImage bufferedImage) {
        TextureUtil.uploadTextureImage(textureObject.getGlTextureId(), bufferedImage);
    }
}
