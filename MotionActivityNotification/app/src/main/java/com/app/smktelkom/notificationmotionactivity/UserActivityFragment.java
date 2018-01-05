package com.app.smktelkom.notificationmotionactivity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.samsung.android.sdk.motion.SmotionActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by SMK TELKOM on 03/01/2018.
 */

public class UserActivityFragment extends Fragment implements UserActivityHelper.UserActivityCallback {

    UserActivityHelper userActivityHelper;

    UserActivityFragmentCallback userActivityFragmentCallback;

    RadioGroup radioMode;
    Button btnStart;
    Button btnUpdateInfo;

    TextView textInfo;
    static private String TAG = "ActivityFragment";
    public UserActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_user_activity, container, false);

        textInfo = (TextView)view.findViewById(R.id.text_info);

        radioMode = (RadioGroup)view.findViewById(R.id.radio_mode);
        radioMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                boolean tes = false;
                switch (i){
                    case R.id.radio_realtime:
                        userActivityHelper.setModeActivity(SmotionActivity.Info.MODE_REALTIME);
                        break;
                    case R.id.radio_batch:
                        userActivityHelper.setModeActivity(SmotionActivity.Info.MODE_BATCH);
                        break;
                }
            }
        });

        btnStart = (Button)view.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userActivityHelper.started){
                    userActivityHelper.start();
                }else {
                    userActivityHelper.stop();
                }
            }
        });

        btnUpdateInfo = (Button)view.findViewById(R.id.btn_update);
        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userActivityHelper.updateInfo();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();

        userActivityHelper = new UserActivityHelper(Looper.getMainLooper(),activity.motion);

        userActivityHelper.setCallback(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        MainActivity activity = (MainActivity) context;

        userActivityFragmentCallback = activity;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        userActivityHelper.stop();

    }

    @Override
    public void activityStarted() {

        enableRadio(false);
        btnUpdateInfo.setEnabled(true);
        btnStart.setText(R.string.stop);
        userActivityFragmentCallback.userActivityStarted();

    }

    @Override
    public void activityStopped() {

        enableRadio(true);
        btnUpdateInfo.setEnabled(false);
        btnStart.setText(R.string.start);
        userActivityFragmentCallback.userActivityStoppped();

    }

    @Override
    public void updateInfo(int mode, SmotionActivity.Info[] info) {

        StringBuffer result = new StringBuffer();

        switch (mode) {
            case SmotionActivity.Info.MODE_REALTIME:

                int status = info[0].getStatus();
                int accuracy = info[0].getAccuracy();
                long timestamp = info[0].getTimeStamp();

                result.append("Status : " + getStatus(status));
                result.append("\n");
                result.append("Accuracy : " + getAccuracy(accuracy));
                result.append("\n");
                result.append("Timestamp : " + timeToString(timestamp));

                break;

            case SmotionActivity.Info.MODE_BATCH:

                for (SmotionActivity.Info data : info){

                    status = data.getStatus();
                    accuracy = data.getAccuracy();
                    timestamp = data.getTimeStamp();

                    result.append("Status : " + getStatus(status));
                    result.append("\n");
                    result.append("Accuracy : " + getAccuracy(accuracy));
                    result.append("\n");
                    result.append("Timestamp : " + timeToString(timestamp));
                    result.append("\n");
                    result.append("=====");
                    result.append("\n");
                }


                break;

        }

        textInfo.setText(result);

    }

    private String timeToString(long timestamp){
        String timestampString;
        if (timestamp == 0) {
            timestampString = "00:00:00";
        } else {
            Date date = new Date(timestamp);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            timestampString = formatter.format(date);
        }
        return timestampString;
    }
    private String getStatus(int status) {
        String str = null;
        switch (status) {
            case SmotionActivity.Info.STATUS_UNKNOWN:
                str = "Unknown";
                break;
            case SmotionActivity.Info.STATUS_STATIONARY:
                str = "Stationary";
                break;
            case SmotionActivity.Info.STATUS_WALK:
                str = "Walk";
                break;
            case SmotionActivity.Info.STATUS_RUN:
                str = "Run";
                break;
            case SmotionActivity.Info.STATUS_VEHICLE:
                str = "Vehicle";
                break;
            default:
                break;
        }
        return str;
    }

    private String getAccuracy(int accuracy) {
        String str = null;
        switch (accuracy) {
            case SmotionActivity.Info.ACCURACY_LOW:
                str = "Low";
                break;
            case SmotionActivity.Info.ACCURACY_MID:
                str = "Mid";
                break;
            case SmotionActivity.Info.ACCURACY_HIGH:
                str = "High";
                break;
            default:
                break;
        }
        return str;
    }

    private String getMode(int mode) {
        String str = null;
        switch (mode) {
            case SmotionActivity.Info.MODE_REALTIME:
                str = "Real time";
                break;
            case SmotionActivity.Info.MODE_BATCH:
                str = "Batch";
                break;
            case SmotionActivity.Info.MODE_ALL:
                str = "ALL";
                break;
            default:
                break;
        }
        return str;
    }

    private void enableRadio(boolean enable) {

        for (int i = 0; i < radioMode.getChildCount(); i++) {
            ((RadioButton) radioMode.getChildAt(i)).setEnabled(enable);
        }
    }
    public interface UserActivityFragmentCallback{
        void userActivityStarted();
        void userActivityStoppped();
    }
}