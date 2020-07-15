package com.example.rgb;

import android.bluetooth.BluetoothDevice;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements BluetoothDeviceAdapter.OnDeviceListener {
    private final String PREFERENCES = "rgb_preferences";
    private final String CURRENT_MAC = "current_mac";
    private Switch themeSwitch;
    private List<BluetoothDevice> listDevices;
    private RecyclerView devicesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Текущая тема
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        // Если темная, то свич - тру
        themeSwitch = findViewById(R.id.switch_theme);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) themeSwitch.setChecked(true);
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putInt("theme mode", AppCompatDelegate.MODE_NIGHT_YES).apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putInt("theme mode", AppCompatDelegate.MODE_NIGHT_NO).apply();
                }
            }
        });

        // Добавлятся ToolBar для настроек
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        // Добавляется кнопка "назад"
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Список устройств
        listDevices = new Bluetooth().getPairedDevices();
        for (int i = 0; i < listDevices.size(); i++) {
            if (listDevices.get(i).getAddress().equals(getSharedPreferences(PREFERENCES, MODE_PRIVATE).getString(CURRENT_MAC, "00:00:00:00:00:00"))) {
                Collections.swap(listDevices, i, 0);
            }
        }

        devicesRecyclerView = findViewById(R.id.recycler_view_devices);
        BluetoothDeviceAdapter bluetoothDeviceAdapter = new BluetoothDeviceAdapter(listDevices, this, this);
        devicesRecyclerView.setAdapter(bluetoothDeviceAdapter);
    }

    // Обработка нажатия кнопки "назад"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Реализация нажатия на устройство
    @Override
    public void onDeviceClick(int position) {
        getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putString(CURRENT_MAC, listDevices.get(position).getAddress()).apply();
        BluetoothDevice item = listDevices.get(position);
        listDevices.remove(position);
        listDevices.add(0, item);
        if (devicesRecyclerView.getAdapter() != null) {
            devicesRecyclerView.getAdapter().notifyItemMoved(position, 0);
        }
    }

}