package com.ujuzicode.mobilebluetothprinter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.widget.TextView;

public class MobileBluetothPrinter_Main  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_bluetoth_printer_main);
        Toolbar mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = mToolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + getResources().getString(R.string.app_name) + "</b></font>"));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
