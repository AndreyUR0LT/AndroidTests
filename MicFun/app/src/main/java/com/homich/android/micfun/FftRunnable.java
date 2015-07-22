package com.homich.android.micfun;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Queue;

import ca.uol.aig.fftpack.RealDoubleFFT;

/**
 * Created by root on 22.07.15.
 */
public class FftRunnable implements Runnable{

    private Queue<double[]> mQueue;
    private int mBlockSize;
    private Handler mHandler;

    public FftRunnable(Queue<double[]> queue, int blockSize, Handler handler)
    {
        mQueue = queue;
        mBlockSize = blockSize;
        mHandler = handler;
    }

    @Override
    public void run() {
        RealDoubleFFT transformer = new RealDoubleFFT(mBlockSize);

        while (true)
        {
            synchronized(mQueue) {
                while (mQueue.isEmpty()){
                    try{
                        mQueue.wait();
                    }
                    catch (InterruptedException ignore) {}
                }
            }
            double[] dataToTransorm = mQueue.poll();

            transformer.ft(dataToTransorm);

            Message msg = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putDoubleArray(MicPoolRunnable.PCM_ARRAY_TAG, dataToTransorm);
            bundle.putInt(MicPoolRunnable.PCM_ARRAY_SIZE_TAG, mQueue.size());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }


    }
}
