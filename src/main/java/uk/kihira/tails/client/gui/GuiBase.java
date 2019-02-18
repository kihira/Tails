package uk.kihira.tails.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public abstract class GuiBase extends GuiBaseScreen {

    //0 is bottom layer
    private final ArrayList<ArrayList<Panel>> layers = new ArrayList<>();

    GuiBase(int layerCount) {
        for (int i = 0; i < layerCount; i++) {
            layers.add(new ArrayList<>());
        }
    }

    ArrayList<Panel> getLayer(int layer) {
        return layers.get(layer);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                //todo switch over to using scaled resolution to allow for cramming more stuff on screen
                // Gotta cache displayWidth/Height as it can't be passed in as params anymore
                int displayWidth = mc.displayWidth;
                int displayHeight = mc.displayHeight;
                mc.displayWidth = panel.right - panel.left;
                mc.displayHeight = panel.bottom - panel.top;
                ScaledResolution scaledRes = new ScaledResolution(mc);
                panel.setWorldAndResolution(mc, scaledRes.getScaledWidth(), scaledRes.getScaledHeight());
                mc.displayWidth = displayWidth;
                mc.displayHeight = displayHeight;
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (panel.enabled) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef(panel.left, panel.top, 0);
                    GlStateManager.color4f(1f, 1f, 1f, 1f);
                    panel.render(mouseX - panel.left, mouseY - panel.top, partialTicks);
                    GlStateManager.disableLighting();
                    GlStateManager.popMatrix();
                }
            }
        }
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean charTyped(char key, int keyCode) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (panel.enabled) panel.keyTyped(key, keyCode);
            }
        }
        super.charTyped(key, keyCode);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (shouldReceiveMouse(mouseX, mouseY, panel)) {
                    panel.mouseClicked(mouseX - panel.left, mouseY - panel.top, mouseButton);
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (shouldReceiveMouse(mouseX, mouseY, panel)) {
                    panel.mouseReleased(mouseX - panel.left, mouseY - panel.top, mouseButton);
                }
            }
        }
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long pressTime) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (shouldReceiveMouse(mouseX, mouseY, panel)) {
                    panel.mouseClickMove(mouseX - panel.left, mouseY - panel.top, mouseButton, pressTime);
                }
            }
        }
        super.mouseClickMove(mouseX, mouseY, mouseButton, pressTime);
    }

    @Override
    public void onGuiClosed() {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                panel.onGuiClosed();
            }
        }
        super.onGuiClosed();
    }

    /**
     * Whether the indicated panel should receive the current mouse event based
     * @param mouseX The mouse's x position
     * @param mouseY The mouse's y position
     * @param panel The panel
     * @return Whether it should receive them
     */
    private boolean shouldReceiveMouse(double mouseX, double mouseY, Panel panel) {
        return panel.enabled && mouseX > panel.left && mouseX < panel.right && mouseY > panel.top && mouseY < panel.bottom || panel.alwaysReceiveMouse;
    }
}
