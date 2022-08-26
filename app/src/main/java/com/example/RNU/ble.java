package com.example.RNU;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ble extends AppCompatActivity {

    private static final String TAG = "MAIN";

    private BluetoothAdapter mBluetoothAdatper;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean mScanning;
    private Handler mHandler;
    ImageButton home;
    private static final int REQUEST_ENABLE_BT=1;
    private static final long SCAN_PERIOD = 100000;
String txt;
    private String mDeviceName;
    private String mDeviceAddress;
    ImageView imageView;
    TextView textView7;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    //송수신 특성
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
   static int i ;
    TextView txt_ble_state, txt_ble_data;
    Button btn_data_send;
    Button btn_set_config;
    Button btn_get_status;
    Button btn_set_status;
    ArrayList<Integer> setting = new ArrayList<>();
    ArrayList<Integer> setting2 = new ArrayList<>();
    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    //MainActivity가 시작되면 BluetoothLeService에 연결을 위해 ServiceConnection 인터페이스를 구현하는 객체를 생성
    //블뤁투스 서비스 클래스 안에 connect 메소드의 인자로 선택한 디바이스의 주소를 넘겨준다.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if(!mBluetoothLeService.initialize()) {
                Logs.e(TAG,"Unable to initialize Bluetooth");
                finish();
            }
            //스캔을 통해 넘겨받은 블루투스 주소를 블루투스 서비스에 넘겨줘 연결을 시작한다.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    //BluetoothLeService의 bradcastUpdate 메소드로부터 전달받은 intent객체를 통해
    //블루투스의 연결상태와 블루투스에서 제공하는 서비스 그리고 수신받은 데이터를 넘겨받는다.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                txt_ble_state.setText("connect");
                Toast.makeText(context, mDeviceName+" connected", Toast.LENGTH_SHORT).show();
                // Log.d("data =  " ," "+ intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            } else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Toast.makeText(context, "Bluetooth disconnected!", Toast.LENGTH_SHORT).show();
            } else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action) && i == 0) {
                findGattServices(mBluetoothLeService.getSupportedGattServices());
                Toast.makeText(context, "Bluetooth SERVICES_DISCOVERED!", Toast.LENGTH_SHORT).show();
                i++;
            } else if(BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                Toast.makeText(context, "Bluetooth DATA_AVAILABLE", Toast.LENGTH_SHORT).show();
                Log.d("data =  " ," "+ intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
    private static final int REQUEST_PHONE_STATE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        home = findViewById(R.id.home);
        setting = getIntent().getIntegerArrayListExtra("setting");
        setting2 = getIntent().getIntegerArrayListExtra("setting2");
        txt_ble_state = (TextView)findViewById(R.id.txt_ble_state);
        txt_ble_data = (TextView)findViewById(R.id.txt_ble_data);
        btn_data_send = (Button)findViewById(R.id.btn_data_send);
        btn_set_config  = (Button)findViewById(R.id.button2);
        btn_get_status = (Button)findViewById(R.id.button3) ;
        btn_set_status = (Button)findViewById(R.id.button4);
        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("시간: %s");
        //런타임 권한 체크
        checkForPhoneStatePermission();
        textView7 = findViewById(R.id.textView7);
        //아이디 받아오기
        GetDevicesUUID(ble.this);

        mHandler = new Handler();

        //블루투스 기능을 지원하는지 체크
        if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported,Toast.LENGTH_SHORT).show();
            finish();
        }

        checkPermissions(ble.this, this);

        //블루투스 어댑터 설정
        final BluetoothManager bluetoothManager= (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdatper=bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdatper.getBluetoothLeScanner();


        if(mBluetoothAdatper==null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if(mBluetoothAdatper == null || !mBluetoothAdatper.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        scanLeDevice(true);

        //서비스 시작
        //클라이언트-서버 와 같이 동작. 서비스(BluetoothLeService.class)가 서버 역할을 수행
        //Activity는 BluetoothLeService에 요청을 할 수 있고, 어떠한 결과를 받을 수 있음
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        //final char[] cr= new char[]{(char)0x61, (char)0x62,(char)0x63,(char)0x64};
        //final int [] senddata = new int[]{97,98,98,99};
        //final byte[] byteSendData = new byte[]{0x61,0x62,0x62,0x62};
        final byte[] byteSendData = new byte[]{(byte) 0xA0, 0x00, 0x00, 0x20};//init 기기의 상태 확인 가능
        final byte[] byteSendData_311 = new byte[]{(byte) 0xA0, 0x01, 0x33, 0x12}; //set config 3분, 음성 ON, 테라피 ON  0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_310= new byte[]{(byte) 0xA0, 0x01, 0x32, 0x13}; //set config 3분, 음성 ON, 테라피 OFF  0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_301 = new byte[]{(byte) 0xA0, 0x01, 0x31, 0x10}; //set config 3분, 음성 OFF, 테라피 ON  0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_300 = new byte[]{(byte) 0xA0, 0x01, 0x30, 0x11}; //set config 3분, 음성 OFF, 테라피 OFF 0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_511 = new byte[]{(byte) 0xA0, 0x01, 0X53, 0x72}; //set config 5분, 음성 ON, 테라피 ON  0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_510 = new byte[]{(byte) 0xA0, 0x01, 0x52, 0x73}; //set config 5분, 음성 ON, 테라피 OFF 0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_501 = new byte[]{(byte) 0xA0, 0x01, 0x51, 0x70}; //set config 5분, 음성 OFF, 테라피 ON  0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_500 = new byte[]{(byte) 0xA0, 0x01, 0x50,  0x71}; //set config 5분, 음성 OFF, 테라피 OFF  0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_1511 = new byte[]{(byte) 0xA0, 0x01, (byte) 0xF3,(byte) 0x52}; //set config 15분, 음성 ON, 테라피 ON 0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_1510 = new byte[]{(byte) 0xA0, 0x01, (byte) 0xF2, (byte) 0x53}; //set config 15분, 음성 ON, 테라피 OFF 0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_1501 = new byte[]{(byte) 0xA0, 0x01, (byte) 0xF1, (byte) 0x50}; //set config 15분, 음성 OFF, 테라피 ON  0    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_1500 = new byte[]{(byte) 0xA0, 0x01, (byte) 0xF0, (byte) 0x51}; //set config 15분, 음성 OFF, 테라피 OFF O    돌아오면  동작시간은 몇분 카운트 , 음성안내 , 테라피가 켜져있다꺼져있따.
        final byte[] byteSendData_getstatus = new byte[]{(byte) 0xA0, 0x02, 0x00, 0x22}; //get status
        // final byte[] byteSendData = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x8D, 0x2E}; //set status 동작중, 300kHz, 1단계
        // final byte[] byteSendData = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x8E, 0x2D}; //set status 동작중, 300kHz, 2단계
        // final byte[] byteSendData = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x8F, 0x2F}; //set status 동작중, 300kHz, 3단계
        // final byte[] byteSendData = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x89, 0x2A}; //set status 동작중, 500kHz, 1단계
        // final byte[] byteSendData = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x8A, 0x29}; //set status 동작중, 500kHz, 2단계
        // final byte[] byteSendData = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x8B, 0x28}; //set status 동작중, 500kHz, 3단계
        // final byte[] byteSendData = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x85, 0x26}; //set status 동작중, 1MHz, 1단계
        // final byte[] byteSendData = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x86, 0x25}; //set status 동작중, 1MHz, 2단계
        // final byte[] byteSendData = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x87, 0x24}; //set status 동작중, 1MHz, 3단계
        final byte[] byteSendData_031 = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x0D, 0x2E}; //set status 대기, 300kHz, 1단계  0
        final byte[] byteSendData_032 = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x0E, 0x2D}; //set status 대기, 300kHz, 2단계  0
        final byte[] byteSendData_033 = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x0F, 0x2C}; //set status 대기, 300kHz, 3단계  0
        final byte[] byteSendData_021 = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x09, 0x2A}; //set status 대기, 500kHz, 1단계  0
        final byte[] byteSendData_022 = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x0A, 0x29}; //set status 대기, 500kHz, 2단계  0
        final byte[] byteSendData_023 = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x0B, 0x28}; //set status 대기, 500kHz, 3단계  0
        final byte[] byteSendData_011 = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x05, 0x26}; //set status 대기, 1MHz, 1단계    0
        final byte[] byteSendData_012 = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x06, 0x25}; //set status 대기, 1MHz, 2단계    0
        final byte[] byteSendData_013 = new byte[]{(byte) 0xA0 , 0x03, (byte) 0x07, 0x24}; //set status 대기, 1MHz, 3단계    0


        // final byte[] stringSendData = new byte[]{(byte)'a',(byte)'b',(byte)'c',(byte)'d'};
        // final String sendmessage = "hbcd";
        Log.d("123 = " ," "+ Arrays.toString(byteSendData));
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ble.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        btn_data_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String senddata = editText.getText().toString();
                //    Log.d("senddata" ," "+ senddata);
                Log.d("init",""+byteSendData);
                makeChange(byteSendData);
            }
        });
        btn_get_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("get_status",""+byteSendData_getstatus);
                makeChange(byteSendData_getstatus);
            }
        });
        btn_set_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setting.get(0)==3 && setting.get(1)==1 && setting.get(2)==1){
                    Log.d("전송 바이트311",""+byteSendData_311);
                    makeChange(byteSendData_311);

                }else if(setting.get(0)==3 && setting.get(1)==1 && setting.get(2)==0){
                    Log.d("전송 바이트310",""+byteSendData_310);
                    makeChange(byteSendData_310);
                }else if(setting.get(0)==3 && setting.get(1)==0 && setting.get(2)==0){
                    Log.d("전송 바이트300",""+byteSendData_300);
                    makeChange(byteSendData_300);
                }else if(setting.get(0)==3 && setting.get(1)==0 && setting.get(2)==1){
                    Log.d("전송 바이트301",""+byteSendData_301);
                    makeChange(byteSendData_301);
                }else if(setting.get(0)==5 && setting.get(1)==1 && setting.get(2)==1){
                    Log.d("전송 바이트511",""+byteSendData_511);
                    makeChange(byteSendData_511);
                }else if(setting.get(0)==5 && setting.get(1)==1 && setting.get(2)==0){
                    Log.d("전송 바이트510",""+byteSendData_510);
                    makeChange(byteSendData_510);
                }else if(setting.get(0)==5 && setting.get(1)==0 && setting.get(2)==1){
                    Log.d("전송 바이트501",""+byteSendData_501);
                    makeChange(byteSendData_501);
                }else if(setting.get(0)==5 && setting.get(1)==0 && setting.get(2)==0){
                    Log.d("전송 바이트500",""+byteSendData_500);
                    makeChange(byteSendData_500);
                }else if(setting.get(0)==15 && setting.get(1)==1 && setting.get(2)==1){
                    Log.d("전송 바이트1511",""+byteSendData_1511);
                    makeChange(byteSendData_1511);
                }else if(setting.get(0)==15 && setting.get(1)==1 && setting.get(2)==0){
                    Log.d("전송 바이트1510",""+byteSendData_1510);
                    makeChange(byteSendData_1510);
                }else if(setting.get(0)==15 && setting.get(1)==0 && setting.get(2)==1){
                    Log.d("전송 바이트1501",""+byteSendData_1501);
                    makeChange(byteSendData_1501);
                }else if(setting.get(0)==15 && setting.get(1)==0 && setting.get(2)==0){
                    Log.d("전송 바이트1500",""+byteSendData_1500);
                    makeChange(byteSendData_1500);
                }
                if(!running){
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    running = true;
                }
                running = false;
            }
        });
        btn_set_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setting.get(3)==1 && setting.get(4)==1  ){
                    Log.d("전송 바이트011",""+byteSendData_011);
                    makeChange(byteSendData_011);
                }else if(setting.get(3)==1 && setting.get(4)==2  ){
                    Log.d("전송 바이트012",""+byteSendData_012);
                    makeChange(byteSendData_012);
                }else if(setting.get(3)==1 && setting.get(4)==3  ){
                    Log.d("전송 바이트013",""+byteSendData_013);
                    makeChange(byteSendData_013);
                }else if(setting.get(3)==2 && setting.get(4)==1  ){
                    Log.d("전송 바이트021",""+byteSendData_021);
                    makeChange(byteSendData_021);
                }else if(setting.get(3)==2 && setting.get(4)==2  ){
                    Log.d("전송 바이트022",""+byteSendData_022);
                    makeChange(byteSendData_022);
                }else if(setting.get(3)==2 && setting.get(4)==3  ){
                    Log.d("전송 바이트023",""+byteSendData_023);
                    makeChange(byteSendData_023);
                }else if(setting.get(3)==3 && setting.get(4)==1  ){
                    Log.d("전송 바이트031",""+byteSendData_031);
                    makeChange(byteSendData_031);
                }else if(setting.get(3)==3 && setting.get(4)==2  ){
                    Log.d("전송 바이트032",""+byteSendData_032);
                    makeChange(byteSendData_032);
                }else if(setting.get(3)==3 && setting.get(4)==3  ){
                    Log.d("전송 바이트033",""+byteSendData_033);
                    makeChange(byteSendData_033);
                }

            }
        });

    }

    private String GetDevicesUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        UUID deviceUuid;
        String deviceId = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "Fail to Get UUID";
        }
        Log.d("sdk = ",""+ + Build.VERSION_CODES.Q);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceUuid = new UUID(-0x121074568629b532L,-0X5c37d8232ae2de13L);
            try{
                MediaDrm mediaDrm = new MediaDrm(deviceUuid);
                deviceId = android.util.Base64.encodeToString(mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID),0).trim();
                Log.d("uuid_device"," " + deviceId);
            }catch (UnsupportedSchemeException e){
                e.printStackTrace();
            }
            return deviceId;
        }else{
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
            Log.d("UUID_generater123123"," " + deviceUuid);
             deviceId = deviceUuid.toString();
            Log.d("uuid_device"," " + deviceId);
            return deviceId;
        }
    }

    private void checkForPhoneStatePermission(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(ble.this,
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ble.this,
                        Manifest.permission.READ_PHONE_STATE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    showPermissionMessage();

                } else {

                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(ble.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            REQUEST_PHONE_STATE);
                }
            }
        }else{
            //... Permission has already been granted, obtain the UUID
            Log.d("UUID_generater", "deviceId");
            GetDevicesUUID(ble.this);
        }
    }


    private void showPermissionMessage(){
        new AlertDialog.Builder(this)
                .setTitle("Read phone state")
                .setMessage("This app requires the permission to read phone state to continue")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ble.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                REQUEST_PHONE_STATE);
                    }
                }).create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if(mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Logs.d(TAG, "Connect request reuslt = " + result);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanLeDevice(false);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //사용자에게 권한 허용에 대해 물어봄
    public static void checkPermissions(Activity activity, Context context) {
        int PERMISSION_ALL =1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_PRIVILEGED
        };

        if(!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if(context != null && permissions != null) {
            for(String permission: permissions) {
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //BLE 디바이스 스캔
    private void scanLeDevice(final boolean enable) {
        if(enable) {
            //별도 종료시켜주는 방법이 없기 때문에 일정시간 경과 후 종료
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothLeScanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothLeScanner.startScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    ScanCallback mLeScanCallback = new ScanCallback() {
        String deviceName;
        String deviceAddress;
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            BluetoothDevice device = result.getDevice();
            deviceName = device.getName();
            deviceAddress = device.getAddress();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(("RN4871-2325").equals(deviceName)) {
                        mDeviceName=deviceName;
                        mDeviceAddress=deviceAddress;
                        if(mScanning) {
                            mBluetoothLeScanner.stopScan(mLeScanCallback);
                            mScanning = false;
                        }
                    }
                    else {
                        Logs.d("onConnectFailed");
                    }
                }
            });
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Logs.d("onScanFailed(", errorCode + "");
        }
    };

    //블루투스에서 지원하는 서비스 값을 돌며 송수신 특성을 찾는다.
    private void findGattServices(List<BluetoothGattService> gattServices) {
        for(BluetoothGattService gattService: gattServices) {

            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }
    }

    //data를 write 해주는 특성을 통해 data를 보내준다.
    private void makeChange(byte [] out) {
        if(mConnected) {
            findGattServices(mBluetoothLeService.getSupportedGattServices());
            characteristicTX.setValue(out);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
        }
    }


    private void displayData(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_ble_data.setText(data);
                String txt2= (String) txt_ble_data.getText();
                txt = txt2.substring(4,6);
                if(txt.contains("32")){
                    textView7.setText("동작시간은 3분, 음성안내 ON, 테라피 OFF 동작!");
                }else if(txt.contains("33")) {
                    textView7.setText("동작시간은 3분, 음성안내 ON, 테라피 ON 동작!");
                }else if(txt.contains("31")){
                    textView7.setText("동작시간은 3분, 음성안내 OFF, 테라피 ON 동작!");
                }else if(txt.contains("30")){
                    textView7.setText("동작시간은 3분, 음성안내 OFF, 테라피 OFF 동작!");
                }else if(txt.contains("53")){
                    textView7.setText("동작시간은 5분, 음성안내 ON, 테라피 ON 동작!");
                }else if(txt.contains("52")){
                    textView7.setText("동작시간은 5분, 음성안내 ON, 테라피 OFF 동작!");
                }else if(txt.contains("51")){
                    textView7.setText("동작시간은 5분, 음성안내 OFF, 테라피 ON 동작!");
                }else if(txt.contains("50")){
                    textView7.setText("동작시간은 5분, 음성안내 OFF, 테라피 OFF 동작!");
                }else if(txt.contains("F3")){
                    textView7.setText("동작시간은 15분, 음성안내 ON, 테라피 ON 동작!");
                }else if(txt.contains("F2")){
                    textView7.setText("동작시간은 15분, 음성안내 ON, 테라피 OFF 동작!");
                }else if(txt.contains("F1")){
                    textView7.setText("동작시간은 15분, 음성안내 OFF, 테라피 ON 동작!");
                }else if(txt.contains("F0")){
                    textView7.setText("동작시간은 15분, 음성안내 OFF, 테라피 OFF 동작!");
                }else if(txt.contains("85")){
                    textView7.setText("주파수 1MHz, 동작세기 1단계로 작동중 입니다");
                }else if(txt.contains("89")){
                    textView7.setText("주파수 500kHz, 동작세기 1단계로 작동중 입니다");
                }else if(txt.contains("8D")){
                    textView7.setText("주파수 3kHz, 동작세기 1단계로 작동중 입니다");
                }else if(txt.contains("86")){
                    textView7.setText("주파수 1MHz, 동작세기 2단계로 작동중 입니다");
                }else if(txt.contains("8A")){
                    textView7.setText("주파수 500kHz, 동작세기 2단계로 작동중 입니다");
                }else if(txt.contains("8E")){
                    textView7.setText("주파수 300kHz, 동작세기 2단계로 작동중 입니다");
                }else if(txt.contains("87")){
                    textView7.setText("주파수 1MHz, 동작세기 3단계로 작동중 입니다");
                }else if(txt.contains("8B")){
                    textView7.setText("주파수 500kHz, 동작세기 3단계로 작동중 입니다");
                }else if(txt.contains("8F")){
                    textView7.setText("주파수 300kHz, 동작세기 3단계로 작동중 입니다");
                }else if(txt.contains("05")){
                    textView7.setText("주파수 1MHz, 동작세기 1단계로 동작 명령 하였습니다");
                }else if(txt.contains("09")){
                    textView7.setText("주파수 500kHz, 동작세기 1단계로 동작 명령 하였습니다");
                }else if(txt.contains("0D")){
                    textView7.setText("주파수 3kHz, 동작세기 1단계로 동작 명령 하였습니다");
                }else if(txt.contains("06")){
                    textView7.setText("주파수 1MHz, 동작세기 2단계로 동작 명령 하였습니다");
                }else if(txt.contains("0A")){
                    textView7.setText("주파수 500kHz, 동작세기 2단계로 동작 명령 하였습니다");
                }else if(txt.contains("0E")){
                    textView7.setText("주파수 300kHz, 동작세기 2단계로 동작 명령 하였습니다");
                }else if(txt.contains("07")){
                    textView7.setText("주파수 1MHz, 동작세기 3단계로 동작 명령 하였습니다");
                }else if(txt.contains("0B")){
                    textView7.setText("주파수 500kHz, 동작세기 3단계로 동작 명령 하였습니다");
                }else if(txt.contains("0F")){
                    textView7.setText("주파수 300kHz, 동작세기 3단계로 동작 명령 하였습니다");
                }
            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}