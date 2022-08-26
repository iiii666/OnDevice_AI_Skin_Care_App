package com.example.RNU;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class compare extends AppCompatActivity {
    RadarChart radarChart;


    ImageView cmp_area;
    ImageButton home;
    int le1;
    int le2;
    int le3;
    int le4;
    TextView txt;
    TextView txt2;
    TextView txt3;
    TextView txt4;
    ArrayAdapter<String> adapter;
    List<String> date_list;
    ArrayList<Integer> li_get = new ArrayList<Integer>();
    ArrayList<String > li_get_2 = new ArrayList<String>();
    ArrayList<Integer> li_get_3 = new ArrayList<>();
    ArrayList<String> li_bitmap = new ArrayList<>();
    ImageView img;
    ImageView img2;
    ViewPager2 sliderViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        radarChart=findViewById(R.id.radarChart);

        cmp_area=findViewById(R.id.cmp_area);
        home=findViewById(R.id.home);

        txt = findViewById(R.id.textView);
        txt2 = findViewById(R.id.textView2);
        txt3 = findViewById(R.id.textView3);
        txt4 = findViewById(R.id.textView4);

        img = (ImageView)findViewById(R.id.img);
        img2 = (ImageView)findViewById(R.id.img6);
        sliderViewPager = findViewById(R.id.viewpager);

        UserDateDatabase db=UserDateDatabase.getDatabase(this);
        date_list=db.getUserDateDao().getdateAll();
        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_multiple_choice,date_list);


        li_get_3 = getIntent().getIntegerArrayListExtra("li3");
        li_bitmap = getIntent().getStringArrayListExtra("li_bitmap");

        if(li_bitmap.size() == 2){
            txt.setText(adapter.getItem(li_get_3.get(0)));
            Glide.with(this).load(li_bitmap.get(0)).override(1000,1000).into(img);
            txt2.setText(adapter.getItem(li_get_3.get(1)));
            Glide.with(this).load(li_bitmap.get(1)).override(1000,1000).into(img2);
        }else{
            txt.setText(adapter.getItem(li_get_3.get(0)));
            Glide.with(this).load(li_bitmap.get(0)).override(1000,1000).into(img);
        }
//        for(int i =0; i<length; i=i+4){
//            le1 = li_get.get(i);
//            le2 = li_get.get(i+1);
//            le3 = li_get.get(i+2);
//            le4 = li_get.get(i+3);
//
//            //그래프 출력 및 해당 날짜 비교
//            if(i == 0){
//
//                txt.setText(adapter.getItem(li_get_3.get(0)));
//                Glide.with(this).load(li_bitmap.get(0)).override(1000,1000).into(img);
//            }
//            else if (i == 4) {
//
//                txt2.setText(adapter.getItem(li_get_3.get(1)));
//                Glide.with(this).load(li_bitmap.get(1)).override(1000,1000).into(img2);
//            }
//            else if(i == 8){
//
//                txt3.setText(adapter.getItem(li_get_3.get(2)));
//            }
//            else if(i == 12){
//
//                txt4.setText(adapter.getItem(li_get_3.get(3)));
//            }
//            else{
//                txt.setText("");
//                txt2.setText("");
//                txt3.setText("");
//                txt4.setText("");
//            }
//        }


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(compare.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        //makeChart();
    }
    private ArrayList<RadarEntry> skin_data(){
        ArrayList<RadarEntry> skin_data = new ArrayList<>();

        //그래프 정보 리스트에 데이터 추가
        skin_data.add(new RadarEntry(le1));//주름 레벨
        skin_data.add(new RadarEntry(le2));//모공 레벨
        skin_data.add(new RadarEntry(le3));//번들거림 정도
        skin_data.add(new RadarEntry(le4));//피부톤 단계

        return skin_data;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}