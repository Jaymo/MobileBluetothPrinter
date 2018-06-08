package com.ujuzicode.mobilebluetothprinter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MobileBluetothPrinter_Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_bluetoth_printer_welcome);

        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent i = new Intent(MobileBluetothPrinter_Welcome.this,
                            MobileBluetothPrinter_Main.class);
                    startActivity(i);
                    finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
