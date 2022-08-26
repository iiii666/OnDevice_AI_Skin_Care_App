package com.example.RNU;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class capture_oily extends AppCompatActivity {

    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageButton take_picture;
    TextView t;
    ImageButton see_result;
    ImageButton home;


    int flag =1;
    String m_current_oily_path;
    String wrinkle_pic;
    String pore_pic;

    int result1;
    int result2;
    int result3;

    public static Context context_oily;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_oily);

        img1=(ImageView)findViewById(R.id.img1);
        img2=(ImageView)findViewById(R.id.img2);
        img3=(ImageView)findViewById(R.id.img3);
        take_picture =(ImageButton)findViewById(R.id.take_picture);
        t=(TextView)findViewById(R.id.t);
        see_result =(ImageButton)findViewById(R.id.see_result);
        home=(ImageButton) findViewById(R.id.home);

        context_oily=this;

        wrinkle_pic=getIntent().getStringExtra("wrinkle_path");
        pore_pic=getIntent().getStringExtra("pore_path");
        result1=getIntent().getIntExtra("wrinkle_level",0);
        result2=getIntent().getIntExtra("pore_level",0);

        Glide.with(this).load(wrinkle_pic).into(img1);
        Glide.with(this).load(pore_pic).into(img2);


        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent= new Intent(capture_oily.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.take_picture:
                        takePicture();
                        break;
                }
            }

        });

        t.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                switch (v.getId()) {

                }

            }
        });
    }
    private void transformScreen(int time, final Class aClass){
        Handler mHandler=new Handler();
        mHandler.postDelayed(()-> {

            Intent intent_s=new Intent(getApplicationContext(),aClass);

            ByteArrayOutputStream stream= new ByteArrayOutputStream();
            Bitmap bmp=((BitmapDrawable)img3.getDrawable()).getBitmap();
            result3=extract_oily(bmp);

            if(result3>=0 &&result3<3){
                result3=3;
            }
            else if(result3>=3 &&result3<=6){
                result3=6;
            }
            else if(result3>=7 &&result3<=10){
                result3=9;
            }


            intent_s.putExtra("wrinkle_path", wrinkle_pic);
            intent_s.putExtra("pore_path", pore_pic);
            intent_s.putExtra("oily_path", m_current_oily_path);

            intent_s.putExtra("wrinkle_level",result1);
            intent_s.putExtra("pore_level",result2);
            intent_s.putExtra("oily_level",result3);
            intent_s.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent_s);
        },time);
    }
    private void next(int result){
        Intent intent = new Intent(getApplicationContext(), capture_tone.class);

        intent.putExtra("wrinkle_path", wrinkle_pic);
        intent.putExtra("pore_path", pore_pic);
        intent.putExtra("oily_path", m_current_oily_path);

        intent.putExtra("wrinkle_level",result1);
        intent.putExtra("pore_level",result2);
        intent.putExtra("oily_level",result3);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(intent);
    }
    private int extract_oily(Bitmap myBitmap){
        OpenCVLoader.initDebug();

        Mat img1=new Mat();
        Mat img_gray=new Mat();
        Mat img_mask=new Mat();
        Mat img_result=new Mat();

        Utils.bitmapToMat(myBitmap,img1);
        Imgproc.cvtColor(img1,img_gray,Imgproc.COLOR_BGR2GRAY);
        Core.inRange(img_gray, new Scalar(0,0,0),new Scalar(190,1,255),img_mask);
        Core.bitwise_and(img_gray,img_mask,img_result);

        Bitmap bitmap_01;
        bitmap_01=Bitmap.createBitmap(img_result.cols(),img_result.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_result,bitmap_01);

        int black_pixel=0;
        int[] mPixels=new int[bitmap_01.getWidth()*bitmap_01.getHeight()];
        bitmap_01.getPixels(mPixels,0,bitmap_01.getWidth(),0,0,bitmap_01.getWidth(),bitmap_01.getHeight());
        for(int i=0,max=mPixels.length;i<max;i++){
            int rgb=mPixels[i];
            int r=(rgb>>16)&0xFF;
            int g=(rgb>>8)&0xFF;
            int b=rgb&0xFF;

            if(r==0 &&g==0 &&b==0){
                black_pixel++;
            }
        }

        int level=11;
        int level_thd=90000;
        int level_min=0;
        for(int i=0;i<11;i++)
        {
            if(i==0) {
                level_min=-1;
            }
            else{
                level_min=level_thd*i;
            }
            if((level_min<black_pixel) && (black_pixel<=level_thd*(i+1))){
                level=i;
                break;
            }
        }

        return level;
    }

    private void takePicture() {
        Intent take_picture_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (take_picture_intent.resolveActivity(getPackageManager()) != null) {
            File photo_file = null;
            try {
                photo_file = createImageFile();
            } catch (IOException ex) {
            }

            if (photo_file != null) {
                Uri photo_uri = FileProvider.getUriForFile(this, "com.example.RNU.fileprovider", photo_file);
                take_picture_intent.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri);
                take_picture_intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityResult.launch(take_picture_intent);
            }
        }

    }

    private File createImageFile() throws IOException {
        String time_stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String image_file_name = "PNG_" + time_stamp + "_";
        File storage_dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                image_file_name,
                ".png",
                storage_dir
        );
        m_current_oily_path = image.getAbsolutePath();//파일 절대경로 저장하기
        flag += 1;
        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            File file = new File(m_current_oily_path);
                            Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= 29) {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                                try {
                                    bitmap = ImageDecoder.decodeBitmap(source);
                                    if (bitmap != null) {
                                        ExifInterface ei = new ExifInterface(m_current_oily_path);
                                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                                        Bitmap rotatedBitmap = null;

                                        switch (orientation) {
                                            case ExifInterface.ORIENTATION_ROTATE_90:
                                                rotatedBitmap = rotateImage(bitmap, 90);
                                                break;
                                            case ExifInterface.ORIENTATION_ROTATE_180:
                                                rotatedBitmap = rotateImage(bitmap, 180);
                                                break;
                                            case ExifInterface.ORIENTATION_ROTATE_270:
                                                rotatedBitmap = rotateImage(bitmap, 270);
                                                break;
                                            case ExifInterface.ORIENTATION_NORMAL:
                                            default:
                                                rotatedBitmap = bitmap;
                                        }
                                        img3.setImageBitmap(rotatedBitmap);
                                        transformScreen(2000, capture_tone.class);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                    if (bitmap != null) {
                                        ExifInterface ei = new ExifInterface(m_current_oily_path);
                                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                                        Bitmap rotatedBitmap = null;

                                        switch (orientation) {
                                            case ExifInterface.ORIENTATION_ROTATE_90:
                                                rotatedBitmap = rotateImage(bitmap, 90);
                                                break;
                                            case ExifInterface.ORIENTATION_ROTATE_180:
                                                rotatedBitmap = rotateImage(bitmap, 180);
                                                break;
                                            case ExifInterface.ORIENTATION_ROTATE_270:
                                                rotatedBitmap = rotateImage(bitmap, 270);
                                                break;
                                            case ExifInterface.ORIENTATION_NORMAL:
                                            default:
                                                rotatedBitmap = bitmap;
                                        }
                                        img3.setImageBitmap(rotatedBitmap);
                                        transformScreen(2000, capture_tone.class);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}
