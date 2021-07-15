package com.example.smartalarmclock.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity
public class NotifyEntity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "notify_id")
    private Long notifyID;

    @ColumnInfo(name = "notify_title")
    private String notify_title;

    @ColumnInfo(name = "notify_text", typeAffinity = ColumnInfo.TEXT)
    private String notify_text;

    @Ignore
    public NotifyEntity(String title, String text)
    {
        notifyID = UUID.randomUUID().getMostSignificantBits();
        notify_title = title;
        notify_text = text;
    }

    public NotifyEntity(@NonNull Long id, String title, String text)
    {
        this.notifyID = id;
        this.notify_title = title;
        this.notify_text = text;
    }

    public NotifyEntity()
    {
        this.notifyID = new Long(0);
        this.notify_title = "";
        this.notify_text = "";
    }

    public String getNotify_title() {
        return notify_title;
    }

    public String getNotify_text() {
        return notify_text;
    }

    public Long getNotifyID() { return notifyID; }

    public void setNotifyID(@NonNull Long notifyID) {
        this.notifyID = notifyID;
    }

    public void setNotify_title(String notify_title) {
        this.notify_title = notify_title;
    }

    public void setNotify_text(String notify_text) {
        this.notify_text = notify_text;
    }
}
