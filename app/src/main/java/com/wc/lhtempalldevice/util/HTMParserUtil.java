package com.wc.lhtempalldevice.util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;

import com.wc.lhtempalldevice.R;
import com.wc.lhtempalldevice.model.TempBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnd on 2017/5/9.
 */

public class HTMParserUtil {
    public static List<TempBean> aa=new ArrayList<>();

    public static List<TempBean> getHealthThermo(List<String> mDeviceName,List<String> mDeviceList, List<BluetoothGattCharacteristic> listCharacteristic, Context context){
        String tempUnit = "";
        int temps = 0;
        if(aa.size()>0) {
            aa.clear();
        }

        for(int i=0;i<listCharacteristic.size();i++) {
            final byte[] data = listCharacteristic.get(i).getValue();
            if (data != null && data.length > 0) {
                int b0, b1, b2, b3;
                b0 = data[1] & 0xff;
                b1 = data[2] & 0xff;
                b2 = data[3] & 0xff;
                b3 = data[4] & 0xff;
                temps = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
                byte flagByte = data[0];
                if ((flagByte & 0x01) != 0) {
                    tempUnit = context.getString(R.string.tt_fahren_heit);
                } else {
                    tempUnit = context.getString(R.string.tt_celcius);
                }
            }
            float a = temps;
            String s = a / 10 + "";
            String ss = s + "," + tempUnit;
            String addre = mDeviceList.get(i);
            String name = mDeviceName.get(i);
            TempBean tp = new TempBean();
            tp.setDervicename(name);
            tp.setDerviceaddre(addre);
            tp.setDervicetemp(ss);
            //tp.setTemps(ss);
            //tp.setAddre(addre);
            if(listCharacteristic.size()>0){
                aa.add(tp);
            }
        }
        return aa;
    }

    private static void intToByte(int i){
        byte []a = new byte[4];
        a[0] = (byte) (i>>24&0xff);
        a[1] = (byte) (i>>16&0xff);
        a[2] = (byte) (i>>8&0xff);
        a[3] = (byte) (i&0xff);

        byteToInt(a);

        Log.i("ctmeps",a+"");
    }

    private static void byteToInt(byte[] a){
        int b0,b1,b2,b3;
        b0 = a[0]&0xff;
        b1 = a[1]&0xff;
        b2 = a[2]&0xff;
        b3 = a[3]&0xff;

        int temps = (b0<<24)|(b1<<16)|(b2<<8)|b3;

        Log.i("TAGs",temps+"");
    }
}
