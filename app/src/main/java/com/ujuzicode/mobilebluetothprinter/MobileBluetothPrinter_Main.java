package com.ujuzicode.mobilebluetothprinter;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ujuzicode.mobilebluetothprinter.printer.Global;
import com.ujuzicode.mobilebluetothprinter.printer.WorkService;

import java.lang.ref.WeakReference;

public class MobileBluetothPrinter_Main  extends AppCompatActivity {

    private int  PICK_BT_LIST_REQUEST= 2;

    private int  BLUETOOTH_REQUEST= 3;

    private static Handler mHandler = null;

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

        InitGlobalString();

        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);
        }
    }

    static class MHandler extends Handler {

        WeakReference<MobileBluetothPrinter_Main> mActivity;

        MHandler(MobileBluetothPrinter_Main activity) {
            mActivity = new WeakReference<MobileBluetothPrinter_Main>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MobileBluetothPrinter_Main activity = mActivity.get();
            switch (msg.what) {

            }
        }
    }

    private void InitGlobalString() {
        Global.toast_success = getString(R.string.toast_success);
        Global.toast_fail = getString(R.string.toast_fail);
        Global.toast_notconnect = getString(R.string.toast_notconnect);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (WorkService.workThread.isConnecting()) {
            Toast.makeText(this, getString(R.string.bluetooth_connecting),
            Toast.LENGTH_SHORT).show();
            return true;
        }
        switch (item.getItemId()) {

            case R.id.action_bluetooth_activate:

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter .getDefaultAdapter();

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent , BLUETOOTH_REQUEST);

                }
                else{
                    Intent intent = new Intent(MobileBluetothPrinter_Main.this, MobileBluetothPrinter_PrinterList.class);
                    startActivityForResult(intent, PICK_BT_LIST_REQUEST);
                }


                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_BT_LIST_REQUEST) {

            String sAddress = data.getStringExtra("sAddress");

            if (resultCode == RESULT_OK) { Log.i("Device Address",sAddress);}

            if (resultCode == RESULT_CANCELED) {Log.i("Device Address","no address obtained");}

        }

        if (requestCode == BLUETOOTH_REQUEST) {

            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(MobileBluetothPrinter_Main.this, MobileBluetothPrinter_PrinterList.class);
                startActivityForResult(intent, PICK_BT_LIST_REQUEST);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
    }
}
