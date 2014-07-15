package com.nashglover.bluevncclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.Set;


public class MainActivity extends Activity {

    Thread connectionThread;
    ImageView outputImg;

    int imgWidth;
    int imgHeight;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            System.out.println("Handling a message...");
            Bundle bundle = msg.getData();
            String type = bundle.getString("type");
            if (type == "connected") {
                System.out.println("It's connected now!");
            }
        }
    };

    Connection connection = new Connection(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outputImg = (ImageView) findViewById(R.id.output_image);
        outputImg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imgWidth = outputImg.getWidth();
                imgHeight = outputImg.getHeight();
                System.out.println("Height and width: " + imgWidth + " " + imgHeight);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startButton(View view) {
        imgWidth = outputImg.getWidth();
        imgHeight = outputImg.getHeight();
        System.out.println("Height and width: " + imgWidth + " " + imgHeight);
        Runnable runnable = new Runnable() {
            public void run() {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
                final int size = pairedDevices.size();
                final BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[size]);
                String[] deviceNames = new String[size];
                int i;
                for (i=0; i < size; i++) {
                    deviceNames[i] = devices[i].getName();
                }
                final String[] finalNames = deviceNames;
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(MainActivity.this);
                        menuAlert.setTitle("Bluetooth Devices");
                        menuAlert.setItems(finalNames, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                System.out.println("Clicked!");
                                connection.setDevice(devices[item]);
                                connectionThread = new Thread(connection);
                                connectionThread.start();
                            }
                        });
                        final AlertDialog menuDrop = menuAlert.create();
                        menuDrop.show();
                    }
                });
            }
        };
        Thread newThread = new Thread(runnable);
        newThread.start();
    }
}
