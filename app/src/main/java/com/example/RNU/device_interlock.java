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
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class device_interlock extends AppCompatActivity {
    private static final String TAG = "MAIN";
    ImageButton freq1;
    ImageButton freq500;
    ImageButton freq300;
    ImageButton power1;
    ImageButton power2;
    ImageButton power3;
    ImageButton home;
    TextView textView;
    ImageView imageView;
    private BluetoothLeService mBluetoothLeService;
    ArrayList<Integer> setting = new ArrayList<>();
    Button btnSearch;
    Button btnconnect;
    List<BluetoothDevice> bluetoothDevices;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdatper;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private static final int REQUEST_PHONE_STATE = 1;
    private static final long SCAN_PERIOD = 100000;
    private static final int REQUEST_ENABLE_BT=1;

    ArrayList<Integer> setting2 = new ArrayList<>();
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Logs.e(TAG, "Unable to initialize Bluetooth");
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
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();


            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                imageView.setImageResource(R.drawable.bluetooth);//추후 이미지 변경 해야함
                Toast.makeText(context, mDeviceName + " connected", Toast.LENGTH_SHORT).show();
                Log.d("data =  " ," "+ intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Toast.makeText(context, "Bluetooth disconnected!", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                findGattServices(mBluetoothLeService.getSupportedGattServices());
                Toast.makeText(context, "Bluetooth SERVICES_DISCOVERED!", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                Toast.makeText(context, "Bluetooth DATA_AVAILABLE", Toast.LENGTH_SHORT).show();
                Log.d("data =  ", " " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_interlock);

        freq1 = findViewById(R.id.h1);
        freq500 = findViewById(R.id.h2);
        freq300 = findViewById(R.id.h3);
        power1 = findViewById(R.id.s1);
        power2 = findViewById(R.id.s2);
        power3 = findViewById(R.id.s3);
        home = findViewById(R.id.home);
        setting = getIntent().getIntegerArrayListExtra("setting");
        btnSearch = (Button) findViewById(R.id.button);

        textView = (TextView) findViewById(R.id.textView6);
//        checkForPhoneStatePermission();   //런타임 권한 체크
//        GetDevicesUUID(device_interlock.this); //아이디 받아오기
//        mHandler = new Handler();
//        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//        }
//        checkPermissions(device_interlock.this, this);
//        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdatper = bluetoothManager.getAdapter();
//        mBluetoothLeScanner = mBluetoothAdatper.getBluetoothLeScanner();
//        if(mBluetoothAdatper==null) {
//            Toast.makeText(this, R.string.error_bluetooth_not_supported,Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        if(mBluetoothAdatper == null || !mBluetoothAdatper.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//        }
//
//        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
//        scanLeDevice(true);
//        final byte[] byteSendData = new byte[]{0x61, 0x62, 0x62, 0x62};
//        btnconnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                makeChange(byteSendData);
//            }
//        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(device_interlock.this, ble.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                if (freq1.isSelected()) {
                    setting2.add(0, 1);
                } else if (freq500.isSelected()) {
                    setting2.add(0, 2);
                } else if (freq300.isSelected()) {
                    setting2.add(0, 3);
                }else{
                    setting2.add(0,0);
                }

                if (power1.isSelected()) {
                    setting2.add(1, 1);
                } else if (power2.isSelected()) {
                    setting2.add(1, 2);
                } else if (power3.isSelected()) {
                    setting2.add(1, 3);
                }else{
                    setting2.add(1,0);
                }//동작중도해야하는데 조율해봐야함

                if (setting2.get(0) == 0 || setting2.get(1)== 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "환경설정을 모두 해주세요!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    intent.putExtra("setting", setting);
                    intent.putExtra("setting2", setting2);
                    freq1.setSelected(false);
                    freq300.setSelected(false);
                    freq500.setSelected(false);
                    power1.setSelected(false);
                    power2.setSelected(false);
                    power3.setSelected(false);
                    startActivity(intent);
                }

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(device_interlock.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
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

    //MainActivity가 시작되면 BluetoothLeService에 연결을 위해 ServiceConnection 인터페이스를 구현하는 객체를 생성
    //블뤁투스 서비스 클래스 안에 connect 메소드의 인자로 선택한 디바이스의 주소를 넘겨준다.

//    BluetoothLeService의 bradcastUpdate 메소드로부터 전달받은 intent객체를 통해
//    블루투스의 연결상태와 블루투스에서 제공하는 서비스 그리고 수신받은 데이터를 넘겨받는다.

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
//        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
//        final String tmDevice, tmSerial, androidId;
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return "Fail to Get UUID";
//        }
//        tmDevice = "" + tm.getDeviceId();
//        tmSerial = "" + tm.getSimSerialNumber();
//        androidId = "" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
//        String deviceId = deviceUuid.toString();
//        Log.d("UUID_generater", deviceId);
//        return deviceId;
    }
    private void checkForPhoneStatePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(device_interlock.this,
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(device_interlock.this,
                        Manifest.permission.READ_PHONE_STATE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    showPermissionMessage();

                } else {

                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(device_interlock.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            REQUEST_PHONE_STATE);
                }
            }
        } else {
            //... Permission has already been granted, obtain the UUID
            Log.d("UUID_generater", "deviceId");
            GetDevicesUUID(device_interlock.this);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//        if(mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            Logs.d(TAG, "Connect request reuslt = " + result);
//        }
//
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        scanLeDevice(false);
//    }
//    @Override
//    protected void onDestroy() {
//
//        super.onDestroy();
//        scanLeDevice(false);
//        unbindService(mServiceConnection);
//        mBluetoothLeService = null;
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public static void checkPermissions(Activity activity, Context context) {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_PRIVILEGED
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
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

                    //콜백메소드를 통해 받은 결과값의 디바이스 이름이 비교문 안에 이름과 똑같다면
                    //이름과 주소를 변수에 할당
                    if (("RN4871-2325").equals(deviceName)) {
                        mDeviceName = deviceName;
                        mDeviceAddress = deviceAddress;

                        //위에서 주소를 넘겨줘서 connect 되기 때문에 스캔을 하고있다면 중지
                        if (mScanning) {
                            mBluetoothLeScanner.stopScan(mLeScanCallback);
                            mScanning = false;
                        }
                    } else {
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
    private void findGattServices(List<BluetoothGattService> gattServices) {
        for (BluetoothGattService gattService : gattServices) {
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }
    }
    private void makeChange(byte [] out) {
        if(mConnected) {
            findGattServices(mBluetoothLeService.getSupportedGattServices());
            Log.d("123456 = " ," "+ out);
            characteristicTX.setValue(out);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
        }
    }
    private void displayData(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                textView.setText(data);
            }
        });
    }
    private void showPermissionMessage() {
        new AlertDialog.Builder(this)
                .setTitle("Read phone state")
                .setMessage("This app requires the permission to read phone state to continue")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(device_interlock.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                REQUEST_PHONE_STATE);
                    }
                }).create().show();
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