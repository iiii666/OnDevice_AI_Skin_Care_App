package com.example.RNU;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;

public class solution extends AppCompatActivity {

    ImageButton home;
    WebView wView;
    ImageView img;
    ImageView img2;
    String max_value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);
        home=findViewById(R.id.home);
        wView = (WebView)findViewById(R.id.webview);
        img = (ImageView)findViewById(R.id.imageView2);
        img2 = (ImageView)findViewById(R.id.imageView3);
        initWebView();

        max_value = getIntent().getStringExtra("max_value");
        Log.d("가져온값",""+max_value);
        if(max_value.contains("모공")){
            img.setImageResource(R.drawable.pore_01);
            img2.setImageResource(R.drawable.pore_02);
        }else if(max_value.contains("모공")){
            img.setImageResource(R.drawable.wrinkles_01);
            img2.setImageResource(R.drawable.wrinkles_02);
        }else if(max_value.contains("번들")){
            img.setImageResource(R.drawable.sebum_01);
            img2.setImageResource(R.drawable.sebum_02);
        }else if(max_value.contains("톤")){
            img.setImageResource(R.drawable.pigment_01);
            img2.setImageResource(R.drawable.pigment_02);
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(solution.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }
    public void initWebView(){
        wView.setWebViewClient(new WebViewClient(){

            public void onPageStart(WebView view, String url, Bitmap favicon){
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings ws = wView.getSettings();
        ws.setJavaScriptEnabled(true);
        wView.loadUrl("https://www.naver.com");
    }
    @Override
    public void onBackPressed() {
        if(wView.canGoBack()){
            wView.goBack();
        }else{
            super.onBackPressed();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}