package com.sandun.efoodsaver.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sandun.efoodsaver.entities.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    long insert(User u);

    @Insert
    long[] insertAll(User... u);

    @Update
    int update(User u);

    @Delete
    int delete(User u);

    @Query("DELETE FROM User")
    void deleteAll();

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE id=:id")
    User getById(int id);

    @Query("SELECT * FROM User WHERE emailOrCno=:email")
    User getByEmail(String email);
}
