/*
package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.Rotation;
import org.joml.Vector3f;

class PreviewPanel extends Panel<GuiEditor>
{
    private float yaw = 0f;
    private float pitch = 10f;
    private int prevMouseX = -1;
    private boolean doRender;

    PreviewPanel(GuiEditor parent, int left, int top, int right, int bottom)
    {
        super(parent, left, top, right, bottom);
    }

    @Override
    public void init()
    {
*/
/*        this.doRender = Minecraft.getInstance().options.getPointOfView().func_243192_a(); // third person camera
        if (!this.doRender)
        {
            return;
        }*//*


        // Reset Camera
        //addRenderableWidget(new IconButton(width - 18, 22, IconButton.Icons.UNDO, this::onUndoButtonPressed, Component.translatable("gui.button.reset.camera")));
        // Help
        //addRenderableWidget(new IconButton(width - 18, 4, IconButton.Icons.QUESTION, this::onHelpButtonPressed, Component.translatable("gui.button.help.camera")));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        if (!this.doRender)
        {
            return;
        }
        graphics.fillGradient(-1000, 0, 0, this.width, this.height, GuiEditor.GREY, GuiEditor.GREY);

        drawEntity(graphics.pose(), this.width / 2, this.height / 2 + Minecraft.getInstance().getWindow().getGuiScaledHeight() / 8, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4, this.yaw, this.pitch, this.getMinecraft().player);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void onUndoButtonPressed(GuiEventListener button)
    {
        this.yaw = 0;
        this.pitch = 10F;
    }

    private void onHelpButtonPressed(GuiEventListener button)
    {

    }

    // todo
*/
/*    @Override
    public void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick)
    {
        if (lastButtonClicked == 0)
        {
            //Yaw
            if (this.prevMouseX != -1)
            {
                this.yaw += (mouseX - this.prevMouseX) * 1.5f;
            }
            this.prevMouseX = mouseX;
        }
    }*//*


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        this.prevMouseX = -1;
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private static void drawEntity(PoseStack poseStack, int x, int y, int scale, float yaw, float pitch, AbstractClientPlayer entity)
    {
*/
/*        float prevHeadYaw = entity.rotationYawHead;
        float prevRotYaw = entity.rotationYaw;
        float prevRotPitch = entity.rotationPitch;
        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();

        entity.setYHeadRot(0);
        entity.rotate(Rotation.NONE);
        entity.setPose(Pose.CROUCHING);

        poseStack.pushPose();

        poseStack.translate(x, y, 100f);
        poseStack.scale(-scale, scale, scale);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(180f), 0, 0, 0);
        //poseStack.translate(0d, entity.getYOffset(), 0d);
        poseStack.rotateAround(Axis.XP.rotationDegrees(pitch), 0, 0, 0);
        poseStack.rotateAround(Axis.YP.rotationDegrees(yaw), 0, 0, 0);

        // todo
        RenderHelper.enableStandardItemLighting();
        //renderManager.setPlayerViewY(180f);
        //renderManager.renderEntity(entity, 0d, 0d, 0d, 0f, 1f, false);
        RenderHelper.disableStandardItemLighting();
        //OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        //OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        poseStack.popPose();

        entity.rotationYawHead = prevHeadYaw;
        entity.rotationYaw = prevRotYaw;
        entity.rotationPitch = prevRotPitch;*//*

    }
}
*/
