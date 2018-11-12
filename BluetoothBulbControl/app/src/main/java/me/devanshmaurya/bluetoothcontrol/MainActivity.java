package me.devanshmaurya.bluetoothcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button buttonPaired;
    private ListView pairedDevicesList;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    public static final int BT_ENABLE_REQUEST = 4;
    public static final String EXTRA_ADDRESS = "address";

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

        buttonPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPairedDeviceList();
            }
        });
    }

    private void showPairedDeviceList() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                //Get the device's name and address
                list.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No paired Bluetooth devices found",
                    Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter<String> adapter = new
                ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        pairedDevicesList.setAdapter(adapter);
        pairedDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Get the device MAC address, the last 17 characters in the view
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                //Make an intent to start next activity
                Intent intent = new Intent(MainActivity.this, LEDControlActivity.class);
                intent.putExtra(EXTRA_ADDRESS, address);
                startActivity(intent);
            }
        });
    }

}
