package com.example.RNU;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserDate {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;//날짜
    public int quan_wrinkle;//주름 정량화 값 받을 변수
    public int quan_pore;//모공 정량화 값 받을 변수
    public int quan_oily;//번들거림 정량화 값 받을 변수
    public int quan_tone;//피부톤 정량화 값 받을 변수
    public String col_bitmap;
}
