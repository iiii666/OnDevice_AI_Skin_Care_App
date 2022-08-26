package com.example.RNU;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class record_list extends AppCompatActivity {


    ImageButton btn_compar;
    ImageButton btn_delete;
    ImageButton home;

    TextView[] tv= new TextView[6];
    ArrayAdapter<String> adapter;
    List<Integer> q_w;
    List<Integer> q_p;
    List<Integer> q_o;
    List<Integer> q_t;
    List<String> date_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);


        btn_compar=(ImageButton) findViewById(R.id.btn_compar);
        btn_delete=(ImageButton)findViewById(R.id.btn_delete);
        home=(ImageButton)findViewById(R.id.home);
        ListView lv=(ListView)findViewById(R.id.lv);

        UserDateDatabase db=UserDateDatabase.getDatabase(this);
        date_list=db.getUserDateDao().getdateAll();

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,date_list);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //리스트뷰와 어레이어댑터를 연결
        lv.setAdapter(adapter);

        //정량화 값 각각의 리스트에 저장
        q_w=db.getUserDateDao().get_winkleAll();
        q_p=db.getUserDateDao().get_poreAll();
        q_o=db.getUserDateDao().get_oilyAll();
        q_t=db.getUserDateDao().get_toneAll();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //클릭 한 아이템의 정량화 값 저장
                int result1=q_w.get(position);
                int result2=q_p.get(position);
                int result3=q_o.get(position);
                int result4=q_t.get(position);

                Intent intent = new Intent(getApplicationContext(), com.example.RNU.see_result.class);
                intent.putExtra("wrinkle_level",result1);
                intent.putExtra("pore_level",result2);
                intent.putExtra("oily_level",result3);
                intent.putExtra("tone_level",result4);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                startActivity(intent);
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(record_list.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        btn_compar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(record_list.this, for_compare.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(record_list.this, delete_record.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

}