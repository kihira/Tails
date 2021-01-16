package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.gui.GuiUtils;

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
        this.doRender = Minecraft.getInstance().gameSettings.getPointOfView().func_243192_a(); // third person camera
        if (!this.doRender)
        {
            return;
        }

        // Reset Camera
        addButton(new IconButton(width - 18, 22, IconButton.Icons.UNDO, this::onUndoButtonPressed, I18n.format("gui.button.reset.camera")));
        // Help
        addButton(new IconButton(width - 18, 4, IconButton.Icons.QUESTION, this::onHelpButtonPressed, I18n.format("gui.button.help.camera")));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (!this.doRender)
        {
            return;
        }
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), -1000, 0, 0, this.width, this.height, GuiEditor.GREY, GuiEditor.GREY);

        drawEntity(matrixStack, this.width / 2, this.height / 2 + Minecraft.getInstance().getMainWindow().getScaledHeight() / 8, Minecraft.getInstance().getMainWindow().getScaledHeight() / 4, this.yaw, this.pitch, this.minecraft.player);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void onUndoButtonPressed(Button button)
    {
        this.yaw = 0;
        this.pitch = 10F;
    }

    private void onHelpButtonPressed(Button button)
    {

    }

    // todo
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
    }*/

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        this.prevMouseX = -1;
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private static void drawEntity(MatrixStack matrixStack, int x, int y, int scale, float yaw, float pitch, ClientPlayerEntity entity)
    {
        float prevHeadYaw = entity.rotationYawHead;
        float prevRotYaw = entity.rotationYaw;
        float prevRotPitch = entity.rotationPitch;
        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();

        entity.rotationYawHead = 0f;
        entity.rotationYaw = 0f;
        entity.rotationPitch = 0f;
        entity.renderYawOffset = 0f;
        entity.setSneaking(false);

        matrixStack.push();

        matrixStack.translate(x, y, 100f);
        matrixStack.scale(-scale, scale, scale);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180f));
        matrixStack.translate(0d, entity.getYOffset(), 0d);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(pitch));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(yaw));
        GlStateManager.color4f(1f, 1f, 1f, 1f);

        // todo
        RenderHelper.enableStandardItemLighting();
        //renderManager.setPlayerViewY(180f);
        //renderManager.renderEntity(entity, 0d, 0d, 0d, 0f, 1f, false);
        RenderHelper.disableStandardItemLighting();
        //OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        //OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        matrixStack.pop();

        entity.rotationYawHead = prevHeadYaw;
        entity.rotationYaw = prevRotYaw;
        entity.rotationPitch = prevRotPitch;
    }
}
