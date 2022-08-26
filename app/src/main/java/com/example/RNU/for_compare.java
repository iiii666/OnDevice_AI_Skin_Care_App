package com.example.RNU;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class for_compare extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    List<String> date_list;
    List<Integer> q_w;
    List<Integer> q_p;
    List<Integer> q_o;
    List<Integer> q_t;
    List<String> col_bitmap;
    ImageButton home;

    ArrayList<Integer> li = new ArrayList<Integer>();
    ArrayList<String> li2 = new ArrayList<>();
    ArrayList<String> li_bitmap = new ArrayList<>();
    ArrayList<Integer> li3 = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_compare);

        ImageButton btn_compar=(ImageButton) findViewById(R.id.btn_compar);
        ListView lv=(ListView)findViewById(R.id.lv);
        home = findViewById(R.id.home);
        UserDateDatabase db=UserDateDatabase.getDatabase(this);
        date_list=db.getUserDateDao().getdateAll();


        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_multiple_choice,date_list);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adapter);

        //정량화 값 각각의 리스트에 저장
        q_w=db.getUserDateDao().get_winkleAll();
        q_p=db.getUserDateDao().get_poreAll();
        q_o=db.getUserDateDao().get_oilyAll();
        q_t=db.getUserDateDao().get_toneAll();
        col_bitmap =db.getUserDateDao().get_colbitmap();
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(for_compare.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        btn_compar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
                int count = lv.getCount();
                int index = 0;
                Intent intent = new Intent(for_compare.this, compare.class);
                Intent intent2 = new Intent(for_compare.this,flipper.class);//클래스 변경해야함
                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        String result5 =col_bitmap.get(i);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        li3.add(i);
                        li_bitmap.add(result5);
                        index = index + 1;
                    }
                }
                intent.putExtra("li3", li3);
                intent.putExtra("li_bitmap",li_bitmap);
                intent2.putExtra("li3", li3);
                intent2.putExtra("li_bitmap",li_bitmap);
                if(checkedItems.size() <= 2){
                    startActivity(intent);
                }else{
                    startActivity(intent2);
                }
                lv.clearChoices() ;
            }
        });
    }

}