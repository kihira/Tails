// 경로: src/main/java/com/akashiro/tails/common/data/LibraryEntryData.java
package com.akashiro.tails.common.data;

import com.google.gson.annotations.Expose;

import java.util.Calendar;
import java.util.UUID;

/**
 * 라이브러리에 저장되는 하나의 엔트리 (파츠 세트 + 메타정보).
 * tailslibrary.json 파일에 Gson으로 직렬화됨.
 */
public class LibraryEntryData {

    @Expose
    public PartsData partsData;

    @Expose
    public String entryName;

    @Expose
    public String comment;

    @Expose
    public boolean favourite;

    @Expose
    public long creationDate;

    @Expose
    public UUID creatorUUID;

    @Expose
    public String creatorName;

    /** 서버에서 받아온 원격 엔트리인지 여부 (로컬 저장 안 함) */
    @Expose
    public boolean remoteEntry;

    public LibraryEntryData() {}

    public LibraryEntryData(UUID creatorUUID, String creatorName,
                            String entryName, PartsData partsData) {
        this.creatorUUID  = creatorUUID;
        this.creatorName  = creatorName;
        this.entryName    = entryName;
        this.partsData    = partsData;
        this.creationDate = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryEntryData d = (LibraryEntryData) o;
        return entryName != null && entryName.equals(d.entryName)
                && creatorUUID != null && creatorUUID.equals(d.creatorUUID);
    }

    @Override
    public int hashCode() {
        int result = entryName != null ? entryName.hashCode() : 0;
        result = 31 * result + (creatorUUID != null ? creatorUUID.hashCode() : 0);
        return result;
    }
}