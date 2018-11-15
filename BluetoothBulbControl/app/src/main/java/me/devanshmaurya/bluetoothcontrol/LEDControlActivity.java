package me.devanshmaurya.bluetoothcontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class LEDControlActivity extends AppCompatActivity {

    private Button buttonDisconnect;
    private CheckBox bulb1Checkbox, bulb2Checkbox;
    private ImageView buttonOn;
    private EditText textCommand, startDelayET, stopAfterET;
    private String address;
    private ProgressDialog progress;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private boolean isBTConnected = false;
    //Default Universally Unique Identifier, HC-05's default
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean on = false;
    private Toast previousToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledcontrol);
        Intent receivedIntent = getIntent();
        address = receivedIntent.getStringExtra(MainActivity.EXTRA_ADDRESS);

        buttonOn = findViewById(R.id.button_on);
        buttonDisconnect = findViewById(R.id.button_disconnect);
        bulb1Checkbox = findViewById(R.id.bulb1_checkbox);
        bulb2Checkbox = findViewById(R.id.bulb2_checkbox);
        startDelayET = findViewById(R.id.start_delay);
        stopAfterET = findViewById(R.id.stop_after);

        new ConnectBluetooth().execute();

        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothSocket != null) {
                    try {
                        String command;
                        //Set on only if one of the bulb has been checked
                        if (!on ) {
                            command = "ON";
                            //Keeping it here is required, don't keep it out for proper functioning of app
                            if (bulb1Checkbox.isChecked() || bulb2Checkbox.isChecked())
                                buttonOn.setImageResource(R.drawable.bulb_on);
                            on = true;
                        } else {
                            command = "OFF";
                            buttonOn.setImageResource(R.drawable.bulb_off);
                            on = false;
                        }
                        byte[] commandBytes = command.getBytes();
                        final String startDelay = startDelayET.getText().toString();
                        String stopAfter = stopAfterET.getText().toString();

                        if (!startDelay.equals("") && !stopAfter.equals("") && on) {
                            command = "b" + startDelay + "000" + "e" + stopAfter + "000";
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
                        //To prevent functioning of bulb on next start of app
                        bluetoothSocket.getOutputStream().write("STOP".getBytes());
                        bluetoothSocket.close(); //Close connection
                    } catch (IOException e) {
                        showToast("Error!!!");
                    }
                }
                finish();   //Return to the previous activity
            }
        });
    }

    public void onBulbCheckBoxClicked(View view) throws IOException{
        boolean checked = ((CheckBox) view).isChecked();

        OutputStream bluetoothOS = bluetoothSocket.getOutputStream();

        switch (view.getId()) {
            case R.id.bulb1_checkbox:
                if (checked) {
                    //If box is checked, make pin 1 of Arduino as output for bulb 1
                    bluetoothOS.write("1".getBytes());
                    showToast("Bulb 1 is now functional");
                } else {
                    //If box is unchecked, make pin 0 as output, as nothing is connected there,
                    //bulb won't glow
                    bluetoothOS.write("01".getBytes());
                    showToast("Bulb 1 is not functional");
                }
                break;
            case R.id.bulb2_checkbox:
                if (checked) {
                    //If box is checked, make pin 2 of Arduino as output for bulb 2
                    bluetoothOS.write("2".getBytes());
                    showToast("Bulb 2 is now functional");
                } else {
                    //If box is unchecked, make pin 0 as output, as nothing is connected there,
                    //bulb won't glow
                    bluetoothOS.write("02".getBytes());
                    showToast("Bulb 1 is not functional");
                }
        }
    }


    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
        //Hide the previous toast
        if (previousToast != null)
            previousToast.cancel();
        previousToast = toast;
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
