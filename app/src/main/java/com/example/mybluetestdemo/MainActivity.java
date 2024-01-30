package com.example.mybluetestdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.example.commonlibrary.Qulick.QuickClickListener;
import com.example.mybluetestdemo.Adapter.BluetoothDeviceAdapter;
import com.example.mybluetestdemo.Bean.BlueToothBean;
import com.example.mybluetestdemo.Utils.BlueToothUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BlueToothUtils.OnDeviceDiscoveredListener, BluetoothDeviceAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private Button open, close, strat;
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;
    private BlueToothUtils blueToothUtils;
    private Handler handler = new Handler();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket connectedSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blueToothUtils = new BlueToothUtils(this, this);
        initView();
        initAdapter();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView_blue);
        open = findViewById(R.id.open_blue);
        close = findViewById(R.id.close_blue);
        strat = findViewById(R.id.start_ALL_blue);
        strat.setOnClickListener(quickClickListener);
        open.setOnClickListener(quickClickListener);
        close.setOnClickListener(quickClickListener);
    }

    private void initAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<BlueToothBean> blueToothBeanList = new ArrayList<>();
//        blueToothBeanList.add(new BlueToothBean("Device1","00:11:22:33:44"));
//        blueToothBeanList.add(new BlueToothBean("Device2","00:11:22:33:44"));
        bluetoothDeviceAdapter = new BluetoothDeviceAdapter(blueToothBeanList);
        bluetoothDeviceAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(bluetoothDeviceAdapter);
    }

    private void addListener() {

    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.open_blue:
                    LogUtils.e("打开蓝牙并扫描");
                    blueToothUtils.openBlueTooth();
                    handler.postDelayed(startScanRunnable, 2000);
                    break;
                case R.id.close_blue:
                    LogUtils.e("关闭蓝牙");
                    blueToothUtils.stopScanAndGetDiscoveredDevices();
                    break;
                case R.id.start_ALL_blue:
                    blueToothUtils.startScanAndAddToList();//扫描设备
                    break;
                default:
                    break;
            }
        }
    };

    private Runnable startScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (blueToothUtils.isBlueTooth()) {
                LogUtils.e("蓝牙开启可用");
                blueToothUtils.startScanAndAddToList();//扫描设备
            } else {
                LogUtils.e("蓝牙未开启或者不可用");
            }
        }
    };

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {
//        bluetoothDeviceAdapter.addDevice(new BlueToothBean(device.getName(), device.getAddress()));
//        bluetoothDeviceAdapter.notifyDataSetChanged(); // 更新RecyclerView显示
    }

    @Override
    public void onPairDeviceFound(BluetoothDevice device) {
        //已经配对的显示
        bluetoothDeviceAdapter.addDevice(new BlueToothBean(device.getName(), device.getAddress()));
        bluetoothDeviceAdapter.notifyDataSetChanged(); // 更新RecyclerView显示
    }

    //item点击事件
    @Override
    public void onItemClick(BlueToothBean device) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断蓝牙是否可用
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // 蓝牙不可用或未开启，请先打开蓝牙
            ThreadUtils.runOnUiThread(() -> Toast.makeText(this, "蓝牙不可用或未开启", Toast.LENGTH_SHORT).show());
        } else {
            //连接指定蓝牙
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    connectDevices(device.getDeviceAddress());
                }
            }).start();
        }
        LogUtils.e("设备名称:" + device.getDeviceName() + ",设备地址：" + device.getDeviceAddress());
    }
//切换设备
    private void updaterDevices(){

    }
    //更新数据
    //扫描数据
    //连接设备
    private void connectDevices(String address) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP服务UUID
        BluetoothSocket socket = null;
        ParcelUuid[] uuids = device.getUuids();
        // 创建一个BluetoothSocket以连接到设备，这里假设是RFCOMM协议
//        new Thread(new Runnable() {
//            @Override
//            public void run() {

        try {
            for (ParcelUuid uuid : uuids) {
                LogUtils.e("设备UUID：" + uuid);
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(String.valueOf(uuid)));
                socket.connect();
                connectedSocket = socket;
            }
            // 连接成功后在此处执行后续操作，如通知界面更新状态
            LogUtils.e("设备:" + device.getAddress() + "连接成功");
        } catch (IOException e) {
//                    Log.e(TAG, "Error connecting to the device", e);
            // 处理连接失败的情况

            LogUtils.e("设备:" + device.getAddress() + "连接失败" + "异常原因:" + e.getMessage());
            if (socket != null) {
                try {
                    socket.close();
                    LogUtils.e("关流");
                } catch (IOException ignored) {
                }
            }
            // 在这里可以考虑重新尝试连接，或者通知用户连接失败
        }
//            }
//        }).start();

    }
}