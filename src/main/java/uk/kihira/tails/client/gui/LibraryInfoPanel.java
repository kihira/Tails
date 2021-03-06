package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
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
        textField = new TextFieldWidget(this.font, 6, 6, width - 12, 15, StringTextComponent.EMPTY);
        textField.setMaxStringLength(16);

        addListener(textField);

        buttons.add(favButton = new IconButton.IconToggleButton(5, height - 20, IconButton.Icons.STAR, this::onFavouriteButtonPressed, I18n.format("gui.button.favourite")));
        buttons.add(new IconButton(21, height - 20, IconButton.Icons.DELETE, this::onDeleteButtonPressed, I18n.format("gui.button.delete")));
        buttons.add(new IconButton(53, height - 20, IconButton.Icons.DOWNLOAD, this::onDownloadButtonPressed, I18n.format("gui.button.savelocal")));
        buttons.add(new IconButton(68, height - 20, IconButton.Icons.EXPORT, this::onExportButtonPressed, I18n.format("gui.button.share")));
        super.init();

        //Only request library if on remote server
        if (!getMinecraft().isIntegratedServerRunning())
        {
            TailsPacketHandler.networkWrapper.sendToServer(new LibraryRequestMessage());
        }

        setEntry(null);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, 0, 0, width, height, 0xCC000000, 0xCC000000);

        GlStateManager.color4f(0F, 0F, 0F, 0F);

        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 10, 3, 3, width - 3, height - 3, 0xFF000000, 0xFF000000);

        if (entry != null)
        {
            // todo this.font.setUnicodeFlag(true);
            this.font.drawString(matrixStack, I18n.format("gui.library.info.created") + ":", 5, height - 40, 0xAAAAAA);
            this.font.drawString(matrixStack, entry.data.creatorName, width - 5 - this.font.getStringWidth(entry.data.creatorName), height - 40, 0xAAAAAA);
            this.font.drawString(matrixStack, I18n.format("gui.library.info.createdate") + ":", 5, height - 32, 0xAAAAAA);
            String date = new SimpleDateFormat("dd/MM/YY").format(new Date(entry.data.creationDate));
            this.font.drawString(matrixStack, date, width - 5 - this.font.getStringWidth(date), height - 32, 0xAAAAAA);
            //fontRenderer.drawSplitString(EnumChatFormatting.ITALIC + entry.data.comment, 5, 40, width, 0xFFFFFF);
            //this.font.setUnicodeFlag(false);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void onFavouriteButtonPressed(Button button)
    {
        this.entry.data.favourite = ((IconButton.IconToggleButton) button).toggled;

        Tails.proxy.getLibraryManager().saveLibrary();
    }

    private void onDeleteButtonPressed(Button button)
    {
        if (this.entry.data.remoteEntry)
        {
            //Only allow removing if player owns the entry
            if (!this.entry.data.creatorUUID.equals(getMinecraft().player.getUniqueID()))
            {
                return;
            }
        }
        // todo ((IconButton) button).setHover(false);
        this.parent.libraryPanel.removeEntry(entry);
        setEntry(null);

        Tails.proxy.getLibraryManager().saveLibrary();
    }

    private void onDownloadButtonPressed(Button button)
    {
        this.entry.data.remoteEntry = false;
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
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if (this.textField.isFocused() && this.entry != null)
        {
            this.entry.data.entryName = textField.getText();
            Tails.proxy.getLibraryManager().saveLibrary();
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose()
    {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        super.onClose();
    }

    public void setEntry(@Nullable LibraryListEntry entry)
    {
        this.entry = entry;
        if (entry == null)
        {
            this.textField.setVisible(false);
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

                // todo
//                if (widget.id == 1 && entry.data.remoteEntry && !entry.data.creatorUUID.equals(this.minecraft.player.getUniqueID()))
//                {
//                    widget.visible = false;
//                }
//                //Download
//                else if (widget.id == 3 && !entry.data.remoteEntry)
//                {
//                    widget.visible = false;
//                }
//                //Upload
//                else if (widget.id == 2 && (entry.data.remoteEntry || mc.isSingleplayer() || !Tails.hasRemote))
//                {
//                    widget.visible = false;
//                }
            }
        }
    }

    public LibraryListEntry getEntry() {
        return this.entry;
    }
}
