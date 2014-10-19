/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client;

import com.google.common.collect.ArrayListMultimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.model.ModelPartBase;
import kihira.tails.client.model.ears.ModelCatEars;
import kihira.tails.client.model.ears.ModelFoxEars;
import kihira.tails.client.model.ears.ModelPandaEars;
import kihira.tails.client.model.tail.*;
import kihira.tails.client.model.wings.ModelMetalWings;
import kihira.tails.client.render.RenderPart;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.List;

//Yeah using side only isn't nice but as this is static, it means it only gets constructed on the client
@SideOnly(Side.CLIENT)
public class PartRegistry {

    private static final ArrayListMultimap<PartsData.PartType, RenderPart> partRegistry = ArrayListMultimap.create();

    static {
        //Tails
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.fluffy", 2, new ModelFluffyTail(), "foxTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.dragon", 1, new ModelDragonTail(), "dragonTail", "dragonTailStriped"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.raccoon", 0, new ModelRaccoonTail(), "racoonTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.devil", 1, new ModelDevilTail(), "devilTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.cat", 0, new ModelCatTail(), "tabbyTail", "tigerTail"));
        registerPart(PartsData.PartType.TAIL, new RenderPart("tail.bird", 0, new ModelBirdTail(), "birdTail"));

        //Ears
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.fox", 0, new ModelFoxEars(), "foxEars"));
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.cat", 0, new ModelCatEars(), "catEars"));
        registerPart(PartsData.PartType.EARS, new RenderPart("ears.panda", 0, new ModelPandaEars(), "pandaEars"));

        //Wings
        registerPart(PartsData.PartType.WINGS, new RenderPart("wings.big", 0, null, "bigWings") {
            @Override
            protected void doRender(EntityLivingBase entity, PartInfo info, float partialTicks) {
                Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
                Tessellator tessellator = Tessellator.instance;
                boolean isFlying = entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying && entity.isAirBorne || entity.fallDistance > 0F;
                float timestep = ModelPartBase.getAnimationTime(isFlying ? 500 : 6000, entity);
                float angle = (float) Math.sin(timestep) * (isFlying ? 20F : 6F);
                float scale = 2F;

                GL11.glTranslatef(0, -16F * ModelPartBase.SCALE, 0.1F);
                GL11.glRotatef(90, 0, 1, 0);
                GL11.glRotatef(90, 0, 0, 1);
                GL11.glScalef(scale, scale, scale);
                GL11.glTranslatef(0.1F, -0.4F * ModelPartBase.SCALE, -0.025F);

                GL11.glPushMatrix();
                GL11.glTranslatef(0F, 0F, 1F * ModelPartBase.SCALE);
                GL11.glRotatef(30F - angle, 1F, 0F, 0F);
                ItemRenderer.renderItemIn2D(tessellator, 0, 0, 1, 1, 32, 32, ModelPartBase.SCALE / scale);
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glTranslatef(0F, 0.5F * ModelPartBase.SCALE, 0F);
                GL11.glRotatef(-30F + angle, 1F, 0F, 0F);
                ItemRenderer.renderItemIn2D(tessellator, 0, 0, 1, 1, 32, 32, ModelPartBase.SCALE / scale);
                GL11.glPopMatrix();
            }
        });
        registerPart(PartsData.PartType.WINGS, new RenderPart("wings.metal", 0, new ModelMetalWings(), "metalWings"));
    }

    public static void registerPart(PartsData.PartType partType, RenderPart renderPart) {
        partRegistry.put(partType, renderPart);
    }

    public static List<RenderPart> getParts(PartsData.PartType partType) {
        return partRegistry.get(partType);
    }

    /**
     * Safely gets a render part. By safely, this means it checks if the type id is within bounds of the list for that
     * part type and if not, returns the RenderPart associated with type id 0.
     * @param partType The part type
     * @param index The index/type id
     * @return The render part
     */
    public static RenderPart getRenderPart(PartsData.PartType partType, int index) {
        List<RenderPart> parts = PartRegistry.getParts(partType);
        index = index > parts.size() ? 0 : index;
        return parts.get(index);
    }
}
