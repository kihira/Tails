package kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kihira.tails.client.gui.GuiEditor;
import kihira.tails.common.network.LibraryEntriesMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LibraryManager {

    public final List<LibraryEntryData> libraryEntries;

    public LibraryManager() {
        libraryEntries = loadLibrary();
    }

    /**
     * Adds the entries and saves
     * @param entries The entries to add
     */
    public void addEntries(List<? extends LibraryEntryData> entries) {
        libraryEntries.addAll(entries);
    }

    public void addEntry(LibraryEntryData data) {
        libraryEntries.add(data);
    }

    public void removeEntry(LibraryEntryData data) {
        libraryEntries.remove(data);
    }

    /**
     * Removes remote entries from the list
     */
    public void removeRemoteEntries() {
        Iterator<LibraryEntryData> iterator = libraryEntries.iterator();
        while (iterator.hasNext()) {
            LibraryEntryData entry = iterator.next();
            if (entry.remoteEntry) {
                iterator.remove();
            }
        }
    }

    /**
     * Loads the library of entries from disk
     */
    private List<LibraryEntryData> loadLibrary() {
        Gson gson = Tails.gson;
        ArrayList<LibraryEntryData> libraryEntries = new ArrayList<LibraryEntryData>();
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(getLibraryFile());
            List<LibraryEntryData> loadedEntries = gson.fromJson(fileReader, new TypeToken<List<LibraryEntryData>>() {}.getType());
            if (loadedEntries != null && loadedEntries.size() > 0) {
                for (LibraryEntryData libEntry : loadedEntries) {
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
        List<LibraryEntryData> entries = new ArrayList<LibraryEntryData>();
        FileWriter fileWriter = null;

        //Remove remote entries before saving
        for (LibraryEntryData libraryListEntry : this.libraryEntries) {
            if (!libraryListEntry.remoteEntry) {
                entries.add(libraryListEntry);
            }
        }

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
        File libraryFile = new File("tailslibrary.json");

        if (!libraryFile.exists()) {
            try {
                libraryFile.createNewFile();
            } catch (IOException e) {
                Tails.logger.error("Failed to create a library file!", e);
            }
        }
        return libraryFile;
    }

    public static class ClientLibraryManager extends LibraryManager {

        @Override
        public void addEntries(List<? extends LibraryEntryData> entries) {
            super.addEntries(entries);
            GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;

            if (guiScreen instanceof GuiEditor) {
                GuiEditor editor = (GuiEditor) guiScreen;
                if (editor.libraryPanel != null && editor.libraryInfoPanel != null)
                ((GuiEditor) guiScreen).libraryPanel.initList();
                ((GuiEditor) guiScreen).libraryInfoPanel.setEntry(null);
            }
        }

        @Override
        public void removeEntry(final LibraryEntryData data) {
            if (data.remoteEntry) {
                Tails.networkWrapper.sendToServer(new LibraryEntriesMessage(new ArrayList<LibraryEntryData>() {{ add(data); }}, true));
            }
            else {
                super.removeEntry(data);
            }
        }
    }
}
