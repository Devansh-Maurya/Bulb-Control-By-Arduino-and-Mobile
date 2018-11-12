package me.devanshmaurya.bluetoothcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button buttonPaired;
    private ListView pairedDevicesList;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    public static final int BT_ENABLE_REQUEST = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPaired = findViewById(R.id.button_paired_devices);
        pairedDevicesList = findViewById(R.id.paired_devices_list);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth device not available", Toast.LENGTH_LONG).show();
            //Finish the app
            finish();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                //Ask user to turn bluetooth on
               Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
               startActivityForResult(enableBTIntent, BT_ENABLE_REQUEST);
            }
        }
    }

}
