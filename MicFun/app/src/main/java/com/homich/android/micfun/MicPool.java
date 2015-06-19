package com.homich.android.micfun;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * Created by samarin on 19-Jun-15.
 */
public class MicPool<Token> extends HandlerThread {
    private static final String TAG = "MicPool";
    private static final int MIC_POOL_MESSAGE = 0;

    Handler mHandler;

    public MicPool() {
        super(TAG);
    }

    public void queuePool(Token token)
    {
        Log.i(TAG, "Test pool");

        mHandler.obtainMessage(MIC_POOL_MESSAGE, token).sendToTarget();
    }

    @Override
    protected void onLooperPrepared() {
        //super.onLooperPrepared();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MIC_POOL_MESSAGE){
                    Token token = (Token)msg.obj;
                    //Log.i(TAG, "Req:" + )
                }
            };
        };
    }

}
