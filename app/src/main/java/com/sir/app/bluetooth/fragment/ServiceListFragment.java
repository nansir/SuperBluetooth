package com.sir.app.bluetooth.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.sir.app.bluetooth.OperationActivity;
import com.sir.app.bluetooth.R;
import com.sir.library.base.BaseAdapter;
import com.sir.library.base.BaseFragmentV4;
import com.sir.library.base.help.ViewHolder;
import com.sir.library.fastble.BluetoothService;

import butterknife.BindView;

/**
 * Created by zhuyinan on 2017/6/29.
 * Contact by 445181052@qq.com
 */
public class ServiceListFragment extends BaseFragmentV4 {


    @BindView(R.id.txt_name)
    TextView txt_name;
    @BindView(R.id.txt_mac)
    TextView txt_mac;
    @BindView(R.id.list_service)
    ListView listView;
    ResultAdapter mResultAdapter;

    private BluetoothService mBluetoothService;


    @Override
    public int bindLayout() {
        return R.layout.fragment_service_list;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mBluetoothService = ((OperationActivity) getActivity()).getBluetoothService();
        mResultAdapter = new ResultAdapter(getActivity());
        listView.setAdapter(mResultAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BluetoothGattService service = mResultAdapter.getItem(position);
                mBluetoothService.setService(service);
                ((OperationActivity) getActivity()).showFragment(1);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        showData();
    }

    private void showData() {
        String name = mBluetoothService.getName();
        String mac = mBluetoothService.getMac();
        BluetoothGatt gatt = mBluetoothService.getGatt();
        txt_name.setText(String.valueOf("设备广播名：" + name));
        txt_mac.setText(String.valueOf("MAC地址: " + mac));
        mResultAdapter.clearAllItem();
        for (final BluetoothGattService service : gatt.getServices()) {
            mResultAdapter.addItem(service);
        }
        mResultAdapter.notifyDataSetChanged();
    }


    @Override
    public void lazyFetchData() {

    }


    class ResultAdapter extends BaseAdapter<BluetoothGattService> {

        public ResultAdapter(Activity mContext) {
            super(mContext);
        }

        @Override
        public int bindLayout() {
            return R.layout.adapter_service;
        }

        @Override
        public void onBindHolder(ViewHolder holder, int position) {
            BluetoothGattService service = getItem(position);
            String uuid = service.getUuid().toString();
            holder.setText(R.id.txt_title, String.valueOf("服务" + "（" + position + ")"));
            holder.setText(R.id.txt_uuid, uuid);
            holder.setText(R.id.txt_type, "类型（主服务）");
        }
    }
}
