package com.ujuzicode.mobilebluetothprinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ujuzicode.mobilebluetothprinter.adapters.MobileBluetothPrinter_PrinterListAdapter;
import com.ujuzicode.mobilebluetothprinter.printer.Global;
import com.ujuzicode.mobilebluetothprinter.printer.WorkService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MobileBluetothPrinter_PrinterList extends AppCompatActivity {

    public static final String PRINTERNAME = "PRINTERNAME";

    public static final String PRINTERMAC = "PRINTERMAC";

    private static String address,name;

    private ProgressDialog dialog;

    private static Handler mHandler = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_bluetoth_printer_list);

        final ArrayList<HashMap<String, String>> boundedPrinters = getBoundedPrinters();

        dialog = new ProgressDialog(this);

        mHandler = new MHandler(this);

        WorkService.addHandler(mHandler);

        ListView listView = findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDividerHeight(0);

        MobileBluetothPrinter_PrinterListAdapter adapter = new MobileBluetothPrinter_PrinterListAdapter(MobileBluetothPrinter_PrinterList.this, boundedPrinters);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter.isEnabled()) {
                    address = boundedPrinters.get(position).get(PRINTERMAC);
                    name = boundedPrinters.get(position).get(PRINTERNAME);
                    dialog.setMessage(Global.toast_connecting + " " + name);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.show();
                    WorkService.workThread.connectBt(address);
                }
            }
        });


    }

    private ArrayList<HashMap<String, String>> getBoundedPrinters() {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter .getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter .getBondedDevices();


        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {

                int deviceBTMajorClass = device.getBluetoothClass().getMajorDeviceClass();
                HashMap<String, String> map = new HashMap<>();
                map.put(PRINTERNAME, device.getName());
                map.put(PRINTERMAC, device.getAddress());
                list.add(map);

            }
        }
        return list;
    }

    static class MHandler extends Handler {

        WeakReference<MobileBluetothPrinter_PrinterList> mActivity;

        MHandler(MobileBluetothPrinter_PrinterList activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MobileBluetothPrinter_PrinterList activity = mActivity.get();
            switch (msg.what) {

                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    if (result == 1) {
                        activity.dialog.cancel();
                        Toast.makeText(activity,Global.toast_success, Toast.LENGTH_SHORT).show();
                        activity.setResult(Activity.RESULT_OK, new Intent().putExtra("sName", name));
                        activity.finish();
                    }
                    else{
                        activity.dialog.cancel();
                        Toast.makeText(activity,Global.toast_fail, Toast.LENGTH_SHORT).show();
                        activity.setResult(Activity.RESULT_CANCELED, new Intent().putExtra("sName", ""));
                        activity.finish();
                    }

                    break;
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        setResult(Activity.RESULT_CANCELED, new Intent().putExtra("sName", ""));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
    }

}
