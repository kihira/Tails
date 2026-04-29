// 경로: src/main/java/com/akashiro/tails/common/LibraryManager.java
package com.akashiro.tails.common;

import com.akashiro.tails.Tails;
import com.akashiro.tails.common.data.LibraryEntryData;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 라이브러리 엔트리 목록을 tailslibrary.json 파일에 저장/로드.
 * 1.12.2 LibraryManager 이식.
 */
public class LibraryManager {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final List<LibraryEntryData> libraryEntries = new ArrayList<>();

    public List<LibraryEntryData> loadLibrary() {
        File file = getLibraryFile();
        if (!file.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<LibraryEntryData>>(){}.getType();
            List<LibraryEntryData> loaded = Tails.GSON.fromJson(reader, type);
            if (loaded != null) {
                libraryEntries.addAll(loaded);
                // PartsData 참조 복원 (역직렬화 후 연결)
                for (LibraryEntryData entry : libraryEntries) {
                    if (entry.partsData == null) {
                        LOGGER.warn("Library entry '{}' has null partsData", entry.entryName);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // 파일 없음 — 무시
        } catch (Exception e) {
            LOGGER.catching(e);
        }
        return new ArrayList<>(libraryEntries);
    }

    public void saveLibrary() {
        File file = getLibraryFile();
        try (FileWriter writer = new FileWriter(file)) {
            // remoteEntry는 저장하지 않음
            List<LibraryEntryData> toSave = new ArrayList<>();
            for (LibraryEntryData e : libraryEntries) {
                if (!e.remoteEntry) toSave.add(e);
            }
            Tails.GSON.toJson(toSave, writer);
        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    public void addEntries(List<? extends LibraryEntryData> entries) {
        libraryEntries.addAll(entries);
    }

    public void addEntry(LibraryEntryData data) {
        libraryEntries.add(data);
    }

    public void removeEntry(LibraryEntryData data) {
        libraryEntries.remove(data);
    }

    public void removeRemoteEntries() {
        libraryEntries.removeIf(e -> e.remoteEntry);
    }

    public List<LibraryEntryData> getEntries() {
        return libraryEntries;
    }

    private File getLibraryFile() {
        File configDir = new File("config");
        if (!configDir.exists()) configDir.mkdirs();
        File file = new File(configDir, "tailslibrary.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LOGGER.error("Failed to create a library file!", e);
            }
        }
        return file;
    }
}