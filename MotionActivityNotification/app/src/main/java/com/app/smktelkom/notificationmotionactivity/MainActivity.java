package com.app.smktelkom.notificationmotionactivity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.motion.Smotion;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,UserActivityFragment.UserActivityFragmentCallback,
        UserActivityNotificationFragment.UserNotificationFragmentCallback {

    public Smotion motion = new Smotion();
    Spinner spinner;

    static private String TAG = "MotionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            initializeSDK();
        }catch (IllegalArgumentException e){

            Log.d(TAG, "onCreate: Something went wrong");
            showErrorDialog("Something went wrong",e.getMessage());

            return;
        }catch (SsdkUnsupportedException e){

            Log.d(TAG, "onCreate: SDK tidak support");
            showErrorDialog("SDK tidak support",e.getMessage());

            return;
        }

        spinner = (Spinner)findViewById(R.id.spinner_menu);
        spinner.setOnItemSelectedListener(this);

    }

    void initializeSDK() throws IllegalArgumentException,SsdkUnsupportedException{

        motion.initialize(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Log.d(TAG, "onItemSelected: "+i);
        Fragment fragment = null;
        Class fragmentClass = null;

        switch(i){
            case 0:
                Log.d(TAG, "onItemSelected: case 0");
                fragmentClass = UserActivityFragment.class;
                break;
            case 1:
                Log.d(TAG, "onItemSelected: case 1");

                fragmentClass = UserActivityNotificationFragment.class;
                break;
            default:
                Log.d(TAG, "onItemSelected: case default");

                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "catch Item selected: "+e.getMessage());
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void showErrorDialog(String title,String message){

        new AlertDialog().Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void userActivityStarted() {

        spinner.setEnabled(false);
    }

    @Override
    public void userActivityStoppped() {

        spinner.setEnabled(true);

    }


    @Override
    public void userNotificationStarted() {

        spinner.setEnabled(false);
    }

    @Override
    public void userNotificationStoppped() {

        spinner.setEnabled(true);


    }
}