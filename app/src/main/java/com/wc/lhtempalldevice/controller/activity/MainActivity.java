package com.wc.lhtempalldevice.controller.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.wc.lhtempalldevice.R;
import com.wc.lhtempalldevice.model.TempBean;
import com.wc.lhtempalldevice.service.BluetoothLeService;
import com.wc.lhtempalldevice.view.DeviceAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rnd on 2017/5/9.
 *
 * 主界面用来控制 多个蓝牙设备的连接
 *
 * 使用前
 * 必读：
 * 1.在连接多个的时候 请你注意以下几点 我这个是温度数据的 所以在读取数据转换的时候跟你们的不一样 相应的修改HTMParseUtil代码即可
 *
 * 2.此代码没有写入到蓝牙的数据 如果需要写入 方法只要你理解了此代码 一样用for循环去写入
 *
 * 3. 有什么不懂的地方可以联系我qq278161009 问题答案: 123
 *
 *
 */

public class MainActivity extends BaseActivity{
    private ListView devicelist;
    //蓝牙常用的
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeService mBluetoothLeService = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private boolean mScanning;
    private LinkedList<BluetoothDevice> mDeviceContainer = new LinkedList<BluetoothDevice>();
    public static ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    private final static String TAG = MainActivity.class.getSimpleName();

    private List<TempBean> addss = new ArrayList<>();

    private DeviceAdapter deviceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        iniBle();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        Log.d(TAG, "Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void initView(){
        devicelist = (ListView) findViewById(R.id.device_list);

    }

    private void iniBle() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(new Runnable() {
            public void run() {
                if (mBluetoothAdapter.isEnabled()){
                    scanLeDevice(true);
                    mScanning = true;
                }else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        }).start();
    }


    private void clearDevice() {
        mBluetoothLeService.disconnect();
        mDeviceContainer.clear();
        mDeviceList.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(SCAN_PERIOD);

                        if(mScanning)
                        {
                            mScanning = false;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            invalidateOptionsMenu();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        //invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        scanLeDevice(true);
        mScanning = true;
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mDeviceContainer.isEmpty()) {
                                if(!isEquals(device)){
                                    connectBle(device);
                                }
                            }else{
                                connectBle(device);
                            }
                        }
                    });
                }
            };

    private boolean isEquals(BluetoothDevice device){
        for(BluetoothDevice mDdevice: mDeviceContainer){
            if(mDdevice.equals(device)){
                return true;
            }
        }
        return false;
    }

    private void connectBle(BluetoothDevice device) {
        mDeviceContainer.add(device);
        while (true) {
            if (mBluetoothLeService != null) {
                //新增了设备的名字
                mBluetoothLeService.connect(device.getName(),device.getAddress(),this);
                break;
            } else {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            Log.e(TAG, "mBluetoothLeService is okay");
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.e(TAG, "Only gatt, just wait");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if (!mDeviceList.isEmpty()) {
                    String strAddress = intent.getStringExtra("DEVICE_ADDRESS");
                    if(removeDevice(strAddress)){
                        //int deviceNum = mDeviceList.size()-1;
                        //numDevice.setText(deviceText + deviceNum);
                    }
                }

                //invalidateOptionsMenu();
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                if (!mDeviceContainer.isEmpty()) {
                    String strAddress = intent.getStringExtra("DEVICE_ADDRESS");
                    for(BluetoothDevice bluetoothDevice: mDeviceContainer){
                        if(bluetoothDevice.getAddress().equals(strAddress)){
                            mDeviceList.add(bluetoothDevice);
                        }
                    }
                }
                Log.e(TAG, "Discover GATT Services");
                //invalidateOptionsMenu();
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.i(TAG, "ACTION_DATA_AVAILABLE");
                addss = (List<TempBean>) intent.getSerializableExtra(BluetoothLeService.EXTRA_DATA);
                if (addss.size()>0) {
                    deviceAdapter = new DeviceAdapter(MainActivity.this, addss);
                    devicelist.setAdapter(deviceAdapter);
                    deviceAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private boolean removeDevice(String strAddress) {
        for(final BluetoothDevice bluetoothDevice:mDeviceList){
            if(bluetoothDevice.getAddress().equals(strAddress)){
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(250);
                            mDeviceList.remove(bluetoothDevice);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);

        if(mBluetoothLeService != null)
        {
            mBluetoothLeService.close();
            mBluetoothLeService = null;
        }

        Log.i(TAG, "MonitorActivity closed!!!");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
