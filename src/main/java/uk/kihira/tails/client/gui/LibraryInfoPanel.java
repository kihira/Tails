package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.LibraryRequestMessage;
import uk.kihira.tails.common.network.TailsPacketHandler;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class LibraryInfoPanel extends Panel<GuiEditor>
{
    private LibraryListEntry entry;
    private TextFieldWidget textField;
    private IconButton.IconToggleButton favButton;

    public LibraryInfoPanel(GuiEditor parent, int left, int top, int width, int height)
    {
        super(parent, left, top, width, height);
        Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
    }

    @Override
    public void init()
    {
        textField = new TextFieldWidget(this.font, 6, 6, width - 12, 15);
        textField.setMaxStringLength(16);

        addListener(textField);

        buttons.add(favButton = new IconButton.IconToggleButton(0, 5, height - 20, IconButton.Icons.STAR, this::onFavouriteButtonPressed, I18n.format("gui.button.favourite")));
        buttons.add(new IconButton(1, 21, height - 20, IconButton.Icons.DELETE, this::onDeleteButtonPressed, I18n.format("gui.button.delete")));
        buttons.add(new IconButton(3, 53, height - 20, IconButton.Icons.DOWNLOAD, this::onDownloadButtonPressed, I18n.format("gui.button.savelocal")));
        buttons.add(new IconButton(4, 68, height - 20, IconButton.Icons.EXPORT, this::onExportButtonPressed, I18n.format("gui.button.share")));
        super.init();

        //Only request library if on remote server
        if (!this.minecraft.isIntegratedServerRunning()) {
            TailsPacketHandler.networkWrapper.sendToServer(new LibraryRequestMessage());
        }

        setEntry(null);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        zLevel = 0;
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);

        GlStateManager.color4f(0F, 0F, 0F, 0F);

        zLevel = 10;
        drawGradientRect(3, 3, width - 3, height - 3, 0xFF000000, 0xFF000000);

        if (entry != null)
        {
            this.font.setUnicodeFlag(true);
            this.font.drawString(matrixStack, I18n.format("gui.library.info.created") + ":", 5, height - 40, 0xAAAAAA);
            this.font.drawString(matrixStack, entry.data.creatorName, width - 5 - this.font.getStringWidth(entry.data.creatorName), height - 40, 0xAAAAAA);
            this.font.drawString(matrixStack, I18n.format("gui.library.info.createdate") + ":", 5, height - 32, 0xAAAAAA);
            String date = new SimpleDateFormat("dd/MM/YY").format(new Date(entry.data.creationDate));
            this.font.drawString(matrixStack, date, width - 5 - this.font.getStringWidth(date), height - 32, 0xAAAAAA);
            //fontRenderer.drawSplitString(EnumChatFormatting.ITALIC + entry.data.comment, 5, 40, width, 0xFFFFFF);
            this.font.setUnicodeFlag(false);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void onFavouriteButtonPressed(Button button)
    {
        entry.data.favourite = ((IconButton.IconToggleButton) button).toggled;

        Tails.proxy.getLibraryManager().saveLibrary();
    }

    private void onDeleteButtonPressed(Button button)
    {
        if (entry.data.remoteEntry) {
            //Only allow removing if player owns the entry
            if (!entry.data.creatorUUID.equals(this.minecraft.player.getUniqueID())) {
                return;
            }
        }
        ((IconButton) button).setHover(false);
        parent.libraryPanel.removeEntry(entry);
        setEntry(null);

        Tails.proxy.getLibraryManager().saveLibrary();
    }

    private void onDownloadButtonPressed(Button button)
    {
        entry.data.remoteEntry = false;
        button.active = false;

        Tails.proxy.getLibraryManager().saveLibrary();
    }

    private void onExportButtonPressed(Button button)
    {
        StringBuilder sb = new StringBuilder();
        LibraryEntryData libData = parent.libraryInfoPanel.getEntry().data;
        sb.append(libData.entryName).append(":");
        sb.append(libData.creatorUUID).append(":");
        sb.append(Tails.GSON.toJson(libData.outfit));

        ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, I18n.format("gui.library.info.toast.export"));
        Minecraft.getInstance().keyboardListener.setClipboardString(sb.toString());

        Tails.proxy.getLibraryManager().saveLibrary();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char key, int keyCode)
    {
        if (textField.isFocused() && entry != null)
        {
            entry.data.entryName = textField.getText();
            Tails.proxy.getLibraryManager().saveLibrary();
        }
        super.keyTyped(key, keyCode);
    }

    @Override
    public void onClose()
    {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        super.onClose();
    }

    public void setEntry(@Nullable LibraryListEntry entry)
    {
        this.entry = entry;
        if (entry == null)
        {
            textField.setVisible(false);
            for (Widget widget : this.buttons)
            {
                widget.visible = false;
            }
        }
        else
        {
            this.favButton.toggled = entry.data.favourite;
            this.textField.setVisible(true);
            this.textField.setText(entry.data.entryName);
            for (Widget widget : this.buttons)
            {
                widget.visible = true;

                if (widget.id == 1 && entry.data.remoteEntry && !entry.data.creatorUUID.equals(this.minecraft.player.getUniqueID()))
                {
                    widget.visible = false;
                }
                //Download
                else if (widget.id == 3 && !entry.data.remoteEntry)
                {
                    widget.visible = false;
                }
                //Upload
                else if (widget.id == 2 && (entry.data.remoteEntry || mc.isSingleplayer() || !Tails.hasRemote))
                {
                    widget.visible = false;
                }
            }
        }
    }

    public LibraryListEntry getEntry() {
        return this.entry;
    }
}
