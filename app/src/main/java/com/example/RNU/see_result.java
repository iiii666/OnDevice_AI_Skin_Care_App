package com.example.RNU;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class see_result extends AppCompatActivity {

    ImageButton manager;
    ImageButton care_solu;
    ImageButton device;
    ImageButton home;
    RadarChart radarChart;

    int le1;
    int le2;
    int le3;
    int le4;
    int le1_weight;
    int le2_weight;
    int le3_weight;
    int le4_weight;
    TextView textView9;
    static int weight = 1800;
    TreeMap<Integer, String> map = new TreeMap<>();
    TreeMap<Integer, String> map_max = new TreeMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        manager=findViewById(R.id.btn04);
        care_solu=findViewById(R.id.btn05);
        device=findViewById(R.id.btn06);
        home=findViewById(R.id.home);
        radarChart=findViewById(R.id.radarChart);
        textView9 = findViewById(R.id.textView9);
        //정량화 값 전달 받기
        le1=getIntent().getIntExtra("wrinkle_level",0);
        le2=getIntent().getIntExtra("pore_level",0);
        le3=getIntent().getIntExtra("oily_level",0);
        le4=getIntent().getIntExtra("tone_level",0);

        le1_weight = weight / le1;
        le2_weight = weight / le2;
        le3_weight = weight / le3;
        le4_weight = weight / le4;
        map.put(le3_weight, "번들");
        map.put(le4_weight, "톤");
        map.put(le1_weight, "주름");
        map.put(le2_weight, "모공");
        //Log.d("트리",""+map);
        Integer maxKey = Collections.max(map.keySet());
        //.d("트리 길이",""+maxKey);
        String max_value = map.get(maxKey);
        Integer max_level = 0;
        if(max_value == "주름"){
            max_level = le1;
        }else if(max_value =="모공"){
            max_level = le2;
        }else if(max_value =="번들"){
            max_level = le3;
        }else if(max_value =="톤"){
            max_level = le4;
        }
        Log.d("max_value",""+max_value);
        Log.d("max_level = ","" + max_level);  //max_level = 가중치 계산 값중 최대값에 해당하는 정량화 level 입니다. max_value, max_level의 조건만 나누어 링크로 연결하면 될 것 입니다.

        if(max_value == "모공" && (max_level == 1 || max_level == 2) ){
            textView9.setText("피부가 번들거리지 않고 맑고 건강한 피부로 보일 수 있다. 그러나 작은 모공으로 인해 피지의 분비량이 과도하게 적으면 피부가 건조해지기 쉬우며 민감성 피부로 바뀔 가능성이 있다.");
        }else if(max_value == "모공" && (max_level == 3 || max_level == 4) ){
            textView9.setText("피부가 번들거리지 않고 맑고 건강한 피부로 보일 수 있다. 그러나 작은 모공으로 인해 피지의 분비량이 적으면 피부가 건조해질 가능성이 있다.");
        }else if(max_value == "모공" && (max_level == 5 || max_level == 6) ){
            textView9.setText("피부가 번들거리지 않고 모공이 조금 보이지만 건강한 피부를 나타낼 가능성이 높다.");
        }else if(max_value == "모공" && (max_level == 7 || max_level == 8) ){
            textView9.setText("피부는 두꺼워 보이며 모공이 커 보이고 피지의 분비량의 다소 많아 번들거리는 지성 피부를 가질 가능성이 있다.");
        }else if(max_value == "모공" && (max_level == 9 || max_level == 10 || max_level == 11) ){
            textView9.setText("피부가 두꺼워 보이며 모공은 아주 두드러져 보이고 피지의 분비량이 과다하게 많아 번들거리는 지성 피부를 가질 가능상이 높으며 여드름과 같은 피부 트러블이 생길 가능성이 높다.");
        }else if(max_value == "주름" && (max_level == 1 ) ){
            textView9.setText("햇빛에 노출이 적어 그을리지 않은 젊고 말고 탄력 있고 건강한 피부를 유지하고 있는 상태이다. 20대의 젊은 연령층에서 볼 수 있는 주름 상태이다.");
        }else if(max_value == "주름" && (max_level == 2 || max_level == 3) ){
            textView9.setText("젊고 건강한 피부이며 30대에 나타나는 주름 형태라고 볼 수 있다.");
        }else if(max_value == "주름" && (max_level == 4 || max_level == 5) ){
            textView9.setText("젊고 건강한 피부이나 자연적 노화에 의해 40대에 나타나는 주름 형태라고 볼 수 있다.");
        }else if(max_value == "주름" && (max_level == 6 || max_level == 7 )){
            textView9.setText("건강한 피부이나 자연적 노화에 의해 40-50대에 나타나는 주름형태라고 볼 수 있다. 햇빛에 의한 광노화로도 발생할 수 있으며 자연 노화보다는 좀더 주름의 정도가 심해질 수 있다.");
        }else if(max_value == "주름" && (max_level == 8 || max_level == 9 )){
            textView9.setText("자연적 노화에 의해 60대에 나타나는 주름 형태라고 볼 수 있다. 햇빛에 의한 광노화로도 발생할 수 있으며 자연 노화보다는 좀더 주름의 정도가 심해질 수 있다.");
        }else if(max_value == "주름" && (max_level == 10 || max_level == 11 )){
            textView9.setText("70대 이상의 자연적 노화가 많이 발생하는 노인에서 볼 수 있는 피부 주름형태이며 햇빛에 의한 광노화가 진행되어 나타나는 경우에는 좀더 젊은 나이에도 나타날 수 있고 주름이 훨씬 깊다. 담배나 사우나 같은 뜨거운 열, 갱년기 호르몬의 변화도 주름의 원인이 된다.");
        }else if(max_value == "톤" && (max_level ==1 )){
            textView9.setText("백인들이 주로 가진 피부이다");
        }else if(max_value == "톤" && (max_level ==2 )){
            textView9.setText("백인들 중 지중해 연안에 거주하는 사람들의 피부 형태이다");
        }else if(max_value == "톤" && (max_level ==3 || max_level ==4 ||max_level==5 )){
            textView9.setText("우리나라 사람들이 흔히 가지고 있는 피부 형태이다.");
        }else if(max_value == "톤" && (max_level ==6 )){
            textView9.setText("흑인들이 가지고 있는 피부 형태이다.");
        }else if(max_value == "번들" && (max_level ==1 )){
            textView9.setText("모공이 좁고 건성의 피부소견을 보일 가능성이 높다.");
        } else if(max_value == "번들" && (max_level ==2 )){
            textView9.setText("모공이 좁고 약간의 건성 피부소견을 보일 가능성이 높다.");
        }else if(max_value == "번들" && (max_level ==3 )){
            textView9.setText("건강한 중성의 피부소견을 보일 가능성이 높다.");
        }else if(max_value == "번들" && (max_level ==4 )){
            textView9.setText("모공이 넓고 지성의 피부소견을 보일 가능성이 높다.");
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(see_result.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(see_result.this, manage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        care_solu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(see_result.this, solution.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("max_value",max_value);
                startActivity(intent);
            }
        });

        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(see_result.this, device_setting.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        makeChart();
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

    private void makeChart(){
        RadarDataSet dataSet = new RadarDataSet(skin_data(),"Data");
        dataSet.setDrawValues(false);//주름,모공,번들거림,피부톤 레벨 수치 값 없애기

        dataSet.setColor(Color.parseColor("#137617"));
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#FF80D283"));

        RadarData data = new RadarData();
        data.addDataSet(dataSet);

        String[] labels =  {"주름", "모공", "번들거림", "피부톤"};

        XAxis xAxis = radarChart.getXAxis();
        YAxis yAxis=radarChart.getYAxis();

        radarChart.setDescription(null);//Description 지워
        radarChart.setTouchEnabled(false);//차트 터치 못하게
        radarChart.getLegend().setEnabled(false);//범례 다 지워

        yAxis.setAxisMaximum(8);//최대값 설정
        yAxis.setAxisMinimum(1);//최솟값 설정

        yAxis.setEnabled(false);//y축 구간 값 안보여주기
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        radarChart.setData(data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}