package com.app.smktelkom.notificationmotionactivity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.samsung.android.sdk.motion.SmotionActivityNotification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by SMK TELKOM on 03/01/2018.
 */

public class UserActivityNotificationFragment extends Fragment
        implements UserNotificationHelper.UserActivityNotificationCallback,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    CheckBox checkBoxStationary, checkBoxWalk, checkBoxRun, checkBoxVehicle;
    Button btnStart;
    TextView textInfo;
    UserNotificationHelper userNotificationHelper;

    UserNotificationFragmentCallback userNotificationFragmentCallback;

    public UserActivityNotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragmen_user_activity_notification, container, false);

        btnStart = (Button)view.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);

        checkBoxStationary = (CheckBox)view.findViewById(R.id.check_stationary);
        checkBoxWalk = (CheckBox)view.findViewById(R.id.check_walk);
        checkBoxRun = (CheckBox)view.findViewById(R.id.check_run);
        checkBoxVehicle = (CheckBox)view.findViewById(R.id.check_vehicle);
        checkBoxStationary.setOnCheckedChangeListener(this);
        checkBoxWalk.setOnCheckedChangeListener(this);
        checkBoxRun.setOnCheckedChangeListener(this);
        checkBoxVehicle.setOnCheckedChangeListener(this);

        textInfo = (TextView)view.findViewById(R.id.text_info);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity activity = (MainActivity)getActivity();

        userNotificationHelper = new UserNotificationHelper(Looper.getMainLooper(),activity.motion);

        userNotificationHelper.setcallback(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        MainActivity activity = (MainActivity)context;



        userNotificationFragmentCallback = activity;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userNotificationHelper.stop();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.btn_start){
            if (!userNotificationHelper.started) {

                if (checkBoxStationary.isChecked()) {
                    userNotificationHelper.addActivity(SmotionActivityNotification.Info.STATUS_STATIONARY);
                }
                if (checkBoxWalk.isChecked()){
                    userNotificationHelper.addActivity(SmotionActivityNotification.Info.STATUS_WALK);
                }
                if (checkBoxRun.isChecked()){
                    userNotificationHelper.addActivity(SmotionActivityNotification.Info.STATUS_RUN);
                }
                if (checkBoxVehicle.isChecked()){
                    userNotificationHelper.addActivity(SmotionActivityNotification.Info.STATUS_VEHICLE);
                }

                userNotificationHelper.start();

            }else{
                userNotificationHelper.stop();
            }
        }
    }

    @Override
    public void notificationStarted() {

        enableBox(false);
        btnStart.setText(R.string.stop);
        userNotificationFragmentCallback.userNotificationStarted();
    }

    @Override
    public void notificationStopped() {

        enableBox(true);
        btnStart.setText(R.string.start);
        userNotificationFragmentCallback.userNotificationStoppped();
    }

    @Override
    public void updateInfo(SmotionActivityNotification.Info info) {

        int status = info.getStatus();
        int accuracy = info.getAccuracy();
        long timestamp = info.getTimeStamp();

        StringBuffer result = new StringBuffer();

        result.append("Status : " + getStatus(status));
        result.append("\n");
        result.append("Accuracy : " + getAccuracy(accuracy));
        result.append("\n");
        result.append("Timestamp : " + timeToString(timestamp));

        textInfo.setText(result);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


    }

    void enableBox(boolean enable){
        checkBoxStationary.setEnabled(enable);
        checkBoxWalk.setEnabled(enable);
        checkBoxRun.setEnabled(enable);
        checkBoxVehicle.setEnabled(enable);
    }

    private String getStatus(int status) {
        String str = null;
        switch (status) {
            case SmotionActivityNotification.Info.STATUS_UNKNOWN:
                str = "Unknown";
                break;
            case SmotionActivityNotification.Info.STATUS_STATIONARY:
                str = "Stationary";
                break;
            case SmotionActivityNotification.Info.STATUS_WALK:
                str = "Walk";
                break;
            case SmotionActivityNotification.Info.STATUS_RUN:
                str = "Run";
                break;
            case SmotionActivityNotification.Info.STATUS_VEHICLE:
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
            case SmotionActivityNotification.Info.ACCURACY_LOW:
                str = "Low";
                break;
            case SmotionActivityNotification.Info.ACCURACY_MID:
                str = "Mid";
                break;
            case SmotionActivityNotification.Info.ACCURACY_HIGH:
                str = "High";
                break;
            default:
                break;
        }
        return str;
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

    public interface UserNotificationFragmentCallback{
        void userNotificationStarted();
        void userNotificationStoppped();
    }
}

