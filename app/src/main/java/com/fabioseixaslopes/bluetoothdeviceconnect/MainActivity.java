package com.fabioseixaslopes.bluetoothdeviceconnect;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Handler;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private final ArrayList<String> deviceListView = new ArrayList<>();
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private Device selectedDevice;
    BluetoothLeScanner leScanner;
    boolean scanning;
    Handler handler = new Handler();
    long SCAN_PERIOD = 5000;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("App start.\nEntered MainActivity.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if(!bluetoothAdapter.isEnabled() || !manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
                System.out.println("BT or GPS not ON!");
                return;
            }
            scanLeDevice();
            listView.setOnItemClickListener((adapterView, view1, position, l) -> {
                selectedDevice = new Device(deviceList.get(position).getName(),deviceList.get(position).getAddress());
                System.out.println("Selected Device: " +
                        position + " " + selectedDevice.address + " " + selectedDevice.name);
                Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                intent.putExtra("deviceName", selectedDevice.name);
                intent.putExtra("deviceAddress", selectedDevice.address);
                startActivity(intent);
            });
        });
    }

    private void permissions(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH_PRIVILEGED},
                1);

        listView = findViewById(R.id.listView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        leScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothAdapter == null) {
            System.out.println("Device does not support Bluetooth");
            return;
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Toast.makeText(getApplicationContext(), "Turned BT On!", Toast.LENGTH_LONG).show();
            bluetoothAdapter.enable();
        }
        manager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE );
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
            Toast.makeText(getApplicationContext(), "GPS is disabled!", Toast.LENGTH_LONG).show();
    }

    private void scanLeDevice() {
        if (!scanning) {
            handler.postDelayed(() -> {
                scanning = false;
                leScanner.stopScan(leScanCallback);
            }, SCAN_PERIOD);
            scanning = true;
            startScan();
        } else {
            scanning = false;
            leScanner.stopScan(leScanCallback);
        }
    }

    private void startScan(){
        if(!bluetoothAdapter.isEnabled())
            Toast.makeText(getApplicationContext(), "Turned BT On!", Toast.LENGTH_LONG).show();
        else if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
            Toast.makeText(getApplicationContext(), "GPS is disabled!", Toast.LENGTH_LONG).show();
        else
            leScanner.startScan(leScanCallback);
    }

    private final ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (!deviceList.contains(result.getDevice())) {
                        deviceList.add(result.getDevice());
                        deviceListView.add(result.getDevice().getName() + "\n" + result.getDevice().getAddress());
                        System.out.println("Found device: " + result.getDevice().getName() + "|" + result.getDevice().getAddress());
                        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_expandable_list_item_1, deviceListView));
                    }
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}