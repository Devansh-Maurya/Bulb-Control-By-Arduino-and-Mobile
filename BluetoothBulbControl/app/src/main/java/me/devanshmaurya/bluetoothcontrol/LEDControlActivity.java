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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class LEDControlActivity extends AppCompatActivity {

    private Button buttonOn, buttonDisconnect;
    private EditText textCommand, startDelayET, stopAfterET;
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

        textCommand = findViewById(R.id.command_edit_text);
        buttonOn = findViewById(R.id.button_on);
        buttonDisconnect = findViewById(R.id.button_disconnect);
        startDelayET = findViewById(R.id.start_delay);
        stopAfterET = findViewById(R.id.stop_after);

        new ConnectBluetooth().execute();

        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothSocket != null) {
                    try {
                        String command = textCommand.getText().toString();
                        byte[] commandBytes = command.getBytes();
                        String startDelay = startDelayET.getText().toString();
                        String stopAfter = stopAfterET.getText().toString();

                        if (!startDelay.equals("") && !stopAfter.equals("")) {
                            command = "b" + startDelay + "000" + "e" + stopAfter + "000" + command;
                            commandBytes = command.getBytes();
                        }

                        bluetoothSocket.getOutputStream().write(commandBytes);
                        showToast("Sent: " + command);
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
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
