package com.homich.android.micfun;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by samarin on 15-Jun-15.
 */
public class LevelFragment extends Fragment {

    private TextView mLevelField;
    private Button mStartButton;
    //MicPool<String> mMicPoolThread;
    private MicPoolRunnable mMicPoolRunnable;
    private Thread mMicPoolThread;
    private boolean isMicPoolThreadRunning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_level, container, false);

        mLevelField = (TextView)v.findViewById(R.id.mic_level);
        mStartButton = (Button)v.findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        Bundle bundle = msg.getData();
                        int[] arr = bundle.getIntArray(MicPoolRunnable.ARRAY_TAG);

                        //TextView mLevelField = (TextView)v.findViewById(R.id.mic_level);
                        mLevelField.setText(Integer.toString(arr[3]));
                        //mLevelField.setText("Test");
                    }
                };

                if (isMicPoolThreadRunning) {
                    MicPoolThreadStop();
                    mStartButton.setText(R.string.button_start_start);
                    isMicPoolThreadRunning = false;
                } else {
                    mMicPoolRunnable = new MicPoolRunnable(handler);
                    mMicPoolThread = new Thread(mMicPoolRunnable);
                    mMicPoolThread.start();
                    mStartButton.setText(R.string.button_start_stop);
                    Log.i("START", "Background thread started");
                    isMicPoolThreadRunning = true;
                }

                //mLevelField.setText("Her");
                //mStartButton.setText("vam");
/*
                mMicPoolThread = new MicPool<String>();
                mMicPoolThread.start();
                mMicPoolThread.getLooper();
                Log.i("START", "Background thread started");
                mMicPoolThread.queuePool("Test");
*/
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mMicPoolThread.quit();
        MicPoolThreadStop();
    }

    private void MicPoolThreadStop(){
        if (mMicPoolThread != null) {
            Thread dummy = mMicPoolThread;
            dummy.interrupt();
            mMicPoolThread = null;
            Log.i("START", "Background thread destroyed");
        }
    }

}
