package kihira.tails.common;

import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

import java.util.Calendar;
import java.util.UUID;

public class LibraryEntryData {
    @Expose public PartsData partsData;
    @Expose public String entryName = "";
    @Expose public String comment = "";
    @Expose public UUID creatorUUID;
    @Expose public boolean favourite;
    @Expose public long creationDate;
    public GameProfile gameProfile;
    public boolean remoteEntry = false;

    public LibraryEntryData(UUID creatorUUID, String name, PartsData partsData) {
        this(name, partsData);
        this.creatorUUID = creatorUUID;
    }

    public LibraryEntryData(String name, PartsData partsData) {
        this.entryName = name;
        this.partsData = partsData;
        this.creationDate = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LibraryEntryData data = (LibraryEntryData) o;

        if (creationDate != data.creationDate) return false;
        if (favourite != data.favourite) return false;
        if (comment != null ? !comment.equals(data.comment) : data.comment != null) return false;
        if (!creatorUUID.equals(data.creatorUUID)) return false;
        if (entryName != null ? !entryName.equals(data.entryName) : data.entryName != null) return false;
        if (partsData != null ? !partsData.equals(data.partsData) : data.partsData != null) return false;

        return true;
    }

    @SideOnly(Side.CLIENT)
    public GameProfile getGameProfile() {
        if (gameProfile == null) {
            gameProfile = Minecraft.getMinecraft().func_152347_ac().fillProfileProperties(new GameProfile(creatorUUID, null), false);
        }
        return gameProfile;
    }

    @Override
    public int hashCode() {
        int result = partsData != null ? partsData.hashCode() : 0;
        result = 31 * result + (entryName != null ? entryName.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + creatorUUID.hashCode();
        result = 31 * result + (favourite ? 1 : 0);
        result = 31 * result + (int) (creationDate ^ (creationDate >>> 32));
        return result;
    }
}
