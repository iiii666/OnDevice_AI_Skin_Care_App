package com.example.RNU;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.palette.graphics.Palette;

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

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class capture_tone extends AppCompatActivity {

    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;
    ImageButton take_picture;
    ImageButton see_result;

    String m_current_tone_path;
    String wrinkle_pic;
    String pore_pic;
    String oily_pic;

    int result1;
    int result2;
    int result3;
    int result4=0;

    public static Context context_tone;

    String [][] color={
            {"F6F6F6","F9F9F9","FBFBFB","FEFEFE","FFFFFF",
                    "FDEBC8", "FDF5D2", "FDF5DC", "FDF5E6",
                    "FAE6B9", "FAEBC3", "FAEBCD", "FAEBD7",
                    "FFE0C6", "FFE5CB", "FFEAD0", "FFEFD5","F0E8D8",
                    "FFD9E4",  "FFDEE9",  "FFE3EE",
                    "FFE6EB",  "FFEBF0",  "FFF0F5",
                    "FFDFDC",  "FFE4E1", "FFDCDC",  "FFE6E6",  "FFF0F0","FFFFE0","FFF8DC","FFFACD",
                    "FFFFF0","FAEB78","FAF082","FAF58C","FAFA96","FAFAA0",
                    "FAFAD2","FFFF8C","FAFAB4","FFEB5A","FFF064","FFF56E","FAFAAA","FAFABE","FFFA78","FFFA82","FFFF8C","FFFF96","FFF978","FAE1AF","FFA98F"
                    ,"FFB399","FFBDA3","FFC7AD","FFD1B7","FFA374","FFAD7E","FFB788","FFC192","FFCB9C","FFC8C8","FFD2D2","FAB2B2","FAB7B7","FABCBC","FAC1C1",  "FAC6C6","F5A0A0"
                    ,"F5AAAA","FAB4B4","FABEBE","FAC8C8","F09696","F4A0A0","F4AAAA","F4B4B4","FEBEBE","FF6464","FF6E6E","FF7878","FF8282","FF8C8C","EB6464"
            },
            {"FBE4D2","FBE7D2","FBEBD2","FBEED2","FBF1D2","FBF5D2","FBF8D2",
                    "FDDCAA", "FDE1B4",
                    "FAD79B", "FADCA5",
                    "FFD0A1", "FFD5A6", "FFDAAB", "FFDFB0", "FFE4B5",
                    "FFB6C1", "FFD0CD", "FFD5D2", "FFDAD7","D8A8A8","FFBB8C", "FFC091", "FFC596", "FFCA9B",
                    "E8B8B8","D8A8A0","C88888","E09898","D89898","FF9696",
                    "FFAAAF",  "FFB4B9","FFEBCD","FFDC3C","FFE146","FFE650","FAC87D","FACD87","FAD291","FDCD8C","FDD296","FDD7A0","FDDCAA","FDE1B4","FDE6BE",
                    "FFC81E", "FFD228","FFD732","FFB400","FFBE0A","FFC314","FFCD28","FFD232","FFD73C","FFDC46","FFE150","FFE65A","FFDBC1","FFC6A5","FFD0AF",
                    "FFD0AF","FFD5B4","FFDAB9","FFA0A0","FFAAAA","FFB4B4","FFBEBE","F08080","F08A8A","F09494","F59E9E","FAA8A8","F56E6E","F57878","F58282","F58C8C","F59696"
                    ,"F06464","F06E6E","F07878","F08282","F08C8C"
            },
            {"EAB179","EABA79","EAC479","EACD79","EAD679","EAE079","EAE979",
                    "FF9473", "FF9E7D", "FFA887", "FFB291", "FFBC9B",
                    "FF7F50", "FF895A", "FF9364", "FF9D6E", "FFA778",
                    "FF9E9B", "FFA8A5","D88888",
                    "FFB182", "FFBB82", "FFA782",
                    "E19B50", "E6A55A", "EBAA5F", "EBAF64", "F0B469",
                    "FF7A85", "FF848F", "FF8E99","D0C0C0","D8C8C8","E1B771","E6C17B","EBC680","F0CB85","F5D08A"},
            {"D76F06","D78006","D79206","D7A306","D7B406","D7C606","D7D706",
                    "E0904C", "E59551", "EA9A56", "EF9F5B", "F4A460",
                    "FF5675", "FF607F", "FF6A89",
                    "CD853F", "CD8F49", "D29953", "D7A35D", "DCAD67",
                    "C2722E", "CC7C38", "D68642",
                    "D2691E", "D27328", "D77D32", "D7873C", "DC9146",
                    "FF88A7","B8A888","C8B088",
                    "D2691E","B0A080","B8B0A0","C0B8A8", "AE5E1A", "B86824","B8A078","A8A088", "D25A1E","D27D32","A05C37","A06641","A5704B","AA7A55","B4845F","B98E69","C39873"},
            {"965A30","966230","966B30","967330","967C30","968430","968D30",
                    "8B5927", "8B6331", "906D3B",
                    "957745", "9F814F", "A48654", "A98B59", "AE905E","A8A090",
                    "8B6331", "906D3B","583028","A0A098",
                    "9A7745", "A4814F", "AE8B59", "B89563", "C29F6D","989890","A0522D","887070","908870"},
            {"6E2107","6E2A07","6E3207","6E3B07","6E4407","6E4C07","6E5507",
                    "8B4513", "8B3113", "8B3B13", "5E2F0D", "753A10", "8B4F1D","909080","605848","800000"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_tone);

        img1=(ImageView)findViewById(R.id.img1);
        img2=(ImageView)findViewById(R.id.img2);
        img3=(ImageView)findViewById(R.id.img3);
        img4=(ImageView)findViewById(R.id.img4);
        take_picture =(ImageButton)findViewById(R.id.take_picture);
        see_result =(ImageButton)findViewById(R.id.see_result);

        context_tone=this;

        wrinkle_pic=getIntent().getStringExtra("wrinkle_path");
        pore_pic=getIntent().getStringExtra("pore_path");
        oily_pic=getIntent().getStringExtra("oily_path");

        Glide.with(this).load(wrinkle_pic).into(img1);
        Glide.with(this).load(pore_pic).into(img2);
        Glide.with(this).load(oily_pic).into(img3);

        result1=getIntent().getIntExtra("wrinkle_level",0);
        result2=getIntent().getIntExtra("pore_level",0);
        result3=getIntent().getIntExtra("oily_level",0);

        ImageButton home=findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(capture_tone.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        take_picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.take_picture:
                        takePicture();
                        break;
                }
            }
        });

    }

    public void extract_tone(Bitmap myBitmap){
        Palette.from(myBitmap).generate(new Palette.PaletteAsyncListener(){
            @Override
            public void onGenerated(@Nullable Palette palette) {

                Palette.Swatch swatch_01=palette.getLightVibrantSwatch();
                // 오류 발생: 색상 코드 안에 있는 것이 아닌 피부색은 전에 추출한 피부색코드가 뜸
                if(swatch_01!=null) {
                    String candi_01 = String.format("%06X", (0xFFFFFF & swatch_01.getRgb()));
                    for(int i=0;i<color.length;i++) {
                        for (int j = 0; j < color[i].length; j++) {
                            if (color[i][j].equals(candi_01))
                            {
                                result4=i;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    Palette.Swatch swatch_02=palette.getDominantSwatch();
                    String candi_02=String.format("%06X", (0xFFFFFF&swatch_02.getRgb()));

                    for(int i=0;i<color.length;i++) {
                        for (int j = 0; j < color[i].length; j++) {

                            if (color[i][j].equals(candi_02)) {
                                result4 = i;
                                break;
                            }
                        }
                    }

                }
            }
        });
    }

    private void transformScreen(int time, final Class aClass){
        Handler mHandler=new Handler();
        mHandler.postDelayed(()-> {

            Intent intent_s=new Intent(getApplicationContext(),aClass);

            ByteArrayOutputStream stream= new ByteArrayOutputStream();
            Bitmap bmp=((BitmapDrawable)img4.getDrawable()).getBitmap();
            extract_tone(bmp);
            if(result4==0)
            {
                result4=5;
            }
            //데이터 시각화를 위한 정량화 값 전달
            intent_s.putExtra("wrinkle_level",result1);
            intent_s.putExtra("pore_level",result2);
            intent_s.putExtra("oily_level",result3);
            intent_s.putExtra("tone_level",result4);
            intent_s.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //사진 데이터 전달
            intent_s.putExtra("wrinkle_path", ((capture_wrinkle) capture_wrinkle.context_wrinkle).m_current_wrinkle_path);
            intent_s.putExtra("pore_path", ((capture_pore)capture_pore.context_pore).m_current_pore_path);
            intent_s.putExtra("oily_path", ((capture_oily)capture_oily.context_oily).m_current_oily_path);
            intent_s.putExtra("tone_path",m_current_tone_path);
            intent_s.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent_s);
        },time);
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
        m_current_tone_path = image.getAbsolutePath();//파일 절대경로 저장하기
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
                            File file = new File(m_current_tone_path);
                            Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= 29) {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                                try {
                                    bitmap = ImageDecoder.decodeBitmap(source);
                                    if (bitmap != null) {
                                        ExifInterface ei = new ExifInterface(m_current_tone_path);
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
                                        img4.setImageBitmap(rotatedBitmap);
                                        transformScreen(2000, retake_picture.class);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                    if (bitmap != null) {
                                        ExifInterface ei = new ExifInterface(m_current_tone_path);
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
                                        img4.setImageBitmap(rotatedBitmap);
                                        transformScreen(2500, retake_picture.class);
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
