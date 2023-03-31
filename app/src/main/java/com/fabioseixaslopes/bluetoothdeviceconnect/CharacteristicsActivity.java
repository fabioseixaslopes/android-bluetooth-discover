package com.fabioseixaslopes.bluetoothdeviceconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class CharacteristicsActivity extends AppCompatActivity {

    private List<BluetoothGattCharacteristic> characteristics;
    private List<BluetoothGattService> services;
    private final ArrayList<String> characteristicsListView = new ArrayList<>();
    ListView listView;
    String serviceUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristics);
        setTitle("Characteristics");

        listView = findViewById(R.id.listViewCharacteristics);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Device selectedDevice = new Device(getIntent().getStringExtra("deviceName"),
                getIntent().getStringExtra("deviceAddress"));

        serviceUUID = getIntent().getStringExtra("serviceUUID");
        System.out.println(serviceUUID);

        BluetoothDevice deviceToBeConnected = bluetoothAdapter.getRemoteDevice(selectedDevice.address);
        deviceToBeConnected.connectGatt(getApplicationContext(),true, gattCallback);
    }

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            gatt.discoverServices();
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            services = gatt.getServices();
            runOnUiThread(() -> {
                for (BluetoothGattService service : services)
                {
                    System.out.println(service.getUuid().toString());
                    if (service.getUuid().toString().equals(serviceUUID))
                    {
                        characteristics = service.getCharacteristics();
                        for (BluetoothGattCharacteristic characteristic : characteristics){
                            System.out.println(characteristic.getUuid().toString());
                            System.out.println("Found characteristic: " + characteristic.getUuid().toString());
                            characteristicsListView.add(characteristic.getUuid().toString());
                            listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                                    android.R.layout.simple_expandable_list_item_1, characteristicsListView));
                        }
                    }
                }
            });
        }
    };
}
