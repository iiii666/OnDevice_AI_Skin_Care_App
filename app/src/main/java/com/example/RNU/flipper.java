package com.example.RNU;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

public class flipper extends AppCompatActivity {
    List<String> date_list;
    ArrayAdapter<String> adapter;
    ImageButton home;

    ViewFlipper flipper;
    ToggleButton toggle_Flipping;
    ArrayList<String> li_bitmap = new ArrayList<>();
    ArrayList<Uri> uri_bitmap = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flipper);
        home = findViewById(R.id.home);
        li_bitmap = getIntent().getStringArrayListExtra("li_bitmap");
        for(int i = 0; i<li_bitmap.size();i++){
            uri_bitmap.add(Uri.parse(li_bitmap.get(i)));//uri 를 bitmap 으로 변환 후 저장
        }
        UserDateDatabase db=UserDateDatabase.getDatabase(this);
        date_list=db.getUserDateDao().getdateAll();
        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_multiple_choice,date_list);
        flipper = (ViewFlipper)findViewById(R.id.flipper);

        for(int i = 0; i<li_bitmap.size();i++){
            ImageView img = new ImageView(this);
            img.setImageURI(uri_bitmap.get(i));
            flipper.addView(img);
        }
        Animation showln = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        flipper.setInAnimation(showln);
        flipper.setOutAnimation(this, android.R.anim.slide_out_right);
        toggle_Flipping = (ToggleButton)findViewById(R.id.toggle_auto);
        toggle_Flipping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    flipper.setFlipInterval(1000);
                    flipper.startFlipping();
                }else{
                    flipper.stopFlipping();
                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(flipper.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }
}