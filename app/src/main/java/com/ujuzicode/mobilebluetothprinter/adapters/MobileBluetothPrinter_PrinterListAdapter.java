package com.ujuzicode.mobilebluetothprinter.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ujuzicode.mobilebluetothprinter.MobileBluetothPrinter_PrinterList;
import com.ujuzicode.mobilebluetothprinter.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MobileBluetothPrinter_PrinterListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    private ArrayList<HashMap<String, String>> originalData;

    public MobileBluetothPrinter_PrinterListAdapter (Activity a, ArrayList<HashMap<String, String>> d) {

        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        originalData = d;

    }
    public int getCount()
    {
        return originalData !=null ? originalData.size() : 0;
    }

    public Object getItem(int position)
    {
        return originalData.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }


    public static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView address;

    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;
        View vi = convertView;
        if(convertView==null){
            vi = inflater.inflate(R.layout.activity_mobile_bluetoth_printer_list_row, parent, false);

            holder = new ViewHolder();

            holder.icon = vi.findViewById(R.id.icon);

            holder.name = vi.findViewById(R.id.name);

            holder.address= vi.findViewById(R.id.address);

            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }

        HashMap<String, String> devices = originalData.get(position);

        holder.name.setText(Html.fromHtml("<font color=\"#000000\"><b>Device: </b>" + devices.get(MobileBluetothPrinter_PrinterList.PRINTERNAME) + "</font>"));
        holder.address.setText(Html.fromHtml("<font color=\"#000000\"><b>Address: </b>" + devices.get(MobileBluetothPrinter_PrinterList.PRINTERMAC) + "</font>"));

        return vi;

    }
}
