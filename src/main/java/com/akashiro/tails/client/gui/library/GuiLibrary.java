package com.akashiro.tails.client.gui.library;

import com.akashiro.tails.Tails;
import com.akashiro.tails.client.gui.GuiTailsEditor;
import com.akashiro.tails.common.LibraryManager;
import com.akashiro.tails.common.data.LibraryEntryData;
import com.akashiro.tails.common.data.PartsData;
import com.akashiro.tails.common.network.LibraryEntriesPacket;
import com.akashiro.tails.common.network.LibraryRequestPacket;
import com.akashiro.tails.common.network.TailsNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GuiLibrary extends Screen {

    private static final int LIST_WIDTH = 160;
    private static final int ROW_HEIGHT = 20;
    private static final int SELECTED_COL = 0xFF4477AA;
    private static final int HOVER_COL = 0x44AABBCC;
    private static final int FAV_COL = 0xFFFFAA00;

    private final PartsData editorSeedData;

    private List<LibraryEntryData> entries = new ArrayList<>();
    private int selectedIndex = -1;
    private int scrollOffset;

    private EditBox nameField;
    private EditBox importField;
    private boolean importMode;

    private Button btnApply;
    private Button btnDelete;
    private Button btnFavourite;
    private Button btnExport;
    private Button btnImport;
    private Button btnCreate;
    private Button btnRequestServer;

    public GuiLibrary() {
        this(Tails.localPartsData != null ? Tails.localPartsData.deepCopy() : new PartsData());
    }

    public GuiLibrary(PartsData editorSeedData) {
        super(Component.translatable("gui.button.mode.library"));
        this.editorSeedData = editorSeedData != null ? editorSeedData.deepCopy() : new PartsData();
    }

    @Override
    protected void init() {
        loadLocalEntries();

        int w = this.width;
        int h = this.height;
        int rightX = LIST_WIDTH + 12;
        int btnW = 80;

        btnApply = addRenderableWidget(Button.builder(
                        Component.translatable("gui.button.edit"),
                        btn -> applySelected())
                .bounds(rightX, 30, btnW, 18)
                .build());

        btnFavourite = addRenderableWidget(Button.builder(
                        Component.translatable("gui.button.favourite"),
                        btn -> toggleFavourite())
                .bounds(rightX, 52, btnW, 18)
                .build());

        btnDelete = addRenderableWidget(Button.builder(
                        Component.translatable("gui.button.delete"),
                        btn -> deleteSelected())
                .bounds(rightX, 74, btnW, 18)
                .build());

        btnExport = addRenderableWidget(Button.builder(
                        Component.translatable("gui.button.share"),
                        btn -> exportSelected())
                .bounds(rightX, 96, btnW, 18)
                .build());

        btnImport = addRenderableWidget(Button.builder(
                        Component.translatable("gui.library.import.string"),
                        btn -> toggleImportMode())
                .bounds(rightX, 118, btnW, 18)
                .build());

        btnRequestServer = addRenderableWidget(Button.builder(
                        Component.translatable("gui.button.upload"),
                        btn -> requestServerLibrary())
                .bounds(rightX, 140, btnW, 18)
                .build());
        btnRequestServer.active = Tails.hasRemote;

        addRenderableWidget(Button.builder(
                        Component.translatable("gui.button.mode.editor"),
                        btn -> openEditor(editorSeedData))
                .bounds(w - 90, h - 26, 86, 18)
                .build());

        nameField = new EditBox(this.font, 4, h - 26, LIST_WIDTH - 60, 18,
                Component.translatable("gui.library.entry.default"));
        nameField.setMaxLength(64);
        nameField.setValue(defaultEntryName());
        addRenderableWidget(nameField);

        btnCreate = addRenderableWidget(Button.builder(
                        Component.translatable("gui.library.create"),
                        btn -> createEntry())
                .bounds(LIST_WIDTH - 54, h - 26, 54, 18)
                .build());

        importField = new EditBox(this.font, rightX, 162, btnW, 18, CommonComponents.EMPTY);
        importField.setMaxLength(2048);
        importField.setVisible(false);
        addRenderableWidget(importField);

        updateButtonStates();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        graphics.fill(0, 0, LIST_WIDTH + 4, this.height, 0x88000000);
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.button.mode.library"),
                LIST_WIDTH / 2,
                4,
                0xFFFFFF);

        renderEntryList(graphics, mouseX, mouseY);
        renderInfoPanel(graphics);

        if (importMode) {
            graphics.drawString(this.font,
                    Component.translatable("gui.library.import.string"),
                    LIST_WIDTH + 12,
                    160,
                    0xFFFFFF,
                    false);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderEntryList(GuiGraphics graphics, int mouseX, int mouseY) {
        int listTop = 20;
        int listBottom = this.height - 32;

        for (int i = 0; i < entries.size(); i++) {
            int rowY = listTop + (i - scrollOffset) * ROW_HEIGHT;
            if (rowY < listTop || rowY + ROW_HEIGHT > listBottom) {
                continue;
            }

            LibraryEntryData entry = entries.get(i);
            boolean selected = i == selectedIndex;
            boolean hovered = mouseX >= 4 && mouseX < LIST_WIDTH && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;

            if (selected) {
                graphics.fill(4, rowY, LIST_WIDTH, rowY + ROW_HEIGHT, SELECTED_COL);
            } else if (hovered) {
                graphics.fill(4, rowY, LIST_WIDTH, rowY + ROW_HEIGHT, HOVER_COL);
            }

            if (entry.favourite) {
                graphics.drawString(this.font, "*", 6, rowY + 4, FAV_COL, false);
            }

            String displayName = entry.entryName != null ? entry.entryName : unnamedEntryName();
            graphics.drawString(this.font,
                    Component.literal(displayName),
                    entry.favourite ? 20 : 8,
                    rowY + 4,
                    0xFFFFFF,
                    false);

            if (entry.remoteEntry) {
                graphics.drawString(this.font, "[S]", LIST_WIDTH - 22, rowY + 4, 0xAAAAFF, false);
            }
        }

        if (entries.isEmpty()) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("gui.library.create"),
                    LIST_WIDTH / 2,
                    listTop + 20,
                    0x888888);
        }
    }

    private void renderInfoPanel(GuiGraphics graphics) {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) {
            return;
        }

        LibraryEntryData entry = entries.get(selectedIndex);
        int rightX = LIST_WIDTH + 12;

        if (entry.creatorName != null) {
            graphics.drawString(this.font,
                    Component.translatable("gui.library.info.created").append(": " + entry.creatorName),
                    rightX,
                    10,
                    0xAAAAAA,
                    false);
        }

        if (entry.creationDate > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(entry.creationDate);
            String date = String.format("%04d-%02d-%02d",
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));
            graphics.drawString(this.font,
                    Component.translatable("gui.library.info.createdate").append(": " + date),
                    rightX,
                    20,
                    0xAAAAAA,
                    false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int listTop = 20;
        int listBottom = this.height - 32;

        if (mouseX >= 4 && mouseX < LIST_WIDTH && mouseY >= listTop && mouseY < listBottom) {
            int clicked = scrollOffset + (int) ((mouseY - listTop) / ROW_HEIGHT);
            if (clicked >= 0 && clicked < entries.size()) {
                selectedIndex = clicked;
                if (button == 0) {
                    updateButtonStates();
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= 0 && mouseX < LIST_WIDTH + 4) {
            int maxScroll = Math.max(0, entries.size() - (this.height - 52) / ROW_HEIGHT);
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) delta));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void applySelected() {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) {
            return;
        }
        LibraryEntryData entry = entries.get(selectedIndex);
        if (entry.partsData == null) {
            return;
        }

        openEditor(entry.partsData);
    }

    private void toggleFavourite() {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) {
            return;
        }
        LibraryEntryData entry = entries.get(selectedIndex);
        if (entry.remoteEntry) {
            return;
        }

        entry.favourite = !entry.favourite;
        Tails.PROXY.getLibraryManager().saveLibrary();
    }

    private void deleteSelected() {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) {
            return;
        }
        LibraryEntryData entry = entries.get(selectedIndex);

        if (entry.remoteEntry) {
            List<LibraryEntryData> toDelete = new ArrayList<>();
            toDelete.add(entry);
            TailsNetwork.CHANNEL.send(PacketDistributor.SERVER.noArg(),
                    new LibraryEntriesPacket(toDelete, true));
        } else {
            Tails.PROXY.getLibraryManager().removeEntry(entry);
            Tails.PROXY.getLibraryManager().saveLibrary();
        }

        entries.remove(selectedIndex);
        selectedIndex = Math.min(selectedIndex, entries.size() - 1);
        updateButtonStates();
    }

    private void exportSelected() {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) {
            return;
        }
        LibraryEntryData entry = entries.get(selectedIndex);
        String json = Tails.GSON.toJson(entry);
        Minecraft.getInstance().keyboardHandler.setClipboard(json);
        showToast(Component.translatable("gui.library.info.toast.export"));
    }

    private void toggleImportMode() {
        importMode = !importMode;
        importField.setVisible(importMode);
        if (importMode) {
            importField.setFocused(true);
            importField.setValue("");
        } else {
            String text = importField.getValue().trim();
            if (!text.isEmpty()) {
                tryImport(text);
            }
        }
    }

    private void tryImport(String json) {
        try {
            LibraryEntryData entry = Tails.GSON.fromJson(json, LibraryEntryData.class);
            if (entry == null) {
                showToast(Component.translatable("gui.library.import.toast.invalid"));
                return;
            }
            if (entry.partsData == null) {
                showToast(Component.translatable("gui.library.import.toast.invalid.parts"));
                return;
            }

            Tails.PROXY.getLibraryManager().addEntry(entry);
            Tails.PROXY.getLibraryManager().saveLibrary();
            entries.add(entry);
            showToast(Component.translatable("gui.library.import.toast.success",
                    entry.entryName != null ? entry.entryName : "?"));
        } catch (Exception e) {
            showToast(Component.translatable("gui.library.import.toast.invalid"));
        }
    }

    private void requestServerLibrary() {
        if (!Tails.hasRemote) {
            return;
        }

        Tails.PROXY.getLibraryManager().removeRemoteEntries();
        entries.removeIf(entry -> entry.remoteEntry);
        TailsNetwork.CHANNEL.send(PacketDistributor.SERVER.noArg(), new LibraryRequestPacket());
    }

    private void createEntry() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        PartsData data = editorSeedData.deepCopy();
        String name = nameField.getValue().trim();
        if (name.isEmpty()) {
            name = defaultEntryName();
        }

        LibraryEntryData entry = new LibraryEntryData(
                mc.player.getUUID(),
                mc.player.getName().getString(),
                name,
                data);

        Tails.PROXY.getLibraryManager().addEntry(entry);
        Tails.PROXY.getLibraryManager().saveLibrary();
        entries.add(entry);
        selectedIndex = entries.size() - 1;
        nameField.setValue(defaultEntryName());
        updateButtonStates();
    }

    private void loadLocalEntries() {
        LibraryManager mgr = Tails.PROXY.getLibraryManager();
        entries = new ArrayList<>(mgr.getEntries());
        if (entries.isEmpty()) {
            entries = new ArrayList<>(mgr.loadLibrary());
        }
    }

    private void openEditor(PartsData data) {
        Tails.localPartsData = data != null ? data.deepCopy() : new PartsData();
        Minecraft.getInstance().setScreen(new GuiTailsEditor());
    }

    private static String defaultEntryName() {
        return Component.translatable("gui.library.entry.default").getString();
    }

    private static String unnamedEntryName() {
        return Component.translatable("gui.library.entry.unnamed").getString();
    }

    private void updateButtonStates() {
        boolean hasSelection = selectedIndex >= 0 && selectedIndex < entries.size();
        btnApply.active = hasSelection;
        btnDelete.active = hasSelection;
        btnFavourite.active = hasSelection;
        btnExport.active = hasSelection;
    }

    private void showToast(Component message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(message, true);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
