package com.example.mybluetestdemo.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybluetestdemo.Bean.BlueToothBean;
import com.example.mybluetestdemo.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BluetoothViewHolder> {
    private List<BlueToothBean> bluetoothDeviceList;
    private OnItemClickListener listener;

    public BluetoothDeviceAdapter(List<BlueToothBean> devices) {
        this.bluetoothDeviceList = new ArrayList<>(devices);
    }

    @NonNull
    @Override
    public BluetoothViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_device_item_layout, parent, false);
        return new BluetoothViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothViewHolder holder, int position) {
        BlueToothBean device = bluetoothDeviceList.get(position);
        holder.deviceNameTextView.setText(device.getDeviceName());
        holder.deviceAddressTextView.setText(device.getDeviceAddress());
        //添加点击事件监听
        holder.itemView.setOnClickListener(v -> {
            if(listener!=null){
                listener.onItemClick(bluetoothDeviceList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return bluetoothDeviceList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(BlueToothBean device);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener =  listener;
    }
    public static class BluetoothViewHolder extends RecyclerView.ViewHolder {
        TextView deviceNameTextView;
        TextView deviceAddressTextView;

        public BluetoothViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceNameTextView = itemView.findViewById(R.id.tv_device_name);
            deviceAddressTextView = itemView.findViewById(R.id.tv_device_address);
        }
    }

    // 添加 addDevice 方法
    public void addDevice(BlueToothBean device) {
        bluetoothDeviceList.add(device);
        notifyItemInserted(bluetoothDeviceList.size() - 1); // 告诉适配器在列表末尾添加了一个新项目
    }

    // 添加 replaceData 方法
    public void replaceData(List<BlueToothBean> newList) {
        // 先清空旧的数据
        this.bluetoothDeviceList.clear();

        // 添加新的数据
        this.bluetoothDeviceList.addAll(newList);

        // 通知适配器数据集已更改
        notifyDataSetChanged();
    }
}
