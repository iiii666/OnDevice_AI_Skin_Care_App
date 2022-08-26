package com.example.RNU;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDateDao {
    @Insert
    void insert(UserDate userdate);

    @Query("select date from UserDate")
    List<String> getdateAll();

    @Query("delete from UserDate where date=:dateid ")
    void delDate(String dateid);

    @Query("select date from UserDate where id =:id")
    String getdate(int id);

    @Query("select quan_oily from UserDate")
    List<Integer> get_oilyAll();

    @Query("select quan_pore from UserDate")
    List<Integer> get_poreAll();

    @Query("select quan_tone from UserDate")
    List<Integer> get_toneAll();

    @Query("select quan_wrinkle from UserDate")
    List<Integer> get_winkleAll();

    @Query("select col_bitmap from UserDate")
    List<String> get_colbitmap();
}
