package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Math;
import uk.kihira.tails.client.gui.controls.IconButton;

class PreviewPanel extends Panel<OutfitEditScreen>
{
    private double yaw = 0d;
    private double pitch = Math.toRadians(180d);
    private double zoom;
    private boolean isCrouching = false;
    private final ExtendedSlider headYawSlider;
    private final ExtendedSlider headPitchSlider;

    private final IconButton resetCameraButton;
    private final IconButton helpButton;

    private static final int MAX_ZOOM = 100;
    private static final int MIN_ZOOM = 10;
    private static final double PITCH_MIN = Math.toRadians(110d);
    private static final double PITCH_MAX = Math.toRadians(250d);

    PreviewPanel(OutfitEditScreen parent, int left, int top, int right, int bottom)
    {
        super(parent, left, top, right, bottom);

        // Scale zoom based on width
        this.zoom = Math.clamp(MIN_ZOOM, MAX_ZOOM, Math.lerp(MIN_ZOOM, MAX_ZOOM, (float) this.getWidth() /this.getHeight()));

        // Help
        addChild(this.helpButton = new IconButton(this.getRight() - 18, this.getY() + 4, IconButton.Icons.QUESTION, this::onHelpButtonPressed, Component.translatable("tails.gui.button.help.camera")));
        this.helpButton.setTooltip(Tooltip.create(Component.translatable("tails.gui.button.help.camera")));

        // Reset Camera
        addChild(this.resetCameraButton = new IconButton(this.getRight() - 18,  this.getY() + 22, IconButton.Icons.UNDO, this::onUndoButtonPressed, Component.translatable("tails.gui.button.reset.camera")));
        this.resetCameraButton.setTooltip(Tooltip.create(Component.translatable("tails.gui.button.reset.camera")));

         // Pose Control
        var crouchingButton = CycleButton.onOffBuilder(false)
                .create(this.getX() + 4, this.getBottom() - 20, 80, 16, Component.translatable("tails.gui.button.crouch"), (button, value) -> this.isCrouching = value);
        addChild(crouchingButton);

        addChild(this.headYawSlider = new PitchYawSlider(this.getX() + 90, this.getBottom() - 13, 80, 12, Component.translatable("tails.gui.slider.head_yaw"), -90f, 90f, 0f));
        addChild(this.headPitchSlider = new PitchYawSlider(this.getX() + 90, this.getBottom() - 13, 80, 12, Component.translatable("tails.gui.slider.head_pitch"), -90f, 90f, 0f));
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), OutfitEditScreen.GREY);

        renderPlayer(graphics, this.getX(), this.getY(), this.getRight(), this.getBottom(), (float) this.yaw, (float) this.pitch);

        // Render pose control

        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }

    private void onUndoButtonPressed(GuiEventListener button)
    {
        this.yaw = 0d;
        this.pitch = Math.toRadians(180d);
    }

    private void onHelpButtonPressed(GuiEventListener button)
    {

    }

    private void renderPlayer(GuiGraphics graphics, int x, int y, int right, int bottom, float yaw, float pitch)
    {
        var player = this.minecraft().player;
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var renderer = dispatcher.getRenderer(player);
        var renderState = (AvatarRenderState) renderer.createRenderState(player, 1f);
        renderState.lightCoords = 15728880;
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;
        renderState.bodyRot = 0;
        renderState.yRot = (float) this.headYawSlider.getValue();
        renderState.xRot = (float) this.headPitchSlider.getValue();
        renderState.isCrouching = this.isCrouching;
        //renderState.rightArmPose = HumanoidModel.ArmPose.SPYGLASS;

        Vector3f position = new Vector3f(0f, renderState.boundingBoxHeight / 2f, 0f);
        graphics.submitEntityRenderState(renderState, Mth.floor(this.zoom), position, new Quaternionf().rotationXYZ(pitch, yaw, 0f), null, x, y, right, bottom);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick)
    {
        // Process any buttons etc first before capturing the mouse for panning
        for (var child : this.children())
        {
            if (child.isMouseOver(event.x(), event.y()))
            {
                return super.mouseClicked(event, isDoubleClick);
            }
        }

        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY)
    {
        // Process any buttons etc first before capturing the mouse for panning
        for (var child : this.children())
        {
            if (child.isMouseOver(event.x(), event.y()))
            {
                return super.mouseDragged(event, dragX, dragY);
            }
        }

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
        return this.getHeight();
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

    private static class PitchYawSlider extends ExtendedSlider
    {
        PitchYawSlider(int x, int y, int width, int height, Component text, float min, float max, float defaultValue)
        {
            super(x, y, width, height, text, Component.empty(), min, max, defaultValue, 1, 0, true);
        }

        @Override
        protected void updateMessage()
        {
            this.setMessage(prefix);
        }
    }
}
