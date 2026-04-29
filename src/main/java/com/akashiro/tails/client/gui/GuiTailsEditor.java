package com.akashiro.tails.client.gui;

import com.akashiro.tails.Tails;
import com.akashiro.tails.client.gui.controls.GuiPartSelectPanel;
import com.akashiro.tails.client.gui.controls.GuiTextureSelectPanel;
import com.akashiro.tails.client.gui.controls.GuiTintPanel;
import com.akashiro.tails.client.gui.library.GuiLibrary;
import com.akashiro.tails.common.data.PartInfo;
import com.akashiro.tails.common.data.PartsData;
import com.akashiro.tails.common.network.PlayerDataPacket;
import com.akashiro.tails.common.network.TailsNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Quaternionf;

public class GuiTailsEditor extends Screen {

    private static final int LEFT_W = 110;
    private static final int RIGHT_W = 120;
    private static final int TEX_H = 43;
    private static final int CONTROL_H = 30;
    private static final int BTN_H = 20;
    private static final int BTN_W = 62;
    private static final int PREVIEW_ICON_SIZE = 14;

    private PartsData editingData;
    private PartsData originalData;
    private PartsData.PartType selectedType = PartsData.PartType.TAIL;
    private boolean savedChanges;
    private boolean previewRestored;

    private GuiPartSelectPanel partPanel;
    private GuiTintPanel tintPanel;
    private GuiTextureSelectPanel texPanel;

    private float previewYaw = 0F;
    private float previewPitch = 10F;
    private int lastMX;
    private int lastMY;
    private boolean dragging;

    public GuiTailsEditor() {
        super(Component.translatable("gui.button.editor"));
    }

    @Override
    protected void init() {
        originalData = Tails.localPartsData != null ? Tails.localPartsData.deepCopy() : new PartsData();
        editingData = originalData.deepCopy();
        savedChanges = false;
        previewRestored = false;

        int w = this.width;
        int h = this.height;

        int controlTop = h - CONTROL_H;
        int previewRight = w - RIGHT_W;
        int btnY = h - 25;
        int doneX = previewRight - BTN_W - 4;
        int saveX = doneX - BTN_W - 6;
        int resetX = saveX - BTN_W - 6;

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .bounds(doneX, btnY, BTN_W, BTN_H)
                .build());
        addRenderableWidget(Button.builder(Component.translatable("gui.button.save"), b -> onSave())
                .bounds(saveX, btnY, BTN_W, BTN_H)
                .build());
        addRenderableWidget(Button.builder(Component.translatable("gui.button.reset"), b -> onReset())
                .bounds(resetX, btnY, BTN_W, BTN_H)
                .build());
        addRenderableWidget(Button.builder(Component.translatable("gui.button.mode.library"),
                        b -> Minecraft.getInstance().setScreen(new GuiLibrary(editingData.deepCopy())))
                .bounds(4, btnY, 84, BTN_H)
                .build());

        int textureY = controlTop - TEX_H;
        partPanel = new GuiPartSelectPanel(this, 0, 0, LEFT_W, textureY);
        tintPanel = new GuiTintPanel(this, w - RIGHT_W, 0, RIGHT_W, h);
        texPanel = new GuiTextureSelectPanel(this, 0, textureY, LEFT_W, TEX_H);

        partPanel.init();
        tintPanel.init();
        texPanel.init();

        selectType(PartsData.PartType.TAIL);
        refreshLivePreview();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int w = this.width;
        int h = this.height;
        int controlTop = h - CONTROL_H;
        int textureY = controlTop - TEX_H;
        int previewRight = w - RIGHT_W;

        graphics.fill(0, 0, LEFT_W, textureY, 0xCC101010);
        graphics.fill(w - RIGHT_W, 0, w, h, 0xCC101010);
        graphics.fill(LEFT_W, 0, previewRight, controlTop, 0x7A000000);
        graphics.fill(LEFT_W, controlTop, previewRight, h, 0xCC151515);

        graphics.fill(LEFT_W, 0, LEFT_W + 2, controlTop, 0x90D8D8D8);
        graphics.fill(previewRight - 2, 0, previewRight, controlTop, 0x30FFFFFF);
        graphics.fill(LEFT_W, controlTop, previewRight, controlTop + 1, 0x50FFFFFF);
        graphics.fill(0, textureY - 1, LEFT_W, textureY, 0x40FFFFFF);

        renderPreview(graphics, w, h);
        renderPreviewButtons(graphics);

        partPanel.render(graphics, mouseX, mouseY, partialTick);
        texPanel.render(graphics, mouseX, mouseY, partialTick);
        tintPanel.render(graphics, mouseX, mouseY, partialTick);

