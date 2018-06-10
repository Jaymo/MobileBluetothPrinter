package com.ujuzicode.mobilebluetothprinter;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MobileBluetothPrinter_Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_bluetoth_printer_welcome);

        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (null != adapter) {
                    if (!adapter.isEnabled()) {
                        if (adapter.enable()) {
                            Log.v("MBT","Enabled BluetoothAdapter");

                        } else {
                            finish();
                            return;
                        }
                    }
                }

                    Intent i = new Intent(MobileBluetothPrinter_Welcome.this,
                            MobileBluetothPrinter_Main.class);
                    startActivity(i);
                    finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
