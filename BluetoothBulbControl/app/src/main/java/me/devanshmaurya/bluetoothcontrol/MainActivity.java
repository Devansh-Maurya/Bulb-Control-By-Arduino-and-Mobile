package me.devanshmaurya.bluetoothcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public static final int REQUEST_ENABLE_BT = 4;
    ArrayList<String> pairedDevicesName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        if (bluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                //There are paired devices, get the name of each
                for (BluetoothDevice device : pairedDevices) {
                    pairedDevicesName.add(device.getName());
                }
            }
        }

        ArrayAdapter<String> pairedDevicesAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pairedDevicesName);
        ListView listView = findViewById(R.id.paired_devices_list);
        listView.setAdapter(pairedDevicesAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED)
            Toast.makeText(this, "Bluetooth was not enabled", Toast.LENGTH_SHORT).show();
    }
}
