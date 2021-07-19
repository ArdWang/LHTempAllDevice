package com.wc.lhtempalldevice.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wc.lhtempalldevice.R;
import com.wc.lhtempalldevice.model.TempBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnd on 2017/5/9.
 */

public class DeviceAdapter extends BaseAdapter{
    private List<TempBean> list = new ArrayList<>();

    private LayoutInflater mInflator;

    public DeviceAdapter(Context context, List<TempBean> mylist){
        this.list = mylist;
        mInflator =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.monitor_item, null);
            viewHolder = new ViewHolder();
            //viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceTemp = (TextView) view.findViewById(R.id.all_device_temp);
            viewHolder.deviceAddre = (TextView)view.findViewById(R.id.all_device_addre);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.all_device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        TempBean tp = list.get(position);
        viewHolder.deviceTemp.setText(tp.getDervicetemp());
        viewHolder.deviceAddre.setText(tp.getDerviceaddre());
        viewHolder.deviceName.setText(tp.getDervicename());
        return view;
    }

    private static class ViewHolder {
        TextView deviceTemp;
        TextView deviceAddre;
        TextView deviceName;

    }
}
