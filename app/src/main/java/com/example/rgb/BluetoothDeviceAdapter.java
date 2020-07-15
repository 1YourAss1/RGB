package com.example.rgb;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
    private final int TYPE_DEVICE = 0;
    private final int TYPE_CURRENT_DEVICE = 1;
    private List<BluetoothDevice> devices;
    private OnDeviceListener mOnDeviceListener;
    private Context context;
    private final String PREFERENCES = "rgb_preferences";
    private final String CURRENT_MAC = "current_mac";
    private String MAC;

    BluetoothDeviceAdapter(List<BluetoothDevice> devices, Context context, OnDeviceListener onDeviceListener) {
        this.devices = devices;
        this.MAC = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(CURRENT_MAC, "00:00:00:00:00:00");
        this.context = context;
        this.mOnDeviceListener = onDeviceListener;
    }

    @NonNull
    @Override
    public BluetoothDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        // Выбора шаблона для элемента по его типу
        switch (viewType){
            case TYPE_DEVICE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_device, parent, false);
                break;
            case TYPE_CURRENT_DEVICE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_current_device, parent, false);
                break;
        }
        return new ViewHolder(view, mOnDeviceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position){
        int type = getItemViewType(position);
        // Создание элемента изсходя из его типа
        switch (type){
            case TYPE_CURRENT_DEVICE:
                holder.currentDeviceNameTextView.setText(devices.get(position).getName());
                holder.currentDeviceAddressTextView.setText(devices.get(position).getAddress());
                break;
            case TYPE_DEVICE:
                holder.deviceNameTextView.setText(devices.get(position).getName());
                holder.deviceAddressTextView.setText(devices.get(position).getAddress());
                break;
        }
    }

    // Определение типа элемента
    @Override
    public int getItemViewType(int position) {
        MAC = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(CURRENT_MAC, "00:00:00:00:00:00");
        return devices.get(position).getAddress().equals(MAC) ? TYPE_CURRENT_DEVICE : TYPE_DEVICE;
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deviceNameTextView, deviceAddressTextView, currentDeviceNameTextView, currentDeviceAddressTextView;
        OnDeviceListener onDeviceListener;

        ViewHolder(final View view, OnDeviceListener onDeviceListener) {
            super(view);
            deviceNameTextView = view.findViewById(R.id.device_name);
            deviceAddressTextView = view.findViewById(R.id.device_address);
            currentDeviceNameTextView = view.findViewById(R.id.current_device_name);
            currentDeviceAddressTextView = view.findViewById(R.id.current_device_address);

            this.onDeviceListener = onDeviceListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onDeviceListener.onDeviceClick(getAdapterPosition());
        }
    }

    // Интерфейс слушателя нажатий
    public interface OnDeviceListener{
        void onDeviceClick(int position);
    }
}