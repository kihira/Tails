package uk.kihira.tails.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.tails.client.OutfitPart;

import java.awt.image.BufferedImage;

@SideOnly(Side.CLIENT)
public class TextureHelper {

    public static ResourceLocation generateTexture(OutfitPart part) {
        String texturePath = "texture/parts/"+part.basePart+".png"; // todo best way to do textures?

        // Add UUID to prevent deleting similar textures.
        // todo chance of collision
        ResourceLocation texture = new ResourceLocation("tails_"+part.basePart+"_"+part.tints[0]+"_"+part.tints[1]+"_"+part.tints[2]+"_"+part.basePart);
        Minecraft.getMinecraft().getTextureManager().loadTexture(texture, new TripleTintTexture("tails", texturePath, part.tints[0], part.tints[1], part.tints[2]));

        return texture;
    }

    private static void uploadTexture(ITextureObject textureObject, BufferedImage bufferedImage) {
        TextureUtil.uploadTextureImage(textureObject.getGlTextureId(), bufferedImage);
    }
}
