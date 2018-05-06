package uk.kihira.tails.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.common.Tails;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class TextureHelper {

    public static ResourceLocation generateTexture(OutfitPart part) {
        String texturePath = "texture/parts/"+part.basePart+"/"+part.texture+".png";

        // Add UUID to prevent deleting similar textures.
        ResourceLocation texture = new ResourceLocation(Tails.MOD_ID,"part_"+part.basePart+"_"+part.tint[0]+"_"+part.tint[1]+"_"+part.tint[2]+"_"+ UUID.randomUUID());
        Minecraft.getMinecraft().getTextureManager().loadTexture(texture, new TripleTintTexture(texturePath, part.tint[0], part.tint[1], part.tint[2]));

        return texture;
    }
}
