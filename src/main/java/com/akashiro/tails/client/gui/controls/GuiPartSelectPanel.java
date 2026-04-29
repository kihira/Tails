package com.akashiro.tails.client.gui.controls;

import com.akashiro.tails.client.PartRegistry;
import com.akashiro.tails.client.gui.GuiTailsEditor;
import com.akashiro.tails.client.render.RenderPart;
import com.akashiro.tails.common.data.PartInfo;
import com.akashiro.tails.common.data.PartsData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuiPartSelectPanel {

    private static final int ROW_HEIGHT = 55;
    private static final int HEADER_HEIGHT = 35;
    private static final int TYPE_BUTTON_WIDTH = 56;
    private static final int TYPE_BUTTON_HEIGHT = 16;
    private static final int SELECTED_COLOR = 0xFF4F7FB0;
    private static final int HOVER_COLOR = 0x33FFFFFF;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int AUTHOR_COLOR = 0x55FFFF;
    private static final int FULL_BRIGHT = 15728880;

    private final GuiTailsEditor parent;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private PartsData.PartType currentType;
    private List<RowEntry> rows = List.of();
    private int selectedTypeID = -1;
    private int selectedSubID;
    private int scrollOffset;

    public GuiPartSelectPanel(GuiTailsEditor parent, int x, int y, int width, int height) {
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
        scrollOffset = 0;
        syncSelectionFromEditor();
        rebuildRows();
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        graphics.drawCenteredString(font,
                Component.translatable("gui.partselect"),
                x + width / 2,
                y + 5,
                TEXT_COLOR);

        int buttonX = x + (width - TYPE_BUTTON_WIDTH) / 2;
        int buttonY = y + 16;
        graphics.fill(buttonX, buttonY, buttonX + TYPE_BUTTON_WIDTH, buttonY + TYPE_BUTTON_HEIGHT, 0xFF555555);
        graphics.renderOutline(buttonX, buttonY, TYPE_BUTTON_WIDTH, TYPE_BUTTON_HEIGHT, 0xFFB8B8B8);
        graphics.drawCenteredString(font,
                Component.translatable("gui.part." + currentType.name().toLowerCase(Locale.ROOT)),
                buttonX + TYPE_BUTTON_WIDTH / 2,
                buttonY + 4,
                TEXT_COLOR);

        int listTop = y + HEADER_HEIGHT;
        int listBottom = y + height;
        graphics.renderOutline(x, listTop, width, Math.max(0, height - HEADER_HEIGHT), 0x55FFFFFF);
        graphics.enableScissor(x, listTop, x + width, listBottom);
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            drawRow(graphics, font, mouseX, mouseY, rowIndex, rows.get(rowIndex));
        }
        graphics.disableScissor();
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return;
        }

        int buttonX = x + (width - TYPE_BUTTON_WIDTH) / 2;
        int buttonY = y + 16;
        if (mouseX >= buttonX && mouseX < buttonX + TYPE_BUTTON_WIDTH
                && mouseY >= buttonY && mouseY < buttonY + TYPE_BUTTON_HEIGHT) {
            parent.cycleSelectedType();
            return;
        }

        int listTop = y + HEADER_HEIGHT;
        if (mouseY < listTop) {
            return;
        }

        int clickedRow = scrollOffset + (int) ((mouseY - listTop) / ROW_HEIGHT);
        if (clickedRow < 0 || clickedRow >= rows.size()) {
            return;
        }

        RowEntry row = rows.get(clickedRow);
        if (row.typeID < 0) {
            selectedTypeID = -1;
            selectedSubID = 0;
        } else {
            selectedTypeID = row.typeID;
            selectedSubID = row.subID;
        }
        applySelection();
    }

    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX < x || mouseX >= x + width || mouseY < y + HEADER_HEIGHT || mouseY >= y + height) {
            return;
        }

        int maxVisibleRows = Math.max(1, (height - HEADER_HEIGHT) / ROW_HEIGHT);
        int maxScroll = Math.max(0, rows.size() - maxVisibleRows);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) delta));
    }

    private void rebuildRows() {
        List<RowEntry> builtRows = new ArrayList<>();
        builtRows.add(new RowEntry(-1, 0, "gui.part.none", PartInfo.none(currentType)));

        List<RenderPart> currentParts = PartRegistry.getParts(currentType);
        for (int typeID = 0; typeID < currentParts.size(); typeID++) {
            RenderPart part = currentParts.get(typeID);
            for (int subID = 0; subID < part.subTypes; subID++) {
                PartInfo info = new PartInfo();
                info.hasPart = true;
                info.typeid = typeID;
                info.subid = subID;
                info.textureID = 0;
                info.partType = currentType;
                info.scale = 1.0F;
                info.tints = new int[]{0xFF0000, 0x00FF00, 0x0000FF};
                info.needsTextureCompile = true;
                builtRows.add(new RowEntry(typeID, subID, part.getUnlocalisedName(subID), info));
            }
        }
        rows = builtRows;
    }

    private void syncSelectionFromEditor() {
        PartInfo info = parent.getCurrentPartInfo();
        if (info != null && info.hasPart) {
            selectedTypeID = info.typeid;
            selectedSubID = info.subid;
        } else {
            selectedTypeID = -1;
            selectedSubID = 0;
        }
    }

    private void drawRow(GuiGraphics graphics, Font font, int mouseX, int mouseY, int rowIndex, RowEntry row) {
        int rowY = y + HEADER_HEIGHT + (rowIndex - scrollOffset) * ROW_HEIGHT;
        int listBottom = y + height;
        if (rowY < y + HEADER_HEIGHT || rowY + ROW_HEIGHT > listBottom) {
            return;
        }

        boolean selected = row.typeID == selectedTypeID && row.subID == selectedSubID;
        boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;

        if (selected) {
            graphics.fill(x, rowY, x + width, rowY + ROW_HEIGHT, SELECTED_COLOR);
        } else if (hovered) {
            graphics.fill(x, rowY, x + width, rowY + ROW_HEIGHT, HOVER_COLOR);
        }

        if (row.info.hasPart) {
            PartInfo previewInfo = getPreviewInfo(row, selected);
            renderPartPreview(graphics, rowY, previewInfo, selected);
            drawWrappedLabel(graphics, font, row.langKey, rowY + 15, getLabelWidth(previewInfo));
            if (selected) {
                drawAuthorLine(graphics, font, row, rowY + 37);
            }
        } else {
            graphics.drawString(font,
                    Component.translatable(row.langKey),
                    x + 5,
                    rowY + (ROW_HEIGHT / 2) - 4,
                    TEXT_COLOR,
                    false);
        }
    }

    private void drawWrappedLabel(GuiGraphics graphics, Font font, String langKey, int textY, int maxWidth) {
        List<FormattedCharSequence> lines = font.split(Component.translatable(langKey), maxWidth);
        int linesToDraw = Math.min(2, lines.size());
        for (int i = 0; i < linesToDraw; i++) {
            graphics.drawString(font, lines.get(i), x + 5, textY + i * 10, TEXT_COLOR);
        }
    }

    private void drawAuthorLine(GuiGraphics graphics, Font font, RowEntry row, int textY) {
        RenderPart part = PartRegistry.getRenderPart(currentType, row.typeID);
        if (part == null || part.getModelAuthor() == null) {
            return;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(x + 5, textY, 0.0F);
        graphics.pose().scale(0.75F, 0.75F, 1.0F);
        graphics.drawString(font,
                Component.translatable("gui.createdby").getString() + ":",
                0,
                0,
                TEXT_COLOR,
                false);
        graphics.drawString(font,
                part.getModelAuthor(),
                0,
                10,
                AUTHOR_COLOR,
                false);
        graphics.pose().popPose();
    }

    private int getLabelWidth(PartInfo info) {
        return width - getPreviewLaneWidth(info) - 10;
    }

    private PartInfo getPreviewInfo(RowEntry row, boolean selected) {
        PartInfo previewInfo = row.info.deepCopy();
        if (!selected) {
            return previewInfo;
        }

        PartInfo currentInfo = parent.getCurrentPartInfo();
        if (currentInfo == null
                || !currentInfo.hasPart
                || currentInfo.typeid != row.typeID
                || currentInfo.subid != row.subID) {
            return previewInfo;
        }

        previewInfo.textureID = currentInfo.textureID;
        previewInfo.scale = currentInfo.scale;
        previewInfo.needsTextureCompile = true;
        previewInfo.setTexture(null);
        if (currentInfo.tints != null && currentInfo.tints.length == 3) {
            previewInfo.tints = currentInfo.tints.clone();
        }
        return previewInfo;
    }

    private void renderPartPreview(GuiGraphics graphics, int rowY, PartInfo info, boolean selected) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        RenderPart part = PartRegistry.getRenderPart(info.partType, info.typeid);
        if (part == null) {
            return;
        }

        int previewLaneLeft = x + width - getPreviewLaneWidth(info);
        int previewX = getPreviewCenterX(info);
        int previewY = rowY + getPreviewBaseY(info);
        int previewScale = getPreviewScale(info);

        RenderSystem.enableDepthTest();
        graphics.enableScissor(previewLaneLeft, rowY, x + width, rowY + ROW_HEIGHT);
        graphics.pose().pushPose();
        graphics.pose().translate(previewX, previewY, selected ? 10.0F : 1.0F);
        graphics.pose().scale(-previewScale, previewScale, 1.0F);
        applyPreviewTransform(graphics, info);
        part.renderWithoutHelper(graphics.pose(), graphics.bufferSource(), FULL_BRIGHT, player, info, 0.0F);
        graphics.flush();
        graphics.pose().popPose();
        graphics.disableScissor();
        RenderSystem.disableDepthTest();
    }

    private void applyPreviewTransform(GuiGraphics graphics, PartInfo info) {
        switch (info.partType) {
            case TAIL -> {
                if (info.typeid == 0 && info.subid == 2) {
                    graphics.pose().translate(0.0F, 0.85F, 0.0F);
                } else {
                    graphics.pose().translate(0.0F, 0.65F, 0.0F);
                }
                graphics.pose().scale(0.9F, 0.9F, 0.9F);
            }
            case EARS -> {
                graphics.pose().translate(0.05F, 1.35F, 0.0F);
                graphics.pose().mulPose(Axis.YP.rotationDegrees(145.0F));
                graphics.pose().mulPose(Axis.XP.rotationDegrees(-12.0F));
            }
            case WINGS -> {
                graphics.pose().translate(0.32F, 1.18F, 0.0F);
                graphics.pose().mulPose(Axis.YP.rotationDegrees(180.0F));
                graphics.pose().mulPose(Axis.YP.rotationDegrees(-45.0F));
                graphics.pose().mulPose(Axis.XP.rotationDegrees(25.0F));
                graphics.pose().scale(0.82F, 0.82F, 0.82F);
            }
            case MUZZLE -> {
                graphics.pose().translate(0.08F, 0.92F, 0.0F);
                graphics.pose().mulPose(Axis.YP.rotationDegrees(105.0F));
                graphics.pose().mulPose(Axis.XP.rotationDegrees(-10.0F));
                graphics.pose().scale(0.92F, 0.92F, 0.92F);
            }
        }
    }

    private int getPreviewLaneWidth(PartInfo info) {
        return switch (info.partType) {
            case TAIL -> 52;
            case EARS -> 42;
            case WINGS -> 40;
            case MUZZLE -> 40;
        };
    }

    private int getPreviewCenterX(PartInfo info) {
        return switch (info.partType) {
            case TAIL -> x + width - 20;
            case EARS -> x + width - 18;
            case WINGS -> x + width - 14;
            case MUZZLE -> x + width - 18;
        };
    }

    private int getPreviewBaseY(PartInfo info) {
        return switch (info.partType) {
            case TAIL -> -25;
            case EARS -> -11;
            case WINGS -> -12;
            case MUZZLE -> -12;
        };
    }

    private int getPreviewScale(PartInfo info) {
        return switch (info.partType) {
            case TAIL -> 36;
            case EARS -> 30;
            case WINGS -> 28;
            case MUZZLE -> 34;
        };
    }

    private void applySelection() {
        PartInfo info;
        if (selectedTypeID < 0) {
            info = PartInfo.none(currentType);
        } else {
            PartInfo currentInfo = parent.getCurrentPartInfo();
            info = new PartInfo();
            info.hasPart = true;
            info.typeid = selectedTypeID;
            info.subid = selectedSubID;
            info.partType = currentType;
            if (currentInfo != null && currentInfo.tints != null && currentInfo.tints.length == 3) {
                info.tints = currentInfo.tints.clone();
            } else {
                info.tints = new int[]{0xFF0000, 0x00FF00, 0x0000FF};
            }
            info.textureID = 0;
            info.scale = 1.0F;
            info.needsTextureCompile = true;
        }
        parent.setCurrentPartInfo(info);
        syncSelectionFromEditor();
    }

    private record RowEntry(int typeID, int subID, String langKey, PartInfo info) {
    }
}
