package com.ujuzicode.mobilebluetothprinter.utils;

import android.content.Context;

import com.ujuzicode.mobilebluetothprinter.R;
import com.ujuzicode.mobilebluetothprinter.printer.Global;

public class Util {

    public static void InitGlobalString(Context context) {
        Global.toast_success = context.getString(R.string.toast_success);
        Global.toast_fail = context.getString(R.string.toast_fail);
        Global.toast_notconnect = context.getString(R.string.toast_notconnect);
    }
}
