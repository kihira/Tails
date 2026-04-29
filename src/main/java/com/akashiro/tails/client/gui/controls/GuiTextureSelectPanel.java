package com.akashiro.tails.client.gui.controls;

import com.akashiro.tails.client.PartRegistry;
import com.akashiro.tails.client.gui.GuiTailsEditor;
import com.akashiro.tails.client.render.RenderPart;
import com.akashiro.tails.common.data.PartInfo;
import com.akashiro.tails.common.data.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class GuiTextureSelectPanel {

    private static final int BUTTON_SIZE = 15;
    private static final int SELECT_Y = 17;

    private final GuiTailsEditor parent;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private String[] textureNames = new String[0];
    private int selectedTextureID;
    private PartsData.PartType currentType;

    public GuiTextureSelectPanel(GuiTailsEditor parent, int x, int y, int width, int height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void init() {
        onPartTypeChanged(parent.getSelectedPartType());
    }

    public void onPartTypeChanged(PartsData.PartType type) {
        currentType = type;
        selectedTextureID = 0;
        refreshTextures();
    }

    private void refreshTextures() {
        PartInfo info = parent.getCurrentPartInfo();
        if (info == null || !info.hasPart) {
            textureNames = new String[0];
            return;
        }

        RenderPart part = PartRegistry.getRenderPart(currentType, info.typeid);
        if (part == null) {
            textureNames = new String[0];
            return;
        }

        textureNames = part.getTextureNames(info.subid);
        selectedTextureID = Math.max(0, Math.min(info.textureID, textureNames.length - 1));
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, 0xCC111111);
        graphics.renderOutline(x, y, width, height, 0x55FFFFFF);
        graphics.fill(x + 7, y + SELECT_Y, x + width - 7, y + SELECT_Y + 15, 0x55000000);

        var font = Minecraft.getInstance().font;
        graphics.drawCenteredString(font, Component.translatable("gui.texture"), x + width / 2, y + 5, 0xFFFFFF);

        int leftX = x + 5;
        int buttonY = y + SELECT_Y;
        int rightX = x + width - 20;

        boolean canCycle = textureNames.length > 1;
        int buttonColor = canCycle ? 0xFF444444 : 0xFF2A2A2A;
        graphics.fill(leftX, buttonY, leftX + BUTTON_SIZE, buttonY + BUTTON_SIZE, buttonColor);
        graphics.fill(rightX, buttonY, rightX + BUTTON_SIZE, buttonY + BUTTON_SIZE, buttonColor);
        graphics.renderOutline(leftX, buttonY, BUTTON_SIZE, BUTTON_SIZE, 0xFF888888);
        graphics.renderOutline(rightX, buttonY, BUTTON_SIZE, BUTTON_SIZE, 0xFF888888);
        graphics.drawCenteredString(font, Component.literal("<"), leftX + BUTTON_SIZE / 2, buttonY + 4, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal(">"), rightX + BUTTON_SIZE / 2, buttonY + 4, 0xFFFFFF);

        if (textureNames.length == 0) {
            graphics.drawCenteredString(font,
                    Component.translatable("gui.not_available"),
                    x + width / 2,
                    y + SELECT_Y + 4,
                    0x888888);
            return;
        }

        String langKey = currentType.name().toLowerCase(Locale.ROOT) + ".texture." + textureNames[selectedTextureID] + ".name";
        graphics.drawCenteredString(font,
                Component.translatable(langKey),
                x + width / 2,
                y + SELECT_Y + 4,
                0xFFFFFF);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return;
        }
        if (textureNames.length <= 1) {
            return;
        }

        int leftX = x + 5;
        int rightX = x + width - 20;
        int buttonY = y + SELECT_Y;

        if (mouseY < buttonY || mouseY >= buttonY + BUTTON_SIZE) {
            return;
        }

        if (mouseX >= leftX && mouseX < leftX + BUTTON_SIZE) {
            selectedTextureID = selectedTextureID > 0 ? selectedTextureID - 1 : textureNames.length - 1;
            applySelection();
        } else if (mouseX >= rightX && mouseX < rightX + BUTTON_SIZE) {
            selectedTextureID = (selectedTextureID + 1) % textureNames.length;
            applySelection();
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double delta) {
    }

    private void applySelection() {
        PartInfo info = parent.getCurrentPartInfo();
        if (info == null || !info.hasPart) {
            return;
        }

        info.textureID = selectedTextureID;
        info.needsTextureCompile = true;
        parent.refreshLivePreview();
    }
}
