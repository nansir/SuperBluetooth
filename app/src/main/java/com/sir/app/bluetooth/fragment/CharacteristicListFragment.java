package com.sir.app.bluetooth.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.sir.app.bluetooth.OperationActivity;
import com.sir.app.bluetooth.R;
import com.sir.library.base.BaseAdapter;
import com.sir.library.base.BaseFragmentV4;
import com.sir.library.base.help.ViewHolder;
import com.sir.library.fastble.BluetoothService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyinan on 2017/6/29.
 * Contact by 445181052@qq.com
 */
public class CharacteristicListFragment extends BaseFragmentV4 {

    ResultAdapter mResultAdapter;

    BluetoothService mBluetoothService;

    @Override
    public int bindLayout() {
        return R.layout.fragment_service_list;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mBluetoothService = ((OperationActivity) getActivity()).getBluetoothService();
        mResultAdapter = new ResultAdapter(getActivity());
    }

    @Override
    public void doBusiness(Context mContext) {
        ListView listView_device = (ListView) findViewById(R.id.list_service);
        listView_device.setAdapter(mResultAdapter);
        listView_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothGattCharacteristic characteristic = mResultAdapter.getItem(position);
                final List<Integer> propList = new ArrayList<>();
                List<String> propNameList = new ArrayList<>();
                int charaProp = characteristic.getProperties();
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    propList.add(CharacteristicOperationFragment.PROPERTY_READ);
                    propNameList.add("Read");
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                    propList.add(CharacteristicOperationFragment.PROPERTY_WRITE);
                    propNameList.add("Write");
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                    propList.add(CharacteristicOperationFragment.PROPERTY_WRITE_NO_RESPONSE);
                    propNameList.add("Write No Response");
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    propList.add(CharacteristicOperationFragment.PROPERTY_NOTIFY);
                    propNameList.add("Notify");
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                    propList.add(CharacteristicOperationFragment.PROPERTY_INDICATE);
                    propNameList.add("Indicate");
                }

                if (propList.size() > 1) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("选择操作类型")
                            .setItems(propNameList.toArray(new String[propNameList.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mBluetoothService.setCharacteristic(characteristic);
                                    mBluetoothService.setCharaProp(propList.get(which));
                                    ((OperationActivity) getActivity()).showFragment(2);
                                }
                            })
                            .show();
                } else if (propList.size() > 0) {
                    mBluetoothService.setCharaProp(propList.get(0));
                    mBluetoothService.setCharacteristic(characteristic);
                    ((OperationActivity) getActivity()).showFragment(2);
                }
            }
        });
    }

    public void showData() {
        mBluetoothService = ((OperationActivity) getActivity()).getBluetoothService();
        BluetoothGattService service = mBluetoothService.getService();
        mResultAdapter.clearAllItem();
        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
            mResultAdapter.addItem(characteristic);
        }
        mResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void lazyFetchData() {

    }

    private class ResultAdapter extends BaseAdapter<BluetoothGattCharacteristic> {

        public ResultAdapter(Activity mContext) {
            super(mContext);
        }

        @Override
        public int bindLayout() {
            return R.layout.adapter_service;
        }

        @Override
        public void onBindHolder(ViewHolder holder, int position) {
            BluetoothGattCharacteristic characteristic = getItem(position);
            String uuid = characteristic.getUuid().toString();

            holder.setText(R.id.txt_title, String.valueOf("特征" + "（" + position + ")"));
            holder.setText(R.id.txt_uuid, uuid);
            StringBuilder property = new StringBuilder();
            int charaProp = characteristic.getProperties();
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                property.append("Read");
                property.append(" , ");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                property.append("Write");
                property.append(" , ");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                property.append("Write No Response");
                property.append(" , ");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                property.append("Notify");
                property.append(" , ");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                property.append("Indicate");
                property.append(" , ");
            }
            if (property.length() > 1) {
                property.delete(property.length() - 2, property.length() - 1);
            }
            if (property.length() > 0) {
                holder.setText(R.id.txt_type, String.valueOf("特性" + "( " + property.toString() + ")"));
                holder.getView(R.id.img_next).setVisibility(View.VISIBLE);
            } else {
                holder.getView(R.id.img_next).setVisibility(View.INVISIBLE);
            }
        }
    }
}
