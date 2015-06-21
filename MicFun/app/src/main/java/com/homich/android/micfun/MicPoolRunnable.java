package com.homich.android.micfun;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by root on 21.06.15.
 */
public class MicPoolRunnable implements Runnable {

    public static final String ARRAY_TAG = "ARRAY";

    private Handler mHandler;

    public MicPoolRunnable(Handler handler){
        mHandler = handler;
    }

    @Override
    public void run() {
        int[] arr = new int[10];

        for (int i = 0; i < arr.length; i++)
            arr[i] = i;

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putIntArray(ARRAY_TAG, arr);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
}
