package com.sir.app.bluetooth;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sir.library.base.BaseActivity;
import com.sir.library.base.help.OnItemClickListener;
import com.sir.library.base.help.ViewHolder;
import com.sir.library.fastble.BluetoothService;
import com.sir.library.fastble.data.ScanResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.recycler_bluetooth)
    RecyclerView mRecyclerBluetooth;
    @BindView(R.id.bluetooth_name)
    EditText bluetoothName;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothService  mBluetoothService;


    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
       setSwipeBackEnable(false);
    }

    @Override
    public void doBusiness(Context mContext) {
        mBluetoothAdapter = new BluetoothAdapter(this);
        mRecyclerBluetooth.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerBluetooth.setAdapter(mBluetoothAdapter);

        mBluetoothAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewHolder holder, int position) {
                mBluetoothService.connectDevice(mBluetoothAdapter.getItem(position));
            }
        });
    }


    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setScanCallback(callback);
            mBluetoothService.scanDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };


    private BluetoothService.Callback callback = new BluetoothService.Callback() {

        @Override
        public void onStartScan() {
            mProgressBar.setVisibility(View.VISIBLE);
            mBluetoothAdapter.clearAllItem();
            findViewById(R.id.btn_start).setEnabled(false);
            findViewById(R.id.btn_stop).setEnabled(true);
        }

        @Override
        public void onScanning(ScanResult scanResult) {
            mBluetoothAdapter.addItem(scanResult);
        }

        @Override
        public void onScanComplete() {
            findViewById(R.id.btn_start).setEnabled(true);
            findViewById(R.id.btn_stop).setEnabled(false);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onConnecting() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectFail() {
            mProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDisConnected() {
            Toast.makeText(MainActivity.this, "连接断开", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServicesDiscovered() {
            getOperation().forward(OperationActivity.class);
        }
    };

    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        this.unbindService(mFhrSCon);
    }


    @OnClick({R.id.btn_start, R.id.btn_stop})
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                checkPermissions();
                break;
            case R.id.btn_stop:
                if (mBluetoothService != null) {
                    mBluetoothService.cancelScan();
                }
                break;
            case R.id.btn_start1:
                String blue = bluetoothName.getText().toString();
                if (mBluetoothService != null && !TextUtils.isEmpty(blue)) {
                    mBluetoothService.scanAndConnect2(blue); //扫描并连接
                }
                break;
            case R.id.btn_stop1:

                break;
        }
    }


    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    //检查权限
    private void checkPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }
    }

    //在许可授予
    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (mBluetoothService == null) {
                    bindService();
                } else {
                    mBluetoothService.scanDevice();
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            unbindService();
        }
    }
}
