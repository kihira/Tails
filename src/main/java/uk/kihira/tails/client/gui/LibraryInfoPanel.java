package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.OutfitManager;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

@OnlyIn(Dist.CLIENT)
public class LibraryInfoPanel extends Panel<GuiEditor> {

    private LibraryListEntry entry;

    private GuiTextField textField;
    private GuiIconButton.GuiIconToggleButton favButton;

    public LibraryInfoPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
        Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
    }

    @Override
    public void initGui() {
        textField = new GuiTextField(-1, fontRenderer, 6, 6, width - 12, 15);
        textField.setMaxStringLength(16);

        buttons.add(favButton = new GuiIconButton.GuiIconToggleButton(0, 5, height - 20, GuiIconButton.Icons.STAR, I18n.format("gui.button.favourite")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);

                entry.data.favourite = toggled;
            }
        });
        buttons.add(new GuiIconButton(1, 21, height - 20, GuiIconButton.Icons.DELETE, I18n.format("gui.button.delete")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);

                if (entry.data.remoteEntry) {
                    //Only allow removing if player owns the entry
                    if (!entry.data.creatorUUID.equals(mc.player.getUniqueID())) {
                        return;
                    }
                }
                setHover(false);
                parent.libraryPanel.removeEntry(entry);
                setEntry(null);
            }
        });
        buttons.add(new GuiIconButton(3, 53, height - 20, GuiIconButton.Icons.DOWNLOAD, I18n.format("gui.button.savelocal")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);

                entry.data.remoteEntry = false;
                enabled = false;
            }
        });
        buttons.add(new GuiIconButton(4, 68, height - 20, GuiIconButton.Icons.EXPORT, I18n.format("gui.button.share")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);

                StringBuilder sb = new StringBuilder();
                LibraryEntryData libData = parent.libraryInfoPanel.getEntry().data;
                sb.append(libData.entryName).append(":");
                sb.append(libData.creatorUUID).append(":");
                sb.append(Tails.gson.toJson(libData.outfit));

                ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, I18n.format("gui.library.info.toast.export"));
                Minecraft.getInstance().keyboardListener.setClipboardString(sb.toString());
            }
        });
        super.initGui();

        setEntry(null);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        zLevel = 0;
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);

        GlStateManager.color4f(0f, 0f, 0f, 0f);

        zLevel = 10;
        drawGradientRect(3, 3, width - 3, height - 3, 0xFF000000, 0xFF000000);

        if (entry != null) {
            textField.drawTextField(mouseX, mouseY, partialTicks);

            fontRenderer.setUnicodeFlag(true);
            fontRenderer.drawString(I18n.format("gui.library.info.created") + ":", 5, height - 40, 0xAAAAAA);
            fontRenderer.drawString(entry.data.creatorName, width - 5 - fontRenderer.getStringWidth(entry.data.creatorName), height - 40, 0xAAAAAA);
            fontRenderer.drawString(I18n.format("gui.library.info.createdate") + ":", 5, height - 32, 0xAAAAAA);
            String date = new SimpleDateFormat("dd/MM/YY").format(new Date(entry.data.creationDate));
            fontRenderer.drawString(date, width - 5 - fontRenderer.getStringWidth(date), height - 32, 0xAAAAAA);
            //fontRenderer.drawSplitString(EnumChatFormatting.ITALIC + entry.data.comment, 5, 40, width, 0xFFFFFF);
            fontRenderer.setUnicodeFlag(false);
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (textField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) && entry != null) {
            entry.data.entryName = textField.getText();
            OutfitManager.INSTANCE.getLibraryManager().saveLibrary();
            return true;
        }

        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public void onGuiClosed() {
        Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
        super.onGuiClosed();
    }

    public void setEntry(@Nullable LibraryListEntry entry) {
        this.entry = entry;
        if (entry == null) {
            textField.setVisible(false);
            for (Object button : buttons) {
                ((GuiButton) button).visible = false;
            }
        }
        else {
            favButton.toggled = entry.data.favourite;
            textField.setVisible(true);
            textField.setText(entry.data.entryName);
            for (Object obj : buttons) {
                GuiButton button = (GuiButton) obj;
                button.visible = true;

                if (button.id == 1 && entry.data.remoteEntry && !entry.data.creatorUUID.equals(mc.player.getUniqueID())) {
                    button.visible = false;
                }
                //Download
                else if (button.id == 3 && !entry.data.remoteEntry) {
                    button.visible = false;
                }
                //Upload
                else if (button.id == 2 && (entry.data.remoteEntry || mc.isSingleplayer() || !Tails.hasRemote)) {
                    button.visible = false;
                }
            }
        }
    }

    public LibraryListEntry getEntry() {
        return this.entry;
    }
}
