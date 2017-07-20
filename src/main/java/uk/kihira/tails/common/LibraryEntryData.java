package uk.kihira.tails.common;

import com.google.gson.annotations.Expose;

import java.util.Calendar;
import java.util.UUID;

public class LibraryEntryData {
    @Expose public final Outfit outfit;
    @Expose public String entryName = "";
    @Expose public final String comment = "";
    @Expose public boolean favourite;
    @Expose public final long creationDate;
    @Expose public final UUID creatorUUID;
    /**
     * Name is used purely for display purposes.
     */
    @Expose public String creatorName;
    public boolean remoteEntry = false;

    public LibraryEntryData(UUID creatorUUID, String creatorName, String name, Outfit outfit) {
        this.entryName = name;
        this.outfit = outfit;
        this.creationDate = Calendar.getInstance().getTimeInMillis();
        this.creatorUUID = creatorUUID;
        this.creatorName = creatorName;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LibraryEntryData data = (LibraryEntryData) o;

        if (creationDate != data.creationDate) return false;
        if (favourite != data.favourite) return false;
        if (!comment.equals(data.comment)) return false;
        if (!creatorUUID.equals(data.creatorUUID)) return false;
        if (entryName != null ? !entryName.equals(data.entryName) : data.entryName != null) return false;
        if (outfit != null ? !outfit.equals(data.outfit) : data.outfit != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = outfit != null ? outfit.hashCode() : 0;
        result = 31 * result + (entryName != null ? entryName.hashCode() : 0);
        result = 31 * result + comment.hashCode();
        result = 31 * result + creatorUUID.hashCode();
        result = 31 * result + (favourite ? 1 : 0);
        result = 31 * result + (int) (creationDate ^ (creationDate >>> 32));
        return result;
    }
}
