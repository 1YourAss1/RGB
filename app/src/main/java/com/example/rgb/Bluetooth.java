package com.example.rgb;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

class Bluetooth {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ConnectedThread mConnectedThread;
    private Handler handler;

    Bluetooth() {}

    Bluetooth(Handler handler) {
        this.handler = handler;
    }

    // Проверка поддержки и активности Bluetooth
    void SetUpBluetooth(Activity activity) {
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, "Uuups.. Device doesn't support Bluetooth!", Toast.LENGTH_LONG).show();
            activity.finish();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // Подключение к устройству
    void Connect(String MAC) {
        ConnectThread mConnectThread = new ConnectThread(bluetoothAdapter.getRemoteDevice(MAC));
        mConnectThread.start();
    }

    // Отключение от устройства
    void Disconnect() {
        mConnectedThread.cancel();
    }

    // Получение списка сопряженных устройств
    List<BluetoothDevice> getPairedDevices() {
        return new ArrayList<>(bluetoothAdapter.getBondedDevices());
    }

    // Отправка данных в формате "$int int int int;"
    void SendPackage(int mode, int[] data) {
        String packageData = "$" + mode + " " + data[0] + " " + data[1] + " " + data[2] + ";";
        mConnectedThread.write(packageData.getBytes());
    }

    // Класс-поток для установки соединения
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                Message readMsg = handler.obtainMessage(
                       0);
                readMsg.sendToTarget();
            } catch (IOException connectException) {
                Message readMsg = handler.obtainMessage(
                        -1);
                readMsg.sendToTarget();
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
            SendPackage(7, new int[]{0, 0, 0});
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Класс-поток для управления соединением
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] mmBuffer = new byte[1024];
            int numBytes;
            StringBuilder recDataString = new StringBuilder();
            while (true) {
                try {
                    numBytes = mmInStream.read(mmBuffer);                                           // Чтение байт из bluetooth соединения
                    String readMessage = new String(mmBuffer, 0, numBytes);                  // Преобразование в строку
                    recDataString.append(readMessage);                                              // Конкатинация полученных строк
                    int endOfLineIndex = recDataString.indexOf(";");                                // Индекс конечного символа
                    if (endOfLineIndex > 0) {                                                       // Если он больше нуля
                        if (recDataString.charAt(0) == '$') {                                       // И если первый символ = начальный символ, то
                            String[] strValues = recDataString                                      // То разделяем по значениям
                                    .substring(1, endOfLineIndex)
                                    .split(" ");
                            int mode = Integer.parseInt(strValues[0]);                              // Преобразобуем полученные значения для отправки в Handler
                            int[] val = {
                                    Integer.parseInt(strValues[1]),
                                    Integer.parseInt(strValues[2]),
                                    Integer.parseInt(strValues[3]),
                                    Integer.parseInt(strValues[4]),
                                    Integer.parseInt(strValues[5]),
                                    Integer.parseInt(strValues[6]),
                                    Integer.parseInt(strValues[7]),
                                    Integer.parseInt(strValues[8])};
                            Message readMsg = handler.obtainMessage(                                // Подготовка сообщения для отправки
                                    mode, numBytes, -1,
                                    val);
                            readMsg.sendToTarget();                                                 // Отправка сообщения в Handler
                        }
                        recDataString.delete(0, recDataString.length());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}