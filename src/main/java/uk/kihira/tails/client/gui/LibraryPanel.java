package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LibraryPanel extends Panel<GuiEditor> implements IListCallback<LibraryListEntry>
{
    private static final LibrarySorter SORTER = new LibrarySorter();

    private GuiList<LibraryListEntry> list;
    private TextFieldWidget searchField;

    public LibraryPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    public void init()
    {
        initList();

        this.addButton(new ExtendedButton(3, this.height - 18, this.width - 6, 15, new TranslationTextComponent("gui.button.all"), this::onAllButtonPressed));
        this.searchField = new TextFieldWidget(this.font, 5, this.height - 31, this.width - 10, 10, StringTextComponent.EMPTY);
        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), -100, 0, 0, this.width, this.height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);

        // todo render call still needed?
        this.searchField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.list.render(matrixStack, mouseX, mouseY, partialTicks);

        getMinecraft().textureManager.bindTexture(IconButton.ICONS_TEXTURES);
        matrixStack.push();
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        matrixStack.translate(this.width - 16, this.height - 32, 0);
        matrixStack.scale(.75f, .75f, 0f);
        GuiUtils.drawTexturedModalRect(matrixStack, 0, 0, 160, 0, 16, 16, 0);
        matrixStack.pop();

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void onAllButtonPressed(Button button)
    {
        // todo?
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        this.list.mouseClicked(mouseX, mouseY, mouseButton);
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        this.list.mouseReleased(mouseX, mouseY, mouseButton);
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        this.searchField.charTyped(codePoint, codePoint);
        if (this.searchField.getVisible() && this.searchField.isFocused())
        {
            List<LibraryListEntry> newEntries = filterListEntries(this.searchField.getText().toLowerCase());
            newEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));
            // todo
            ///this.list.getEntries().clear();
            //this.list.getEntries().addAll(newEntries);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean onEntrySelected(GuiList<LibraryListEntry> guiList, int index, LibraryListEntry entry)
    {
        if (!(entry instanceof LibraryListEntry.NewLibraryListEntry))
        {
            this.parent.libraryInfoPanel.setEntry(entry);
            this.parent.setOutfit(entry.data.outfit);
        }
        return true;
    }

    public void initList()
    {
        List<LibraryListEntry> libraryEntries = new ArrayList<>();
        for (LibraryEntryData data : Tails.proxy.getLibraryManager().libraryEntries)
        {
            libraryEntries.add(new LibraryListEntry(data));
        }

        //Add in new entry creation
        libraryEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));

        libraryEntries.sort(SORTER);

        this.list = new GuiList<>(this, this.width, this.height - 34, 0, this.height - 34, 50, libraryEntries);
    }

    public void addSelectedEntry(LibraryListEntry entry)
    {
        // todo
        //this.list.getEntries().add(entry);
        //this.list.setCurrentIndex(this.list.getEntries().size() - 1);
        this.parent.libraryInfoPanel.setEntry(entry);
    }

    public void removeEntry(LibraryListEntry entry)
    {
        Tails.proxy.getLibraryManager().removeEntry(entry.data);
        //todo this.list.getEntries().remove(entry);
    }

    private List<LibraryListEntry> filterListEntries(String filter)
    {
        ArrayList<LibraryListEntry> filteredEntries = new ArrayList<>();
        List<LibraryListEntry> entries = new ArrayList<>();

        for (LibraryEntryData data : Tails.proxy.getLibraryManager().libraryEntries) {
            entries.add(new LibraryListEntry(data));
        }

        for (LibraryListEntry entry : entries) {
            if (entry instanceof LibraryListEntry.NewLibraryListEntry || entry.data.entryName.toLowerCase().contains(filter)) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    @Override
    public void onClose()
    {
        Tails.proxy.getLibraryManager().removeRemoteEntries();
        super.onClose();
    }

    private static class LibrarySorter implements Comparator<LibraryListEntry>
    {

        @Override
        public int compare(LibraryListEntry entry1, LibraryListEntry entry2)
        {
            if (entry1.equals(entry2))
            {
                return 0;
            }

            // Always ensure new library entry button is at the top
            if (entry1 instanceof LibraryListEntry.NewLibraryListEntry)
            {
                return Integer.MIN_VALUE;
            }
            else if (entry2 instanceof LibraryListEntry.NewLibraryListEntry)
            {
                return Integer.MAX_VALUE;
            }

            //Put favourites at the top
            if (entry1.data.favourite && !entry2.data.favourite)
            {
                return -1;
            }
            else if (!entry1.data.favourite && entry2.data.favourite)
            {
                return 1;
            }

            return 0;
        }
    }
}
