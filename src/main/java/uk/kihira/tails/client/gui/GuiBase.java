package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public abstract class GuiBase extends GuiBaseScreen
{
    //0 is bottom layer
    private final ArrayList<ArrayList<Panel<?>>> layers = new ArrayList<>();

    GuiBase(ITextComponent titleIn, int layerCount)
    {
        super(titleIn);

        for (int i = 0; i < layerCount; i++)
        {
            layers.add(new ArrayList<>());
        }
    }

    ArrayList<Panel<?>> getLayer(int layer)
    {
        return layers.get(layer);
    }

    /**
     * Gets all layers that exist in this GUI
     * @return The layers
     */
    final Collection<ArrayList<Panel<?>>> getAllLayers()
    {
        return layers;
    }

    /**
     * Gets all the panels across all the layers
     * @return All the panels
     */
    final Collection<Panel<?>> getAllPanels()
    {
        ArrayList<Panel<?>> panels = new ArrayList<>();
        getAllLayers().forEach(panels::addAll);
        return panels;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        super.resize(minecraft, width, height);
        for (ArrayList<Panel<?>> layer : layers)
        {
            for (Panel<?> panel : layer)
            {
                //todo switch over to using scaled resolution to allow for cramming more stuff on screen
                // Gotta cache displayWidth/Height as it can't be passed in as params anymore
//                int displayWidth = minecraft.getMainWindow().getScaledWidth();
//                int displayHeight = minecraft.getMainWindow().getScaledHeight();
//                minecraft.displayWidth = panel.right - panel.left;
//                minecraft.displayHeight = panel.bottom - panel.top;
//                ScaledResolution scaledRes = new ScaledResolution(mc);
//                panel.resize(minecraft, scaledRes.width, scaledRed.height);
//                mc.displayWidth = displayWidth;
//                mc.displayHeight = displayHeight;
                panel.resize(minecraft, panel.right - panel.left, panel.bottom - panel.top);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        for (ArrayList<Panel<?>> layer : layers)
        {
            for (Panel<?> panel : layer)
            {
                if (panel.enabled)
                {
                    matrixStack.push();
                    matrixStack.translate(panel.left, panel.top, 0);
                    RenderSystem.color4f(1f, 1f, 1f, 1f);
                    panel.render(matrixStack, mouseX - panel.left, mouseY - panel.top, partialTicks);
                    RenderSystem.disableLighting();
                    matrixStack.pop();
                }
            }
        }

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        for (ArrayList<Panel<?>> layer : this.layers)
        {
            for (Panel<?> panel : layer)
            {
                if (panel.enabled)
                {
                    panel.charTyped(codePoint, modifiers);
                }
            }
        }
        super.charTyped(codePoint, modifiers);
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (ArrayList<Panel<?>> layer : this.layers)
        {
            for (Panel<?> panel : layer)
            {
                if (shouldReceiveMouse(mouseX, mouseY, panel))
                {
                    panel.mouseClicked(mouseX - panel.left, mouseY - panel.top, button);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        for (ArrayList<Panel<?>> layer : this.layers)
        {
            for (Panel<?> panel : layer) {
                if (shouldReceiveMouse(mouseX, mouseY, panel))
                {
                    panel.mouseReleased(mouseX - panel.left, mouseY - panel.top, button);
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    // todo
/*    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long pressTime) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (shouldReceiveMouse(mouseX, mouseY, panel)) {
                    panel.mouseClickMove(mouseX - panel.left, mouseY - panel.top, mouseButton, pressTime);
                }
            }
        }
        super.mouseClickMove(mouseX, mouseY, mouseButton, pressTime);
    }*/

    // todo
/*    @Override
    public void handleMouseInput() throws IOException {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (panel.enabled) {
                    panel.handleMouseInput();
                }
            }
        }
        super.handleMouseInput();
    }*/

    @Override
    public void onClose()
    {
        for (ArrayList<Panel<?>> layer : this.layers)
        {
            for (Panel<?> panel : layer)
            {
                panel.onClose();
            }
        }
        super.onClose();
    }

    /**
     * Whether the indicated panel should receive the current mouse event based
     * @param mouseX The mouse's x position
     * @param mouseY The mouse's y position
     * @param panel The panel
     * @return Whether it should receive them
     */
    private boolean shouldReceiveMouse(double mouseX, double mouseY, Panel<?> panel)
    {
        return panel.enabled && mouseX > panel.left && mouseX < panel.right && mouseY > panel.top && mouseY < panel.bottom || panel.alwaysReceiveMouse;
    }
}
