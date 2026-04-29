package com.akashiro.tails.client.render;

import com.akashiro.tails.client.model.ModelPartBase;
import com.akashiro.tails.client.texture.TextureHelper;
import com.akashiro.tails.common.data.PartInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class RenderPart {

    public final String name;
    public final int subTypes;
    protected final String[] textureNames;
    protected String[][] authors;
    protected String modelAuthor;
    public final ModelPartBase modelPart;
    private static final Map<Class<? extends LivingEntity>, IRenderHelper> RENDER_HELPERS = new HashMap<>();

    public RenderPart(String name, int subTypes, ModelPartBase modelPart,
                      String modelAuthor, String[] textureNames) {
        this.name = name;
        this.subTypes = subTypes;
        this.modelPart = modelPart;
        this.modelAuthor = modelAuthor;
        this.textureNames = textureNames;
        this.authors = new String[subTypes][textureNames.length];
    }

    public void render(PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight,
                       LivingEntity entity,
                       PartInfo info,
                       double x,
                       double y,
                       double z,
                       float partialTicks) {
        if (info == null || !info.hasPart) return;

        if (info.needsTextureCompile || info.getTexture() == null) {
            ResourceLocation tex = TextureHelper.generateTexture(entity.getUUID(), info);
            info.setTexture(tex);
        }

        poseStack.pushPose();
        IRenderHelper helper = entity instanceof Player
                ? getRenderHelper(Player.class)
                : getRenderHelper(entity.getClass());
        if (helper != null) helper.onPreRenderPart(poseStack, entity, this, info, x, y, z);
        doRender(poseStack, buffer, packedLight, entity, info, partialTicks);
        poseStack.popPose();
    }

    protected void doRender(PoseStack poseStack,
                            MultiBufferSource buffer,
                            int packedLight,
                            LivingEntity entity,
                            PartInfo info,
                            float partialTicks) {
        ResourceLocation texture = info.getTexture();
        if (texture == null) return;

        var consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        modelPart.render(poseStack, consumer, packedLight, entity, info.subid, partialTicks);
    }

    public void renderWithoutHelper(PoseStack poseStack,
                                    MultiBufferSource buffer,
                                    int packedLight,
                                    LivingEntity entity,
                                    PartInfo info,
                                    float partialTicks) {
        if (info == null || !info.hasPart) {
            return;
        }

        if (info.needsTextureCompile || info.getTexture() == null) {
            ResourceLocation tex = TextureHelper.generateTexture(entity.getUUID(), info);
            info.setTexture(tex);
        }

        doRender(poseStack, buffer, packedLight, entity, info, partialTicks);
    }

    public static void registerRenderHelper(Class<? extends LivingEntity> clazz, IRenderHelper helper) {
        if (RENDER_HELPERS.containsKey(clazz) || helper == null) {
            throw new IllegalArgumentException("An invalid RenderHelper was registered!");
        }
        RENDER_HELPERS.put(clazz, helper);
    }

    public static IRenderHelper getRenderHelper(Class<? extends LivingEntity> clazz) {
        return RENDER_HELPERS.getOrDefault(clazz, null);
    }

    public String[] getTextureNames(int subType) {
        return textureNames;
    }

    public int getAvailableSubTypes() {
        return subTypes;
    }

    public String getUnlocalisedName(int subType) {
        return name + "." + subType + ".name";
    }

    public String getModelAuthor() {
        return modelAuthor;
    }

    public RenderPart setAuthor(String author, int subID, int textureID) {
        if (authors[subID] == null) authors[subID] = new String[textureNames.length];
        if (textureID < authors[subID].length) authors[subID][textureID] = author;
        return this;
    }

    public RenderPart setAuthor(String author, int subID) {
        for (int i = 0; i < textureNames.length; i++) setAuthor(author, subID, i);
        return this;
    }

    public RenderPart setAuthor(String author) {
        for (int s = 0; s < subTypes; s++) setAuthor(author, s);
        return this;
    }

    public String getAuthor(int subID, int textureID) {
        if (authors == null || subID >= authors.length) return null;
        if (authors[subID] == null || textureID >= authors[subID].length) return null;
        return authors[subID][textureID];
    }

    public boolean hasAuthor(int subID, int textureID) {
        return getAuthor(subID, textureID) != null;
    }
}
