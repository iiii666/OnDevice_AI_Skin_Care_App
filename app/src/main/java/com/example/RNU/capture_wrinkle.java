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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class capture_wrinkle extends AppCompatActivity {

    ImageButton take_picture;
    ImageButton see_result;
    ImageView img1;
    ImageView pic;
    TextView p;
    Module m_module = null;

    String m_current_wrinkle_path;
    int result1;
    public static Context context_wrinkle;


    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();//getAbsolutePath() : File??? ????????? ?????? ?????? ??????
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_wrinkle);

        context_wrinkle=this;

        take_picture = (ImageButton) findViewById(R.id.take_picture);
        see_result=findViewById(R.id.see_result);
        img1 = (ImageView) findViewById(R.id.img1);
        pic = (ImageView) findViewById(R.id.pic);
        p = (TextView) findViewById(R.id.p);
        ImageButton home=findViewById(R.id.home);

        //???????????? ?????? ????????????
        try {
            m_module = LiteModuleLoader.load(assetFilePath(getApplicationContext(), "op_epoch100.ptl"));
        } catch (IOException e){
            Log.e("Main Activity", "Error reading assets", e);
            finish();
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(capture_wrinkle.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        //???????????? ?????? ?????? ???, takePicture()?????? ??????
        take_picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.take_picture:
                        takePicture();
                        break;
                }
            }
        });
        //?????? TextView ?????????, ?????? ??????????????? ???????????? ???????????? ?????? ?????? ??????, ?????????/????????? ??? ??????
        p.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                result1=analyzeWrinkle();
//                Intent intent = new Intent(capture_wrinkle.this, capture_pore.class);
//                intent.putExtra("wrinkle_path", m_current_wrinkle_path);
//                intent.putExtra("wrinkle_level",result1);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);
            }
        });
    }
    private void transformScreen(int time, final Class aClass){
        Handler mHandler=new Handler();
        mHandler.postDelayed(()-> {

            Intent intent_s = new Intent(getApplicationContext(),aClass);
            ByteArrayOutputStream stream= new ByteArrayOutputStream();
            result1=analyzeWrinkle();
            intent_s.putExtra("wrinkle_path", m_current_wrinkle_path);
            intent_s.putExtra("wrinkle_level",result1);
            intent_s.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent_s);
        },time);
    }
    private Bitmap getFilteredBitmap(ImageView iv){
        //ImageView??? resource??? bitmap?????? ????????????
        BitmapDrawable drawable = (BitmapDrawable)iv.getDrawable();
        Bitmap s_bitmap = drawable.getBitmap();

        Bitmap d_bitmap = s_bitmap.copy(s_bitmap.getConfig(), true);
        Paint paint = new Paint();//????????? ?????? ???????????? ??????
        ColorFilter f = iv.getColorFilter();
        Log.d("wrinkle", String.valueOf(f));
        paint.setColorFilter(f);//RGB??? ???????????? paint??? ????????? ?????????. ????????? Alpha??? ?????? ?????????
        Canvas canvas = new Canvas(d_bitmap);//Canvas?????? ??????, ??????????????? ??? ????????? ?????????
        canvas.drawBitmap(d_bitmap, 0, 0, paint);//????????? ??????
        return d_bitmap;
    }

    private int analyzeWrinkle() {

        Bitmap m_bitmap = getFilteredBitmap(img1);
        int width_origin = m_bitmap.getWidth();
        int height_origin = m_bitmap.getHeight();
        int width_new = 256;
        int height_new = 256;

        Matrix matrix = new Matrix();
        float width_scale = ((float) width_new / width_origin);
        float height_scale = ((float) height_new / height_origin);

        matrix.postScale(width_scale, height_scale);
        Bitmap bitmap_resized = Bitmap.createBitmap(m_bitmap, 0, 0, width_origin, height_origin, matrix, true);
        ByteArrayOutputStream stream_output = new ByteArrayOutputStream();
        bitmap_resized.compress(Bitmap.CompressFormat.PNG, 100, stream_output);

        Log.d("wrinkle", String.valueOf(bitmap_resized.getWidth()));
        Log.d("wrinkle", String.valueOf(bitmap_resized.getHeight()));

        //????????? ?????? ??????
        Tensor input_tensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap_resized, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);
        Tensor output_tensor = m_module.forward(IValue.from(input_tensor)).toTensor();
        float[] output_array = output_tensor.getDataAsFloatArray();

        Bitmap bitmap_predict = Bitmap.createBitmap(bitmap_resized.getWidth(), bitmap_resized.getHeight(), Bitmap.Config.ARGB_8888);

        float[][] temp_array = new float[256][256];
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                temp_array[i][j] = output_array[(j * 256) + i];
                if (temp_array[i][j] > 0.15f) {
                    bitmap_predict.setPixel(i, j, Color.rgb(0xff, 0xff, 0xff));

                } else {
                    bitmap_predict.setPixel(i, j, Color.rgb(0x00, 0x00, 0x00));
                }
            }
        }
        int output=extract_wrinkle(bitmap_predict);
        return output;
    }
    private int extract_wrinkle(Bitmap bitmap_01){
        //???????????? ???????????? ?????? ?????? ?????? ?????????
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
        Log.d("????????? ?????? ??? = "," "+ black_pixel);
        //?????? ??????
        int level=11;
        int level_thd=5400;
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
        Intent take_picture_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//????????? ????????? ????????? intent??????

        if (take_picture_intent.resolveActivity(getPackageManager()) != null) {
            File photo_file = null;
            try {
                photo_file = createImageFile();
            } catch (IOException ex) {
            }
            if (photo_file != null) {
                Uri photo_uri = FileProvider.getUriForFile(this, "com.example.RNU.fileprovider", photo_file);//????????? uri??????(content:// URI ??????)
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
        m_current_wrinkle_path = image.getAbsolutePath();//?????? ???????????? ????????????
        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, 500, 600,
                matrix, true);
    }
    //???????????? ????????? ????????? ???????????? ??????
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            File file = new File(m_current_wrinkle_path);
                            Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= 29) {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                                try {
                                    bitmap = ImageDecoder.decodeBitmap(source);
                                    if (bitmap != null) {
                                        ExifInterface ei = new ExifInterface(m_current_wrinkle_path);
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
                                        img1.setImageBitmap(rotatedBitmap);
                                        transformScreen(0,capture_pore.class);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                    if (bitmap != null) {
                                        ExifInterface ei = new ExifInterface(m_current_wrinkle_path);
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
                                        img1.setImageBitmap(rotatedBitmap);
                                        transformScreen(0,capture_pore.class);
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
