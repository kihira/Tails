package kihira.tails.client.gui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.client.config.GuiButtonExt;
import kihira.foxlib.client.gui.GuiIconButton;
import kihira.foxlib.client.gui.GuiList;
import kihira.foxlib.client.gui.IListCallback;
import kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LibraryPanel extends Panel<GuiEditor> implements IListCallback<LibraryEntry> {

    private static final LibrarySorter sorter = new LibrarySorter();
    private GuiList<LibraryEntry> list;
    private List<LibraryEntry> libraryEntries;
    private GuiTextField searchField;

    public LibraryPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        libraryEntries = loadLibrary();
        initList();

        searchField = new GuiTextField(fontRendererObj, 5, height - 31, width - 10, 10);
        buttonList.add(new GuiButtonExt(0, 3, height - 18, width - 6, 15, "All"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        zLevel = -100;
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);
        GL11.glColor4f(1, 1, 1, 1);

        searchField.drawTextBox();
        list.drawScreen(mouseX, mouseY, p_73863_3_);

        zLevel = 0;
        Minecraft.getMinecraft().renderEngine.bindTexture(GuiIconButton.iconsTextures);
        GL11.glPushMatrix();
        GL11.glTranslatef(width - 16, height - 32, 0);
        GL11.glScalef(0.75F, 0.75F, 0F);
        drawTexturedModalRect(0, 0, 160, 0, 16, 16);
        GL11.glPopMatrix();

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {

        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        list.func_148179_a(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    public void keyTyped(char key, int keycode) {
        searchField.textboxKeyTyped(key, keycode);
        if (searchField.getVisible() && searchField.isFocused()) {
            //TODO update library list.
            List<LibraryEntry> newEntries = filterListEntries(libraryEntries, searchField.getText().toLowerCase());
            newEntries.add(0, new LibraryEntry.NewLibraryEntry(this, null));
            list.getEntries().clear();
            list.getEntries().addAll(newEntries);
        }
        super.keyTyped(key, keycode);
    }

    @Override
    public boolean onEntrySelected(GuiList guiList, int index, LibraryEntry entry) {
        parent.libraryInfoPanel.setEntry(entry);
        if (!(entry instanceof LibraryEntry.NewLibraryEntry)) {
            Tails.localPartsData = entry.partsData;
            parent.setPartsData(entry.partsData);
        }
        saveLibrary();
        return true;
    }

    private void initList() {
        List<LibraryEntry> libraryEntries = new ArrayList<LibraryEntry>(this.libraryEntries);

        //Add in new entry creation
        libraryEntries.add(0, new LibraryEntry.NewLibraryEntry(this, null));

        Collections.sort(libraryEntries, sorter);

        list = new GuiList<LibraryEntry>(this, width, height - 34, 0, height - 34, 40, libraryEntries);
    }

    public void addSelectedEntry(LibraryEntry entry) {
        list.getEntries().add(entry);
        libraryEntries.add(entry);
        list.setCurrrentIndex(list.getEntries().size() - 1);
        parent.libraryInfoPanel.setEntry(entry);
    }

    public void removeEntry(LibraryEntry entry) {
        libraryEntries.remove(entry);
        list.getEntries().remove(entry);
    }

    /**
     * Loads the library of entries from disk
     */
    private List<LibraryEntry> loadLibrary() {
        Gson gson = Tails.gson;
        ArrayList<LibraryEntry> libraryEntries = new ArrayList<LibraryEntry>();
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(getLibraryFile());
            List<LibraryEntry> loadedEntries = gson.fromJson(fileReader, new TypeToken<List<LibraryEntry>>() {}.getType());
            if (loadedEntries != null && loadedEntries.size() > 0) {
                for (LibraryEntry libEntry : loadedEntries) {
                    if (libEntry.partsData != null) {
                        libraryEntries.add(libEntry);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            Tails.logger.catching(e);
        } finally {
            IOUtils.closeQuietly(fileReader);
        }
        return libraryEntries;
    }

    /**
     * Saves the library to disk
     */
    public void saveLibrary() {
        List<LibraryEntry> entries = this.libraryEntries;
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(getLibraryFile());
            Tails.gson.toJson(entries, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }

    private File getLibraryFile() {
        File libraryFile = new File(mc.mcDataDir, "tailslibrary.json");

        if (!libraryFile.exists()) {
            try {
                libraryFile.createNewFile();
            } catch (IOException e) {
                Tails.logger.error("Failed to create a library file!", e);
            }
        }
        return libraryFile;
    }

    private List<LibraryEntry> filterListEntries(List<LibraryEntry> entries, String filter) {
        ArrayList<LibraryEntry> filteredEntries = new ArrayList<LibraryEntry>();
        for (LibraryEntry entry : entries) {
            if (entry instanceof LibraryEntry.NewLibraryEntry || entry.entryName.toLowerCase().contains(filter)) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    private static class LibrarySorter implements Comparator<LibraryEntry> {

        @Override
        public int compare(LibraryEntry entry1, LibraryEntry entry2) {
            if (entry1.equals(entry2)) {
                return 0;
            }

            if (entry1 instanceof LibraryEntry.NewLibraryEntry) {
                return Integer.MIN_VALUE;
            }
            else if (entry2 instanceof LibraryEntry.NewLibraryEntry) {
                return Integer.MAX_VALUE;
            }

            if (entry1.favourite && !entry2.favourite) {
                return -1;
            }
            else if (!entry1.favourite && entry2.favourite) {
                return 1;
            }

            return 0;
        }
    }
}
