package me.devanshmaurya.bluetoothcontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class LEDControlActivity extends AppCompatActivity {

    private Button buttonOn, buttonOff, buttonDisconnect;
    private SeekBar brightnessBar;
    private String address;
    private ProgressDialog progress;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private boolean isBTConnected = false;
    //Default Universally Unique Identifier, HC-05's default
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledcontrol);
        Intent receivedIntent = getIntent();
        address = receivedIntent.getStringExtra(MainActivity.EXTRA_ADDRESS);

        buttonOn = findViewById(R.id.button_on);
        buttonOff = findViewById(R.id.button_off);
        buttonDisconnect = findViewById(R.id.button_disconnect);
        brightnessBar = findViewById(R.id.brightness_bar);

        ConnectBluetooth connectBluetooth = new ConnectBluetooth();

        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothSocket != null) {
                    try {
                        bluetoothSocket.getOutputStream().write((byte) 1);
                    } catch (IOException e) {
                        showToast("Error!!!");
                    }
                }
            }
        });

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothSocket != null) {
                    try {
                        bluetoothSocket.getOutputStream().write("0".toString().getBytes());
                    } catch (IOException e) {
                        showToast("Error!!!");
                    }
                }
            }
        });

        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If the bluetooth socket is busy
                if (bluetoothSocket != null) {
                    try {
                        bluetoothSocket.close(); //Close connection
                    } catch (IOException e) {
                        showToast("Error!!!");
                    }
                }
                finish();   //Return to the previous activity
            }
        });

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        if (bluetoothSocket != null)
                            bluetoothSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                    } catch (IOException e) {
                        showToast("Error!!!");
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private class ConnectBluetooth extends AsyncTask<Void, Void, Void> {

        private boolean connectSucces = true;


        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(LEDControlActivity.this, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (bluetoothSocket == null || !isBTConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(mUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                }
            } catch (IOException e) {
                connectSucces = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!connectSucces) {
                showToast("Connection failed");
                finish();
            } else {
                showToast("Connected");
                isBTConnected = true;
            }
            progress.dismiss();
        }
    }
}
