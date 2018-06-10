package com.ujuzicode.mobilebluetothprinter;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MobileBluetothPrinter_Welcome extends AppCompatActivity {

    private int  BLUETOOTH_REQUEST= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_bluetoth_printer_welcome);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent , BLUETOOTH_REQUEST);
        }

        else
        {
            Intent i = new Intent(MobileBluetothPrinter_Welcome.this, MobileBluetothPrinter_Main.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BLUETOOTH_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(MobileBluetothPrinter_Welcome.this, MobileBluetothPrinter_Main.class);
                startActivity(i);
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

}
