package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Math;
import uk.kihira.tails.client.gui.controls.IconButton;

class PreviewPanel extends Panel<GuiEditor>
{
    private double yaw = 0d;
    private double pitch = 0d;
    private double zoom  = 30;

    private static final int MAX_ZOOM = 100;
    private static final int MIN_ZOOM = 10;
    private static final double PITCH_MIN = Math.toRadians(110d);
    private static final double PITCH_MAX = Math.toRadians(250d);

    PreviewPanel(GuiEditor parent, int left, int top, int right, int bottom)
    {
        super(parent, left, top, right, bottom);

        // Reset Camera
        addChild(new IconButton(this.getRight() - 18,  this.getY() + 22, IconButton.Icons.UNDO, this::onUndoButtonPressed, Component.translatable("gui.button.reset.camera")));
        // Help
        addChild(new IconButton(this.getRight() - 18, this.getY() + 4, IconButton.Icons.QUESTION, this::onHelpButtonPressed, Component.translatable("gui.button.help.camera")));
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), GuiEditor.GREY);

        renderPlayer(graphics, this.getX(), this.getY(), this.getRight(), this.getBottom(), (float) this.yaw, (float) this.pitch);

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

    private void renderPlayer(GuiGraphics graphics, int x, int y, int right, int bottom, float yaw, float pitch)
    {
        var player = this.minecraft().player;
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var renderer = dispatcher.getRenderer(player);
        var renderState = (LivingEntityRenderState) renderer.createRenderState(player, 1f);
        renderState.lightCoords = 15728880;
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;

        Vector3f position = new Vector3f(0f, renderState.boundingBoxHeight / 2f, 0f);
        graphics.submitEntityRenderState(renderState, Mth.floor(this.zoom), position, new Quaternionf().rotationXYZ(pitch, yaw, 0f), null, x, y, right, bottom);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent p_446698_, boolean p_435133_)
    {
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY)
    {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT)
        {
            this.yaw += dragX * .1f;
            this.pitch = Math.clamp(PITCH_MIN, PITCH_MAX, this.pitch + (dragY * .1d));
        }

        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    protected int contentHeight()
    {
        //todo
        return 0;
    }

    @Override
    protected double scrollRate()
    {
        //todo
        return 0;
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
}
