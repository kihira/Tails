package uk.kihira.tails.client.render;

import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.client.model.ModelPartBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class RenderWings extends LegacyPartRenderer {

    public RenderWings() {
        super(null);
    }

    @Override
    protected void doRender(EntityLivingBase entity, OutfitPart outfitPart, float partialTicks) {
        BufferBuilder renderer = Tessellator.getInstance().getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(outfitPart.getCompiledTexture());
        boolean isFlying = entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying && entity.isAirBorne || entity.fallDistance > 0F;
        float timestep = ModelPartBase.getAnimationTime(isFlying ? 500 : 6500, entity);
        float angle = (float) Math.sin(timestep) * (isFlying ? 24F : 4F);
        float scale = 2F;

        GlStateManager.translate(0, -(scale * 8F) * ModelPartBase.SCALE, 0.1F);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.rotate(90, 0, 0, 1);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0.1F, -0.4F * ModelPartBase.SCALE, -0.025F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, 0F, 1F * ModelPartBase.SCALE);
        GlStateManager.rotate(30F - angle, 1F, 0F, 0F);
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(0, 1, 0).tex(0, 0).endVertex();
        renderer.pos(1, 1, 0).tex(1, 0).endVertex();
        renderer.pos(1, 0, 0).tex(1, 1).endVertex();
        renderer.pos(0, 0, 0).tex(0, 1).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, 0.3F * ModelPartBase.SCALE, 0F);
        GlStateManager.rotate(-30F + angle, 1F, 0F, 0F);
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(0, 1, 0).tex(0, 0).endVertex();
        renderer.pos(1, 1, 0).tex(1, 0).endVertex();
        renderer.pos(1, 0, 0).tex(1, 1).endVertex();
        renderer.pos(0, 0, 0).tex(0, 1).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.popMatrix();
    }
}
