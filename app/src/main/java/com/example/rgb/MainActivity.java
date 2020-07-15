package com.example.rgb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SendPackage {
    private FragmentManager fragmentManager;
    private EmptyFragment emptyFragment;
    private SharedPreferences sharedPreferences;
    private Bluetooth myBluetooth;
    private boolean isConnected = false;
    private int[] RGB = {0,0,0}, HSV = {0,0,0};
    private int interval = 5000, step = 1;

    // Присвоение полученных от Arduino данных в локальные переменные
    private void SetOptions(int[] options) {
        RGB = Arrays.copyOfRange(options, 0, 3);
        HSV = Arrays.copyOfRange(options, 3, 6);
        interval = options[6]; step = options[7];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Установка светлой/темной темы из настроек
        sharedPreferences = getSharedPreferences("rgb_preferences", MODE_PRIVATE);
        int theme_mode =  sharedPreferences.getInt("theme mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(theme_mode);

        setContentView(R.layout.activity_main);

        // Добавлятся основной ToolBar
        final Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // Принятие и обработка данных из BT соединения
        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:                                                                         // Если пришло сообщение об успешном подключении, то
                        isConnected = true;                                                         // Поднять флаг
                        toolbar.getMenu().findItem(R.id.menu_bluetooth).setEnabled(true);           // Включить кнопку "Bluetooth"
                        Toast.makeText(getApplicationContext(),                                     // Сообщение об успешном покдлючении
                                    "Подключено успешно",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        break;
                    case -1:                                                                        // Если пришло сообщение об ошибке подключения, то
                        toolbar.getMenu().findItem(R.id.menu_bluetooth).setEnabled(true);           // Включить кнопку "Bluetooth"
                        Toast.makeText(getApplicationContext(),                                     // Сообщение об ошибке подключения
                                "Ошибка одключения",
                                Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:                                                                        // Иначе пришло сообщение с данными от Arduino, тогда
                        SetOptions((int[]) msg.obj);                                                // Присвоить полученные значение локальным переменным
                        ModeFragment modeFragment = ModeFragment.newInstance(                       // Создать основнйо фрагмент и передать в него полученные значеня
                                msg.what,
                                RGB,
                                HSV,
                                interval,
                                step);
                        fragmentManager.beginTransaction()                                          // Отобразить созданный фрагмент
                                .replace(R.id.frgmCont, modeFragment)
                                .commit();
                        break;
                }
            }
        };

        // Инициализация и проверка BT
        myBluetooth = new Bluetooth(handler);
        myBluetooth.SetUpBluetooth(this);

        // Установка фаргмента-заглушки по дефолту
        fragmentManager = getSupportFragmentManager();
        emptyFragment = new EmptyFragment();
        fragmentManager.beginTransaction().add(R.id.frgmCont, emptyFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_bluetooth:                                                               // Обработка нажатия кнопки "Bluetooth"
                // MAC из настроек
                String MAC = sharedPreferences.getString("current_mac", "00:00:00:00:00:00");
                if (!isConnected) {                                                                 // Если не подключено, то
                    myBluetooth.Connect(MAC);                                                       // Начать подключение
                    item.setEnabled(false);                                                         // Отключить кнопку
                } else {                                                                            // Иначе
                    myBluetooth.Disconnect();                                                       // Отключиться
                    fragmentManager                                                                 // Отобразить "заглушку - нет подключения"
                            .beginTransaction()
                            .replace(R.id.frgmCont, emptyFragment)
                            .commit();
                    isConnected = false;                                                            // Опустить флаг
                }
                return true;
            case R.id.menu_settings:                                                                // Обработка нажатия кнопки "Settings"
                if (isConnected) {                                                                  // Если подклено, то отключиться
                    myBluetooth.Disconnect();
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.frgmCont, emptyFragment)
                            .commit();
                    isConnected = false;
                }
                Intent intent = new Intent(this, SettingsActivity.class);             // Открыть активити настроек
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Реализация интерфейса SendPackage
    @Override
    public void sendPackage(int mode, int[] val) {
        if (myBluetooth != null) {
            myBluetooth.SendPackage(mode, val);
        }
    }
}


