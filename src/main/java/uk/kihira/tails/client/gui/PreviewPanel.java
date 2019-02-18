package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
class PreviewPanel extends Panel<GuiEditor> {
    private static final int RESET_BUTTON = 0;
    private static final int HELP_BUTTON = 1;

    private float yaw = 0f;
    private float pitch = 10f;
    private double prevMouseX = -1;
    private ScaledResolution scaledRes;
    private boolean doRender;

    PreviewPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    public void initGui() {
        doRender = Minecraft.getInstance().gameSettings.thirdPersonView == 0;
        if (!doRender)
            return;
        scaledRes = new ScaledResolution(this.mc);
        // Reset Camera
        buttons.add(new GuiIconButton(RESET_BUTTON, width - 18, 22, GuiIconButton.Icons.UNDO, I18n.format("gui.button.reset.camera")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);

                yaw = 0;
                pitch = 10F;
            }
        });
        // Help
        buttons.add(new GuiIconButton(HELP_BUTTON, width - 18, 4, GuiIconButton.Icons.QUESTION, I18n.format("gui.button.help.camera")));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!doRender)
            return;
        zLevel = -1000;
        // Background
        drawRect(0, 0, width, height, GuiEditor.GREY);

        // Player
        drawEntity(width / 2, height / 2 + scaledRes.getScaledHeight() / 8, scaledRes.getScaledHeight() / 4, yaw, pitch, mc.player);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (mouseButton == 0) {
            //Yaw
            if (prevMouseX == -1) prevMouseX = mouseX;
            else {
                yaw += (mouseX - prevMouseX) * 1.5f;
                prevMouseX = mouseX;
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        prevMouseX = -1;
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private static void drawEntity(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
        float prevHeadYaw = entity.rotationYawHead;
        float prevRotYaw = entity.rotationYaw;
        float prevRotPitch = entity.rotationPitch;
        RenderManager renderManager = Minecraft.getInstance().getRenderManager();

        entity.rotationYawHead = 0f;
        entity.rotationYaw = 0f;
        entity.rotationPitch = 0f;
        entity.renderYawOffset = 0f;
        entity.setSneaking(false);

        GlStateManager.pushMatrix();

        GlStateManager.translatef(x, y, 100f);
        GlStateManager.scalef(-scale, scale, scale);
        GlStateManager.rotatef(180f, 0f, 0f, 1f);
        GlStateManager.translated(0d, entity.getYOffset(), 0d);
        GlStateManager.rotatef(pitch, 1f, 0f, 0f);
        GlStateManager.rotatef(yaw, 0f, 1f, 0f);
        GlStateManager.color4f(1f, 1f, 1f, 1f);

        RenderHelper.enableStandardItemLighting();
        renderManager.setPlayerViewY(180f);
        renderManager.renderEntity(entity, 0d, 0d, 0d, 0f, 1f, false);
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        GlStateManager.popMatrix();

        entity.rotationYawHead = prevHeadYaw;
        entity.rotationYaw = prevRotYaw;
        entity.rotationPitch = prevRotPitch;
    }
}
