package com.akashiro.tails.client.texture;

import com.akashiro.tails.Tails;
import com.akashiro.tails.client.PartRegistry;
import com.akashiro.tails.client.render.RenderPart;
import com.akashiro.tails.common.data.PartInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TextureHelper {

    private static final Map<ResourceLocation, ResourceLocation> GENERATED_TEXTURES =
            new ConcurrentHashMap<>();

    public static ResourceLocation generateTexture(UUID uuid, PartInfo info) {
        if (info == null || !info.hasPart) return null;

        RenderPart renderPart = PartRegistry.getRenderPart(info.partType, info.typeid);
        if (renderPart == null) return null;

        String[] texNames = renderPart.getTextureNames(info.subid);
        if (texNames == null || info.textureID < 0 || info.textureID >= texNames.length) {
            return null;
        }

        String texName = texNames[info.textureID];
        ResourceLocation baseLoc = new ResourceLocation(
                Tails.MOD_ID,
                "texture/" + info.partType.name().toLowerCase(Locale.ROOT) + "/" + texName + ".png"
        );

        ResourceLocation cacheKey = new ResourceLocation(
                Tails.MOD_ID,
                "generated/" + uuid.toString().replace("-", "")
                        + "_" + info.partType.name().toLowerCase(Locale.ROOT)
                        + "_" + info.typeid
                        + "_" + info.subid
                        + "_" + info.textureID
                        + "_" + info.tints[0]
                        + "_" + info.tints[1]
                        + "_" + info.tints[2]
        );

        var minecraft = Minecraft.getInstance();
        var texManager = minecraft.getTextureManager();
        ResourceLocation cachedLocation = GENERATED_TEXTURES.get(cacheKey);
        if (cachedLocation != null && texManager.getTexture(cachedLocation, null) != null) {
            return cachedLocation;
        }

        try {
            var dynamicTexture = TripleTintTexture.create(
                    minecraft.getResourceManager(),
                    baseLoc,
                    info.tints[0], info.tints[1], info.tints[2]
            );
            ResourceLocation registeredLocation = texManager.register(
                    cacheKey.getPath(),
                    dynamicTexture
            );
            GENERATED_TEXTURES.put(cacheKey, registeredLocation);
            return registeredLocation;
        } catch (IOException e) {
            Tails.LOGGER.error("Failed to generate tint texture from {}", baseLoc, e);
            return null;
        }
    }
}
