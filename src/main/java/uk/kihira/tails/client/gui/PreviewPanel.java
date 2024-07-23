package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import uk.kihira.tails.client.gui.controls.IconButton;

class PreviewPanel extends Panel<GuiEditor>
{
    private double yaw = 0d;
    private double pitch = 0d;
    private double zoom  = 30;
    private boolean doRender;

    private static final int MAX_ZOOM = 100;
    private static final int MIN_ZOOM = 10;

    PreviewPanel(GuiEditor parent, int left, int top, int right, int bottom)
    {
        super(parent, left, top, right, bottom);

        //this.doRender = Minecraft.getInstance().options.getPointOfView().func_243192_a(); // third person camera
/*        if (!this.doRender)
        {
            return;
        }*/

        // Reset Camera
        addChild(new IconButton(this.getRight() - 18,  this.getY() + 22, IconButton.Icons.UNDO, this::onUndoButtonPressed, Component.translatable("gui.button.reset.camera")));
        // Help
        addChild(new IconButton(this.getRight() - 18, this.getY() + 4, IconButton.Icons.QUESTION, this::onHelpButtonPressed, Component.translatable("gui.button.help.camera")));
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), GuiEditor.GREY);

        InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, this.getX(), this.getY(), this.getRight(), this.getBottom(), Mth.floor(this.zoom), 0f, (float) this.yaw, (float) this.pitch, minecraft().player);

        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }

    private void onUndoButtonPressed(GuiEventListener button)
    {
        this.yaw = 0;
        this.pitch = 10F;
    }

    private void onHelpButtonPressed(GuiEventListener button)
    {

    }

    @Override
    public boolean mouseClicked(double p_313764_, double p_313832_, int p_313688_)
    {
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (button == 0)
        {
            this.yaw -= dragX * .1f;
            this.pitch -= dragY * .1f;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY)
    {
        this.zoom = Mth.clamp(this.zoom + pScrollY, MIN_ZOOM, MAX_ZOOM);
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
    {

    }

    private static void drawEntity(PoseStack poseStack, int x, int y, int scale, float yaw, float pitch, AbstractClientPlayer entity)
    {
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
        entity.rotationPitch = prevRotPitch;*/
    }
}
