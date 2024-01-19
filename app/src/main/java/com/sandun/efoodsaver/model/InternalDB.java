package com.sandun.efoodsaver.model;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

public class InternalDB<T extends RoomDatabase> {
    private Context context;
    private String dbName;
    private Class<T> db;

    public InternalDB(Context context, Class<T> dbClass) {
        this.context = context;
        db = dbClass;
    }

    public T build() {
        return Room.databaseBuilder(context, db, "app_db").build();
    }
}
