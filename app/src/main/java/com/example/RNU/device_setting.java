package com.example.RNU;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class device_setting extends AppCompatActivity {

    private ImageButton btn_device;
    private ImageButton min3;
    private ImageButton min5;
    private ImageButton min10;
    private ImageButton home;
    private Switch switch1;//블루투스
    private Switch b5;//음성안내
    private Switch b6;//테라피 LED
    ImageButton freq1;
    ImageButton freq500;
    ImageButton freq300;
    ImageButton power1;
    ImageButton power2;
    ImageButton power3;
    Button btnSearch;
    private CompoundButton switchActivateNotify;
    private void initSwitchLayout(final WorkManager workManager) {
        switchActivateNotify = (CompoundButton) findViewById(R.id.switch1);
        switchActivateNotify.setChecked(PreferenceHelper.getBoolean(getApplicationContext(), Constants.SHARED_PREF_NOTIFICATION_KEY));
        switchActivateNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean isChannelCreated = NotificationHelper.isNotificationChannelCreated(getApplicationContext());
                    if (isChannelCreated) {
                        PreferenceHelper.setBoolean(getApplicationContext(), Constants.SHARED_PREF_NOTIFICATION_KEY, true);
                        NotificationHelper.setScheduledNotification(workManager);
                    } else {
                        NotificationHelper.createNotificationChannel(getApplicationContext());
                    }
                } else {
                    PreferenceHelper.setBoolean(getApplicationContext(), Constants.SHARED_PREF_NOTIFICATION_KEY, false);
                    workManager.cancelAllWork();
                }
            }
        });
    }
    ArrayList<Integer> setting = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);


        min3=(ImageButton)findViewById(R.id.b2);
        min5=findViewById(R.id.b3);
        min10=findViewById(R.id.b4);
        home=findViewById(R.id.home);
        switch1 = (Switch) findViewById(R.id.b1);
        b5 = (Switch) findViewById(R.id.b5);
        b6 = (Switch)findViewById(R.id.b6);
        initSwitchLayout(WorkManager.getInstance(getApplicationContext()));
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        freq1 = findViewById(R.id.h1);
        freq500 = findViewById(R.id.h2);
        freq300 = findViewById(R.id.h3);
        power1 = findViewById(R.id.s1);
        power2 = findViewById(R.id.s2);
        power3 = findViewById(R.id.s3);
        btnSearch = (Button) findViewById(R.id.button);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(mBluetoothAdapter == null) {
                        Log.i("DEBUG_TAG", "=========블루투스 지원안합니다");
                    }else{
                        if(!mBluetoothAdapter.isEnabled()){
                            mBluetoothAdapter.enable();
                        }
                    }
                }
                else{
                    mBluetoothAdapter.disable();
                }
            }
        });

        b5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });//음성안내
        b6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });//테라피 LED
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(device_setting.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(device_setting.this, ble.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                if(min3.isSelected()){
                    setting.add(0,3);
                }else if (min5.isSelected()){
                    setting.add(0,5);
                }else if (min10.isSelected()){
                    setting.add(0,15);
                }else{
                    setting.add(0,0);
                }
                if(b5.isChecked()){
                    setting.add(1,1);
                }else{
                    setting.add(1,0);
                }
                if(b6.isChecked()){
                    setting.add(2,1);
                }else{
                    setting.add(2,0);
                }
                if (freq1.isSelected()) {
                    setting.add(3, 1);
                } else if (freq500.isSelected()) {
                    setting.add(3, 2);
                } else if (freq300.isSelected()) {
                    setting.add(3, 3);
                }else{
                    setting.add(3,0);
                }
                if (power1.isSelected()) {
                    setting.add(4, 1);
                } else if (power2.isSelected()) {
                    setting.add(4, 2);
                } else if (power3.isSelected()) {
                    setting.add(4, 3);
                }else{
                    setting.add(4,0);
                }//동작중도해야하는데 조율해봐야함
                if(setting.get(0) == 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "환경설정을 모두 해주세요!",Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    intent.putExtra("setting",setting);
                    startActivity(intent);
                }
            }
        });
//        btn_device.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!mBluetoothAdapter.isEnabled())
//                {
//                    Toast toast = Toast.makeText(getApplicationContext(), "블루투스를 켜주세요!",Toast.LENGTH_SHORT);
//                    toast.show();
//                }else{
//                    Intent intent= new Intent(device_setting.this, device_interlock.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    if(min3.isSelected()){
//                       setting.add(0,3);
//                    }else if (min5.isSelected()){
//                        setting.add(0,5);
//                    }else if (min10.isSelected()){
//                        setting.add(0,15);
//                    }else{
//                        setting.add(0,0);
//                    }
//
//                    if(b5.isChecked()){
//                        setting.add(1,1);
//                    }else{
//                        setting.add(1,0);
//                    }
//
//                    if(b6.isChecked()){
//                        setting.add(2,1);
//                    }else{
//                        setting.add(2,0);
//                    }
//                    if (freq1.isSelected()) {
//                        setting.add(3, 1);
//                    } else if (freq500.isSelected()) {
//                        setting.add(3, 2);
//                    } else if (freq300.isSelected()) {
//                        setting.add(3, 3);
//                    }else{
//                        setting.add(3,0);
//                    }
//
//                    if (power1.isSelected()) {
//                        setting.add(4, 1);
//                    } else if (power2.isSelected()) {
//                        setting.add(4, 2);
//                    } else if (power3.isSelected()) {
//                        setting.add(4, 3);
//                    }else{
//                        setting.add(4,0);
//                    }//동작중도해야하는데 조율해봐야함
//
//                    if(setting.get(0) == 0){
//                        Toast toast = Toast.makeText(getApplicationContext(), "환경설정을 모두 해주세요!",Toast.LENGTH_SHORT);
//                        toast.show();
//                    }else{
//                        intent.putExtra("setting",setting);
//                        startActivity(intent);
//                    }
//
//
//                }
//
//            }
//        });

        min3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                min3.setSelected(!min3.isSelected());
                min5.setSelected(false);
                min10.setSelected(false);
            }
        });

        min5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                min5.setSelected(!min5.isSelected());
                min3.setSelected(false);
                min10.setSelected(false);
            }
        });

        min10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                min10.setSelected(!min10.isSelected());
                min3.setSelected(false);
                min5.setSelected(false);

            }
        });
        freq1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freq1.setSelected(!freq1.isSelected());
                freq500.setSelected(false);
                freq300.setSelected(false);
            }
        });
        freq500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freq500.setSelected(!freq500.isSelected());
                freq300.setSelected(false);
                freq1.setSelected(false);
            }
        });
        freq300.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freq300.setSelected(!freq300.isSelected());
                freq500.setSelected(false);
                freq1.setSelected(false);
            }
        });
        power1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                power1.setSelected(!power1.isSelected());
                power2.setSelected(false);
                power3.setSelected(false);
            }
        });
        power2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                power2.setSelected(!power2.isSelected());
                power1.setSelected(false);
                power3.setSelected(false);
            }
        });
        power3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                power3.setSelected(!power3.isSelected());
                power2.setSelected(false);
                power1.setSelected(false);
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}