package com.homich.android.micfun;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigInteger;

/**
 * Created by samarin on 15-Jun-15.
 */
public class LevelFragment extends Fragment {

    private TextView mLevelField;
    private ImageView mImageView;
    private Button mStartButton;
    //MicPool<String> mMicPoolThread;
    private MicPoolRunnable mMicPoolRunnable;
    private Thread mMicPoolThread;
    private boolean isMicPoolThreadRunning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_level, container, false);

        v.setBackgroundColor(Color.BLACK);
        mLevelField = (TextView)v.findViewById(R.id.mic_level);
        mLevelField.setBackgroundColor(Color.GRAY);

        mImageView = (ImageView)v.findViewById(R.id.imageView);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int widthPixels = displaymetrics.widthPixels;
        final int heightPixels = 300;

        Bitmap bitmap = Bitmap.createBitmap((int) widthPixels, (int) heightPixels, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        mImageView.setImageBitmap(bitmap);

        mStartButton = (Button)v.findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        Bundle bundle = msg.getData();
                        double[] arr = bundle.getDoubleArray(MicPoolRunnable.PCM_ARRAY_TAG);
                        int size = bundle.getInt(MicPoolRunnable.PCM_ARRAY_SIZE_TAG);

                        canvas.drawColor(Color.BLACK);
                        for (int i = 0; i < arr.length; i++){
                            int x = (i * widthPixels) / arr.length;
                            int downy = (int)(heightPixels - (arr[i] * 10 * (heightPixels / 100)));
                            int upy = heightPixels;
                            canvas.drawLine(x, downy, x, upy, paint);
                        }
                        mImageView.invalidate();

                        mLevelField.setText(Integer.toString(size));
                        mLevelField.invalidate();
/*
                        double sum = 0;
                        //BigInteger bigInteger = BigInteger.ZERO;
                        for (int i = 0; i < size; i++) {
                            //sum += arr[i] + (Short.MAX_VALUE / 2);
                            sum += arr[i];
                            //bigInteger = bigInteger.add(BigInteger.valueOf(arr[i]));
                        }

                        double result = sum / size;
                        //long result = bigInteger.divide(BigInteger.valueOf(size)).longValue();

                        mLevelField.setText(Double.toString(result));
*/
                        //TextView mLevelField = (TextView)v.findViewById(R.id.mic_level);
                        //mLevelField.setText(Integer.toString(arr[3]));
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
            mMicPoolRunnable.StopRecording();
            Thread dummy = mMicPoolThread;
            dummy.interrupt();
            mMicPoolThread = null;
            Log.i("START", "Background thread destroyed");
        }
    }

}
