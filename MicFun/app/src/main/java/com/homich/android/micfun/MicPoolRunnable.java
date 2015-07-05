package com.homich.android.micfun;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

        int freq = 44100;
        int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = 0;
        short[] buffer = null;
        isRecording = true;

        try {
            bufferSize = AudioRecord.getMinBufferSize(freq, channelConfiguration, audioEncoding);
            buffer = new short[bufferSize];
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

        while (isRecording)
        {
            try {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);

                Message msg = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putShortArray(PCM_ARRAY_TAG, buffer);
                bundle.putInt(PCM_ARRAY_SIZE_TAG, bufferReadResult);
                msg.setData(bundle);
                mHandler.sendMessage(msg);

            }
            catch (Throwable t){
                Log.d(PCM_ARRAY_TAG, "An error occurred during recording", t);
            }
        }
        audioRecord.stop();
        audioRecord.release();
    }

}
