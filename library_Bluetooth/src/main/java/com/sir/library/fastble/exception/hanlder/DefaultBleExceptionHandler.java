package com.sir.library.fastble.exception.hanlder;

import android.content.Context;
import android.util.Log;

import com.sir.library.fastble.exception.ConnectException;
import com.sir.library.fastble.exception.GattException;
import com.sir.library.fastble.exception.InitiatedException;
import com.sir.library.fastble.exception.OtherException;
import com.sir.library.fastble.exception.TimeoutException;

public class DefaultBleExceptionHandler extends BleExceptionHandler {

    private static final String TAG = "BleExceptionHandler";
    private Context context;

    public DefaultBleExceptionHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onConnectException(ConnectException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onGattException(GattException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onTimeoutException(TimeoutException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onInitiatedException(InitiatedException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onOtherException(OtherException e) {
        Log.e(TAG, e.getDescription());
    }
}
