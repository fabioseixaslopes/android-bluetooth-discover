package com.fabioseixaslopes.bluetoothdeviceconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class ConnectActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private Device selectedDevice;
    private ListView listViewServices;
    private final ArrayList<BluetoothGattService> serviceList = new ArrayList<>();
    private final ArrayList<String> serviceListView = new ArrayList<>();
    List<BluetoothGattService> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        selectedDevice = new Device(getIntent().getStringExtra("deviceName"),
                getIntent().getStringExtra("deviceAddress"));
        listViewServices = findViewById(R.id.listViewServices);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listViewServices.setOnItemClickListener((adapterView, view1, position, l) -> {
            System.out.println("Selected Service: " + serviceList.get(position).getUuid().toString());
            Intent intent = new Intent(this, CharacteristicsActivity.class);
            intent.putExtra("serviceUUID", serviceList.get(position).getUuid().toString());
            startActivity(intent);
        });

        connectDevice();

        //TODO i only have UUIDs for now, how to have names? and values? types?
    }

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            services = gatt.getServices();
            runOnUiThread(() -> {
                for (BluetoothGattService service : services){
                    System.out.println("Found service: " + service.getUuid().toString());
                    if (!serviceList.contains(service))
                    {
                        serviceList.add(service);
                        serviceListView.add(service.getUuid().toString());
                        listViewServices.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_expandable_list_item_1, serviceListView));
                    }
                }
            });
        }
    };

    private void connectDevice(){
        Toast.makeText(getApplicationContext(), "Please wait a few seconds. Trying to connect...",
                Toast.LENGTH_LONG).show();
        BluetoothDevice deviceToBeConnected = bluetoothAdapter.getRemoteDevice(selectedDevice.address);
        deviceToBeConnected.connectGatt(getApplicationContext(),true, gattCallback);
    }
}
