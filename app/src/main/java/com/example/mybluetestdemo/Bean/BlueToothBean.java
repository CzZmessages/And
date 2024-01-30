package com.example.mybluetestdemo.Bean;
//蓝牙实体类
public class BlueToothBean {
    private String deviceName;
    private String deviceAddress;

    public BlueToothBean(String name, String address) {
        this.deviceName = name;
        this.deviceAddress = address;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
