package com.example.RNU;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

public class retake_picture extends AppCompatActivity {

    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;
    ImageButton retake_picture;
    ImageButton see_result;

    String wrinkle_pic;
    String pore_pic;
    String oily_pic;
    String tone_pic;

    int result1;
    int result2;
    int result3;
    int result4;

    private UserDateDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retake_picture);

        db=UserDateDatabase.getDatabase(this);

        img1=(ImageView)findViewById(R.id.img1);
        img2=(ImageView)findViewById(R.id.img2);
        img3=(ImageView)findViewById(R.id.img3);
        img4=(ImageView)findViewById(R.id.img4);

        retake_picture =(ImageButton)findViewById(R.id.retake_picture);
        see_result =(ImageButton)findViewById(R.id.see_result);

        //key값을 통해 데이터 전달 받기
        wrinkle_pic=getIntent().getStringExtra("wrinkle_path");
        pore_pic=getIntent().getStringExtra("pore_path");
        oily_pic=getIntent().getStringExtra("oily_path");
        tone_pic=getIntent().getStringExtra("tone_path");
        //해당 영역 이미지뷰에 이미지 출력
        Glide.with(this).load(wrinkle_pic).into(img1);
        Glide.with(this).load(pore_pic).into(img2);
        Glide.with(this).load(oily_pic).into(img3);
        Glide.with(this).load(tone_pic).into(img4);
        //정량화 값 전달 받기
        result1=getIntent().getIntExtra("wrinkle_level",0);
        result2=getIntent().getIntExtra("pore_level",0);
        result3=getIntent().getIntExtra("oily_level",0);
        result4=getIntent().getIntExtra("tone_level",0);

        ImageButton home=findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(com.example.RNU.retake_picture.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        //재촬영 버튼 클릭 시 주름 촬영하기로 이동
        retake_picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.retake_picture:
                        Intent intent = new Intent(getApplicationContext(), capture_wrinkle.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                }
            }
        });

        //결과보기 클릭 시 결과보기 화면으로 이동
        see_result.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.see_result:

                        Intent intent = new Intent(getApplicationContext(), com.example.RNU.see_result.class);
                        intent.putExtra("wrinkle_level",result1);
                        intent.putExtra("pore_level",result2);
                        intent.putExtra("oily_level",result3);
                        intent.putExtra("tone_level",result4);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        UserDate userdate=new UserDate();
                        //결과보기를 클릭하는 순간의 날짜와 시간 저장하기
                        long now=System.currentTimeMillis();
                        Date date=new Date(now);
                        SimpleDateFormat format1=new SimpleDateFormat("yyyy년 MM월dd일 HH시mm분");
                        String getTime=format1.format(date);

                        userdate.date=getTime;//날짜 저장
                        userdate.quan_wrinkle=result1;//주름정량화 값
                        userdate.quan_pore=result2;//모공 정량화 값
                        userdate.quan_oily=result3;//번들거림 정량화 값
                        userdate.quan_tone=result4;//피부톤 정량화 값
                        userdate.col_bitmap = tone_pic;
                        db.getUserDateDao().insert(userdate);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}