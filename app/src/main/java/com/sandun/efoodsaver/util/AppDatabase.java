package com.sandun.efoodsaver.util;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.sandun.efoodsaver.dao.UserDAO;
import com.sandun.efoodsaver.entities.User;

@Database(version = 1, entities = {User.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
}
