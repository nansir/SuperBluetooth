package com.sir.app.bluetooth;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.sir.app.bluetooth.fragment.CharacteristicListFragment;
import com.sir.app.bluetooth.fragment.CharacteristicOperationFragment;
import com.sir.app.bluetooth.fragment.ServiceListFragment;
import com.sir.library.base.BaseActivity;
import com.sir.library.fastble.BluetoothService;

import butterknife.BindView;


/**
 * 蓝牙
 * Created by zhuyinan on 2017/6/26.
 * Contact by 445181052@qq.com
 */
public class OperationActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    BluetoothService mBluetoothService;
    String[] titles = new String[]{"服务列表", "特征列表", "操作控制台"};
    private BluetoothService.Callback2 callback = new BluetoothService.Callback2() {

        @Override
        public void onDisConnected() {
            finish();
        }
    };
    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TAG", "onServiceConnected");
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setConnectCallback(callback);
            Log.e("TAG", "onServiceConnected" + mBluetoothService == null ? "mBluetoothService is null" : "mBluetoothService 正常");
            prepareFragment();
            showFragment(R.id.fragment, 0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("TAG", "onServiceDisconnected");
            mBluetoothService = null;
        }
    };

    @Override
    public int bindLayout() {
        return R.layout.activity_operation;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        toolbar.setTitle(titles[1]);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upPosition != 0) {
                    upPosition--;
                    showFragment(upPosition);
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        bindService();
    }

    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    public void showFragment(int index) {
        toolbar.setTitle(titles[index]);
        showFragment(R.id.fragment, index);
        if (index == 1) {
            ((CharacteristicListFragment) mFragments.get(1)).showData();
        } else if (index == 2) {
            ((CharacteristicOperationFragment) mFragments.get(2)).showData();
        }
    }

    private void prepareFragment() {
        mFragments.add(new ServiceListFragment());
        mFragments.add(new CharacteristicListFragment());
        mFragments.add(new CharacteristicOperationFragment());
        for (Fragment fragment : mFragments) {
            if (!fragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).hide(fragment).commit();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.closeConnect();
        }
        unbindService();
    }

    private void unbindService() {
        this.unbindService(mFhrSCon);
    }

    public BluetoothService getBluetoothService() {
        return mBluetoothService;
    }
}
