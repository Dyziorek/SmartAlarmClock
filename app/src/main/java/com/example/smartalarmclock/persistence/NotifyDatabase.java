package com.example.smartalarmclock.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NotifyEntity.class}, version = 1, exportSchema = false)
public abstract class NotifyDatabase extends RoomDatabase {

    private static volatile NotifyDatabase INSTANCE;

    public abstract NotifyDao notifyDao();

    public static NotifyDatabase getInstance(Context ctx)
    {
        if (INSTANCE == null)
        {
            synchronized (NotifyDatabase.class)
            {
                if(INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(), NotifyDatabase.class, "Notify.db").build();
                }
            }
        }
        return  INSTANCE;
    }
}
