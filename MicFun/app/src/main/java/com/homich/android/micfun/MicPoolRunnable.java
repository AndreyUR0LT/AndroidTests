package com.homich.android.micfun;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.CircularArray;
import android.util.Log;

import java.nio.Buffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import ca.uol.aig.fftpack.RealDoubleFFT;

/**
 * Created by root on 21.06.15.
 */
public class MicPoolRunnable implements Runnable {

    public static final String PCM_ARRAY_TAG = "PCM_ARRAY";
    public static final String PCM_ARRAY_SIZE_TAG = "PCM_ARRAY_SIZE";
    public static int Counter = 0;

    private Handler mHandler;
    private AudioRecord audioRecord;
    private boolean isRecording;
    private DoublesCircularBuffer mDoublesCircularBuffer;
    private Thread mFftTransformTask;

    public MicPoolRunnable(Handler handler){
        mHandler = handler;
    }

    public void StopRecording(){
        isRecording = false;
    }

    @Override
    public void run() {

        GetDataFromMic();
    }

    private void GetDataFromMic(){

        //int freq = 44100;
        int freq = 8000;
        int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = 0;
        short[] bufferData = null;
        isRecording = true;
        int blockSize = 256;
        final Queue<double[]> queue = new LinkedList<double[]>();

        try {
            bufferSize = AudioRecord.getMinBufferSize(freq, channelConfiguration, audioEncoding);
            mDoublesCircularBuffer = new DoublesCircularBuffer(bufferSize);
            bufferData = new short[bufferSize];
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    freq, channelConfiguration, audioEncoding, bufferSize);

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED){
                Log.d(PCM_ARRAY_TAG, "AudioRecord isn't initialize");
                return;
            }

            audioRecord.startRecording();

        }
        catch (Throwable t){
            Log.d(PCM_ARRAY_TAG, "An error occurred during initialize AudioRecord", t);
            if (audioRecord != null)
                audioRecord.release();
            return;
        }

        FftRunnable fftRunnable = new FftRunnable(queue, blockSize, mHandler);

        mFftTransformTask = new Thread(fftRunnable);
        //mFftTransformTask.start();

        RealDoubleFFT transformer = new RealDoubleFFT(blockSize);

        while (isRecording)
        {
            try {
                //int bufferReadResult = audioRecord.read(bufferData, 0, bufferSize);
                int bufferReadResult = audioRecord.read(bufferData, 0, blockSize);

                double[] micBufferData = new double[blockSize];
/*
                final int bytesPerSample = 2; // As it is 16bit PCM
                final double amplification = 100.0; // choose a number as you like
                for (int index = 0, floatIndex = 0; index < bufferReadResult - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
                    double sample = 0;
                    for (int b = 0; b < bytesPerSample; b++) {
                        int v = bufferData[index + b];
                        if (b < bytesPerSample - 1 || bytesPerSample == 1) {
                            v &= 0xFF;
                        }
                        sample += v << (b * 8);
                    }
                    double sample32 = amplification * (sample / 32768.0);
                    micBufferData[floatIndex] = sample32;
                }
*/
/*
                for (int i = 0; i < blockSize && i < bufferReadResult; i++)
                    micBufferData[i] = (double)bufferData[i]/(Short.MAX_VALUE / 2);
*/
                int dataToTransformSize = blockSize > bufferReadResult ? blockSize : bufferReadResult;

                for (int i = 0; i < dataToTransformSize; i++)
                    mDoublesCircularBuffer.add((double)bufferData[i]/(Short.MAX_VALUE / 2));

                while (mDoublesCircularBuffer.getSize() > blockSize){
                    double[] dataToTransorm = new double[blockSize];
                    for (int i = 0; i < blockSize; i++)
                        dataToTransorm[i] = mDoublesCircularBuffer.get();

                    //queue.add(dataToTransorm);

                    transformer.ft(dataToTransorm);

                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putDoubleArray(MicPoolRunnable.PCM_ARRAY_TAG, dataToTransorm);
                    bundle.putInt(MicPoolRunnable.PCM_ARRAY_SIZE_TAG, queue.size());
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);


                }

            }
            catch (Throwable t){
                Log.d(PCM_ARRAY_TAG, "An error occurred during recording", t);
            }
        }
        audioRecord.stop();
        audioRecord.release();

        Thread dummy = mFftTransformTask;
        dummy.interrupt();
        mFftTransformTask = null;

    }

}
