package com.app.smktelkom.notificationmotionactivity;

import android.os.Looper;

import com.samsung.android.sdk.motion.Smotion;
import com.samsung.android.sdk.motion.SmotionActivityNotification;

/**
 * Created by SMK TELKOM on 03/01/2018.
 */

public class UserNotificationHelper {

    private UserActivityNotificationCallback userActivityNotificationCallback;
    private SmotionActivityNotification mActivityNotification;

    private SmotionActivityNotification.InfoFilter mFilter = null;

    public boolean started = false;

    public UserNotificationHelper(Looper looper, Smotion motion) {
        // TODO Auto-generated constructor stub
        mActivityNotification = new SmotionActivityNotification(looper, motion);
    }

    public void setcallback(UserActivityNotificationCallback callback) {
        userActivityNotificationCallback = callback;
    }

    public void addActivity(int activities) {
        if (mFilter == null) {
            mFilter = new SmotionActivityNotification.InfoFilter();
        }
        mFilter.addActivity(activities);
    }

    public void start() {
        if (!started && mFilter != null) {

            mActivityNotification.start(mFilter, changeListener);
            started = true;
            userActivityNotificationCallback.notificationStarted();
        }
    }

    public void stop() {
        if(started && mFilter != null) {
            mActivityNotification.stop();
            started = false;
            userActivityNotificationCallback.notificationStopped();
            mFilter = null;
        }
    }

    private SmotionActivityNotification.ChangeListener changeListener = new SmotionActivityNotification.ChangeListener() {

        @Override
        public void onChanged(SmotionActivityNotification.Info info) {
            // TODO Auto-generated method stub

            userActivityNotificationCallback.updateInfo(info);
        }
    };

    public interface UserActivityNotificationCallback {
        void notificationStarted();

        void notificationStopped();

        void updateInfo(SmotionActivityNotification.Info info);
    }
}