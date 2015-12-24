package kihira.tails.client.gui;

import kihira.foxlib.client.gui.GuiIconButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class PreviewPanel extends Panel<GuiEditor> {

    private float yaw = 0F;
    private float pitch = 10F;
    private int prevMouseX = -1;
    private ScaledResolution scaledRes;

    public PreviewPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        scaledRes = new ScaledResolution(this.mc);
        //Reset Camera
        buttonList.add(new GuiIconButton(0, width - 18, 22, GuiIconButton.Icons.UNDO, I18n.format("gui.button.reset.camera")));
        //Help
        buttonList.add(new GuiIconButton(1, width - 18, 4, GuiIconButton.Icons.QUESTION, I18n.format("gui.button.help.camera.0"), I18n.format("gui.button.help.camera.1")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        zLevel = -1000;
        //Background
        drawGradientRect(0, 0, width, height, 0xEE000000, 0xEE000000);

        //Player
        drawEntity((width / 2), (height / 2) + (scaledRes.getScaledHeight() / 4), scaledRes.getScaledHeight() / 4, yaw, pitch, mc.thePlayer);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        //Reset Camera
        if (button.id == 1) {
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
                yaw += (mouseX - prevMouseX) * 1.5F;
                prevMouseX = mouseX;
            }
        }
    }

/*    @Override
    TODO public void mouseMovedOrUp(int mouseX, int mouseY, int mouseEvent) {
        prevMouseX = -1;
        super.mouseMovedOrUp(mouseX, mouseY, mouseEvent);
    }*/

    private void drawEntity(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
        float prevHeadYaw = entity.rotationYawHead;
        float prevRotYaw = entity.rotationYaw;
        float prevRotPitch = entity.rotationPitch;
        ItemStack prevItemStack = entity.getHeldItem();

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 100F);
        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.0F, (float) entity.getYOffset(), 0.0F);
        GL11.glRotatef(pitch, 1F, 0.0F, 0.0F);
        GL11.glRotatef(yaw, 0F, 1F, 0.0F);

        entity.rotationYawHead = 0F;
        entity.rotationYaw = 0F;
        entity.rotationPitch = 0F;
        entity.renderYawOffset = 0F;
        entity.setSneaking(false);
        entity.setCurrentItemOrArmor(0, null);

        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
        Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(entity, 0D, 0D, 0D, 0F, 1F);
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        entity.rotationYawHead = prevHeadYaw;
        entity.rotationYaw = prevRotYaw;
        entity.rotationPitch = prevRotPitch;
        entity.setCurrentItemOrArmor(0, prevItemStack);

        GL11.glPopMatrix();
    }
}
