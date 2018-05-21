package com.sir.app.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import com.sir.library.base.BaseRecyclerAdapter;
import com.sir.library.base.help.ViewHolder;
import com.sir.library.fastble.data.ScanResult;


/**
 * 蓝牙适配器
 * Created by zhuyinan on 2017/6/26.
 * Contact by 445181052@qq.com
 */
public class BluetoothAdapter extends BaseRecyclerAdapter<ScanResult> {

    public BluetoothAdapter(Activity context) {
        super(context);
    }

    @Override
    public int bindLayout() {
        return R.layout.adapter_bluetooth;
    }

    @Override
    public void onBindHolder(ViewHolder holder, int position) {
        ScanResult result = getItem(position);
        BluetoothDevice device = result.getDevice();
        holder.setText(R.id.blu_name, device.getName());
        holder.setText(R.id.blu_mac, device.getAddress());
        holder.setText(R.id.blu_rssi, String.valueOf(result.getRssi()));
    }
}
