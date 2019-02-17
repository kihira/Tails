package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;

class PreviewPanel extends Panel<GuiEditor> {
    private static final int RESET_BUTTON = 0;
    private static final int HELP_BUTTON = 1;

    private float yaw = 0f;
    private float pitch = 10f;
    private int prevMouseX = -1;
    private ScaledResolution scaledRes;
    private boolean doRender;

    PreviewPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    public void initGui() {
        doRender = Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
        if (!doRender)
            return;
        scaledRes = new ScaledResolution(this.mc);
        // Reset Camera
        buttons.add(new GuiIconButton(RESET_BUTTON, width - 18, 22, GuiIconButton.Icons.UNDO, I18n.format("gui.button.reset.camera")));
        // Help
        buttons.add(new GuiIconButton(HELP_BUTTON, width - 18, 4, GuiIconButton.Icons.QUESTION, I18n.format("gui.button.help.camera")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!doRender)
            return;
        zLevel = -1000;
        // Background
        drawRect(0, 0, width, height, GuiEditor.GREY);

        // Player
        drawEntity(width / 2, height / 2 + scaledRes.getScaledHeight() / 8, scaledRes.getScaledHeight() / 4, yaw, pitch, mc.player);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // Reset Camera
        if (button.id == RESET_BUTTON) {
            yaw = 0;
            pitch = 10F;
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (lastButtonClicked == 0) {
            //Yaw
            if (prevMouseX == -1) prevMouseX = mouseX;
            else {
                yaw += (mouseX - prevMouseX) * 1.5f;
                prevMouseX = mouseX;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        prevMouseX = -1;
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private static void drawEntity(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
        float prevHeadYaw = entity.rotationYawHead;
        float prevRotYaw = entity.rotationYaw;
        float prevRotPitch = entity.rotationPitch;
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        entity.rotationYawHead = 0f;
        entity.rotationYaw = 0f;
        entity.rotationPitch = 0f;
        entity.renderYawOffset = 0f;
        entity.setSneaking(false);

        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, 100f);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180f, 0f, 0f, 1f);
        GlStateManager.translate(0d, entity.getYOffset(), 0d);
        GlStateManager.rotate(pitch, 1f, 0f, 0f);
        GlStateManager.rotate(yaw, 0f, 1f, 0f);
        GlStateManager.color(1f, 1f, 1f, 1f);

        RenderHelper.enableStandardItemLighting();
        renderManager.setPlayerViewY(180f);
        renderManager.doRenderEntity(entity, 0d, 0d, 0d, 0f, 1f, false);
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        GlStateManager.popMatrix();

        entity.rotationYawHead = prevHeadYaw;
        entity.rotationYaw = prevRotYaw;
        entity.rotationPitch = prevRotPitch;
    }
}
