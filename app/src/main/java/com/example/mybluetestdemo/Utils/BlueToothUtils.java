package com.example.mybluetestdemo.Utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//蓝牙工具类
public class BlueToothUtils {
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> discoveredDevices;
    private List<BluetoothDevice> pairedDevicesList;
    private OnDeviceDiscoveredListener listener;
    // 新增字段：记录已连接的蓝牙设备
    private BluetoothDevice connectedDevice;
    // 内部私有字段用于存储连接状态监听器
//    private OnDeviceConnectedListener deviceConnectedListener;

    public BlueToothUtils(Context context, OnDeviceDiscoveredListener listener) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.discoveredDevices = new ArrayList<>();
        this.pairedDevicesList = new ArrayList<>();
        this.listener = listener;
    }

    //添加发现设备接口
    public interface OnDeviceDiscoveredListener {
        void onDeviceDiscovered(BluetoothDevice device);//发现设备
        void onPairDeviceFound(BluetoothDevice device);//获取已配对设备接口回调

    }

//    // 新增接口回调方法：当设备连接成功时调用
//    public interface OnDeviceConnectedListener {
//        void onDeviceConnected(BluetoothDevice device);
//    }
//
//    // 添加设置连接状态监听器的方法
//    public void setOnDeviceConnectedListener(OnDeviceConnectedListener listener) {
//        this.deviceConnectedListener = listener;
//    }

    //打开蓝牙
    public boolean openBlueTooth() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return bluetoothAdapter.enable();
        }
        return true;//如果蓝牙已开启，则返回true
    }

    //检查蓝牙是否开启
    public boolean isBlueTooth() {
        return bluetoothAdapter.isEnabled();
    }

    //扫描蓝牙
    public void startScanAndAddToList() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // 注册广播接收器监听扫描结果
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(bluetoothFoundReceiver, filter);
            getBlueDevices();
            // 开始扫描
            bluetoothAdapter.startDiscovery();
        } else {
            throw new IllegalStateException("Bluetooth is not enabled");
        }
    }

    //获取已配对的蓝牙设备
    private void getBlueDevices() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            LogUtils.e("大小：" + pairedDevices.size());
            if (pairedDevices.size() > 0) {
                // 将已配对设备添加到列表中
                for (BluetoothDevice device : pairedDevices) {
                    pairedDevicesList.add(device);
                    // 如果你希望在发现已配对设备时也回调，则可以在这里调用回调接口
                    listener.onPairDeviceFound(device);
                }
            } else {

            }
        } else {
        }

    }
    // 定义蓝牙Gatt回调来处理连接状态变化
//    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            super.onConnectionStateChange(gatt, status, newState);
//
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                // 设备连接成功
//                connectedDevice = gatt.getDevice();
//                if (deviceConnectedListener != null) {
//                    deviceConnectedListener.onDeviceConnected(connectedDevice);
//                }
//            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                // 设备断开连接
//                connectedDevice = null;
//            }
//        }
//
//        // 其他必要的回调方法...
//    };
    // 获取已连接的蓝牙设备
    public BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }

    //注册蓝牙扫描广播
    private final BroadcastReceiver bluetoothFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获取新发现的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //获取以前配对的设备
                //获取已经配对成功的设备

                if (device != null && !discoveredDevices.contains(device)) {
                    discoveredDevices.add(device);
                    listener.onDeviceDiscovered(device);
                }
            }
        }
    };

    //停止扫描
    public List<BluetoothDevice> stopScanAndGetDiscoveredDevices() {
        // 取消注册广播接收器
        context.unregisterReceiver(bluetoothFoundReceiver);

        // 停止蓝牙设备扫描
        bluetoothAdapter.cancelDiscovery();

        return discoveredDevices;
    }

}