        super.render(graphics, mouseX, mouseY, partialTick);
        renderPreviewTooltips(graphics, mouseX, mouseY);
    }

    private void renderPreview(GuiGraphics graphics, int width, int height) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        int controlTop = height - CONTROL_H;
        int previewWidth = width - LEFT_W - RIGHT_W;
        int centerX = LEFT_W + previewWidth / 2;
        int centerY = controlTop / 2 + 58;
        int scale = Math.max(48, Math.min(82, previewWidth / 5));

        float previousBodyRot = player.yBodyRot;
        float previousYaw = player.getYRot();
        float previousPitch = player.getXRot();
        float previousHeadYaw = player.yHeadRot;
        float previousHeadYawOld = player.yHeadRotO;

        Quaternionf pitchRotation = new Quaternionf().rotateX((float) Math.toRadians(previewPitch));
        Quaternionf poseRotation = new Quaternionf().rotateZ((float) Math.PI);
        poseRotation.mul(new Quaternionf(pitchRotation));

        player.yBodyRot = 180.0F + previewYaw;
        player.setYRot(180.0F + previewYaw);
        player.setXRot(-previewPitch);
        player.yHeadRot = player.getYRot();
        player.yHeadRotO = player.getYRot();

        InventoryScreen.renderEntityInInventory(
                graphics,
                centerX,
                centerY,
                scale,
                poseRotation,
                new Quaternionf(pitchRotation),
                player
        );

        player.yBodyRot = previousBodyRot;
        player.setYRot(previousYaw);
        player.setXRot(previousPitch);
        player.yHeadRot = previousHeadYaw;
        player.yHeadRotO = previousHeadYawOld;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int w = this.width;
        int controlTop = this.height - CONTROL_H;

        if (button == 0 && mouseX > LEFT_W && mouseX < w - RIGHT_W && mouseY >= 0 && mouseY < controlTop) {
            if (isOverResetCameraButton(mouseX, mouseY)) {
                resetPreviewCamera();
                return true;
            }
            if (isOverHelpButton(mouseX, mouseY)) {
                return true;
            }
            dragging = true;
            lastMX = (int) mouseX;
            lastMY = (int) mouseY;
            return true;
        }

        partPanel.mouseClicked(mouseX, mouseY, button);
        tintPanel.mouseClicked(mouseX, mouseY, button);
        texPanel.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            previewYaw += (mouseX - lastMX) * 1.5F;
            lastMX = (int) mouseX;
            lastMY = (int) mouseY;
            return true;
        }

        tintPanel.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        tintPanel.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        partPanel.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void selectType(PartsData.PartType type) {
        selectedType = type;
        partPanel.onPartTypeChanged(type);
        tintPanel.onPartTypeChanged(type);
        texPanel.onPartTypeChanged(type);
    }

    public void cycleSelectedType() {
        PartsData.PartType[] types = PartsData.PartType.values();
        int nextIndex = (selectedType.ordinal() + 1) % types.length;
        selectType(types[nextIndex]);
    }

    private void resetPreviewCamera() {
        previewYaw = 0.0F;
        previewPitch = 10.0F;
    }

    private void renderPreviewButtons(GuiGraphics graphics) {
        renderPreviewButton(graphics, getPreviewButtonX(), 22, Component.literal("R"));
        renderPreviewButton(graphics, getPreviewButtonX(), 4, Component.literal("?"));
    }

    private void renderPreviewButton(GuiGraphics graphics, int x, int y, Component label) {
        graphics.fill(x, y, x + PREVIEW_ICON_SIZE, y + PREVIEW_ICON_SIZE, 0xCC404040);
        graphics.renderOutline(x, y, PREVIEW_ICON_SIZE, PREVIEW_ICON_SIZE, 0xFFB8B8B8);
        graphics.drawCenteredString(this.font, label, x + PREVIEW_ICON_SIZE / 2, y + 3, 0xFFFFFF);
    }

    private void renderPreviewTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        if (isOverResetCameraButton(mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    Component.translatable("gui.button.reset.camera"),
                    mouseX,
                    mouseY);
        } else if (isOverHelpButton(mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    java.util.List.of(
                            Component.translatable("gui.button.help.camera.0"),
                            Component.translatable("gui.button.help.camera.1")),
                    java.util.Optional.empty(),
                    mouseX,
                    mouseY);
        }
    }

    private boolean isOverResetCameraButton(double mouseX, double mouseY) {
        return isInsidePreviewButton(mouseX, mouseY, getPreviewButtonX(), 22);
    }

    private boolean isOverHelpButton(double mouseX, double mouseY) {
        return isInsidePreviewButton(mouseX, mouseY, getPreviewButtonX(), 4);
    }

    private boolean isInsidePreviewButton(double mouseX, double mouseY, int buttonX, int buttonY) {
        return mouseX >= buttonX
                && mouseX < buttonX + PREVIEW_ICON_SIZE
                && mouseY >= buttonY
                && mouseY < buttonY + PREVIEW_ICON_SIZE;
    }

    private int getPreviewButtonX() {
        return this.width - RIGHT_W - PREVIEW_ICON_SIZE - 4;
    }

    private void onSave() {
        savedChanges = true;
        editingData.clearTextures();
        Tails.setLocalPartsData(editingData);

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            Tails.PROXY.addPartsData(player.getUUID(), editingData);
            TailsNetwork.CHANNEL.send(
                    PacketDistributor.SERVER.noArg(),
                    new PlayerDataPacket(player.getUUID(), editingData, false));
        }
        this.onClose();
    }

    private void onReset() {
        editingData.clearTextures();
        editingData = new PartsData();
        selectType(selectedType);
        refreshLivePreview();
    }

    public PartsData getEditingData() {
        return editingData;
    }

    public PartsData.PartType getSelectedPartType() {
        return selectedType;
    }

    public PartInfo getCurrentPartInfo() {
        return editingData.getPartInfo(selectedType);
    }

    public void setCurrentPartInfo(PartInfo info) {
        PartInfo currentInfo = editingData.getPartInfo(selectedType);
        if (currentInfo != null && currentInfo != info) {
            currentInfo.setTexture(null);
        }
        editingData.setPartInfo(selectedType, info);
        texPanel.onPartTypeChanged(selectedType);
        refreshLivePreview();
    }

    public void refreshLivePreview() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        Tails.PROXY.addPartsData(player.getUUID(), editingData);
    }

    @Override
    public void removed() {
        if (!savedChanges && !previewRestored) {
            previewRestored = true;
            editingData.clearTextures();

            Player player = Minecraft.getInstance().player;
            if (player != null) {
                Tails.PROXY.addPartsData(player.getUUID(), originalData.deepCopy());
            }
        }

        super.removed();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (tintPanel != null && tintPanel.keyPressed(key, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (tintPanel != null && tintPanel.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
