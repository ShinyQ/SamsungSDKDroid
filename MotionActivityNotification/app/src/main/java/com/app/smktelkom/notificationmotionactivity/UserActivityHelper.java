package com.app.smktelkom.notificationmotionactivity;

import android.os.Looper;
import android.util.Log;

import com.samsung.android.sdk.motion.Smotion;
import com.samsung.android.sdk.motion.SmotionActivity;

/**
 * Created by SMK TELKOM on 03/01/2018.
 */

public class UserActivityHelper {

    private SmotionActivity mActivity = null;

    private int mMode = SmotionActivity.Info.MODE_REALTIME;

    public boolean started = false;

    private UserActivityCallback userActivityCallback ;

    public UserActivityHelper(Looper looper, Smotion motion) {
        mActivity = new SmotionActivity(looper, motion);
    }

    public void setModeActivity(int mode){

        mMode = mode;
    }

    public void setCallback(UserActivityCallback callback){
        userActivityCallback = callback;
    }

    public void start() {
        if (!started) {
            mActivity.start(mMode, changeListener);
            started = true;
            userActivityCallback.activityStarted();
        }
    }

    public void stop() {
        if (started) {
            mActivity.stop();
            started = false;
            userActivityCallback.activityStopped();
        }

    }

    public void updateInfo() {
        if (mActivity == null)
            return;
        mActivity.updateInfo();
    }

    public boolean isUpdateInfoBatchModeSupport() {
        return mActivity.isUpdateInfoBatchModeSupport();
    }

    static private String TAG = "ActionNotifHelper";
    private SmotionActivity.ChangeListener changeListener = new SmotionActivity.ChangeListener() {


        @Override
        public void onChanged(int mode, SmotionActivity.Info[] infoArray) {

            switch (mode){
                case SmotionActivity.Info.MODE_REALTIME:
                    break;
                case SmotionActivity.Info.MODE_BATCH:
                    Log.d(TAG, "onChanged: BATCH");

                    break;
                case SmotionActivity.Info.MODE_ALL:
                    Log.d(TAG, "onChanged: ALL");

                    break;
            }

            userActivityCallback.updateInfo(mode,infoArray);
        }
    };

    public interface UserActivityCallback {
        void activityStarted();
        void activityStopped();
        void updateInfo(int mode,SmotionActivity.Info[] info);
    }

}
