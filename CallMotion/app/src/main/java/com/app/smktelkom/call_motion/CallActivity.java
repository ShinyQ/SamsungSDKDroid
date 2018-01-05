package com.app.smktelkom.call_motion;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.motion.Smotion;
import com.samsung.android.sdk.motion.SmotionCall;

/**
 * Created by SMK TELKOM on 03/01/2018.
 */

    public class CallActivity extends AppCompatActivity {

        private Smotion mMotion;
        private SmotionCall mCall;

        public static final String EXTRAS_NAME = "extras_name";
        public static final String EXTRAS_NUMBER = "extras_number";

        TextView textName;
        TextView textNumber;

        private String TAG = "MOTION_TAG";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.callactivity);

            textName = (TextView) findViewById(R.id.text_name);
            textNumber = (TextView) findViewById(R.id.text_number);

            String name = getIntent().getExtras().getString(EXTRAS_NAME);
            String number = getIntent().getExtras().getString(EXTRAS_NUMBER);

            textName.setText(name);
            textNumber.setText(number);

            initMotion();
        }

        private void initMotion() {

            mMotion = new Smotion();
            try {
                mMotion.initialize(this);
            } catch (IllegalArgumentException e) {
                // Error handling
                Log.d(TAG, "initMotion: "+e.getMessage());
            } catch (SsdkUnsupportedException e) {
                // Error handling
                Log.d(TAG, "initMotion: "+e.getMessage());
            }

            mCall = new SmotionCall(Looper.getMainLooper(), mMotion);

        }

        @Override
        protected void onStart() {
            super.onStart();
            mCall.start(changeListener);
        }

        @Override
        protected void onStop() {
            super.onStop();
            mCall.stop();
        }


        private static final int PERMISSION_REQUEST_CALL = 100;

        public void askForContactPermission(String phone) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CALL_PHONE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Contacts access needed");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setMessage("please confirm Contacts access");//TODO put real question
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                requestPermissions(
                                        new String[]
                                                {Manifest.permission.CALL_PHONE}
                                        , PERMISSION_REQUEST_CALL);
                            }
                        });
                        builder.show();
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                PERMISSION_REQUEST_CALL);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    doCall(phone);
                }
            } else {
                doCall(phone);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case PERMISSION_REQUEST_CALL: {

                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Toast.makeText(this, "No permission for contacs", Toast.LENGTH_SHORT).show();

                    }
                    return;
                }

            }
        }

        final SmotionCall.ChangeListener changeListener = new
                SmotionCall.ChangeListener() {

                    @Override
                    public void onChanged(SmotionCall.Info info) {
// TODO Auto-generated method stub

                        switch (info.getCallPosition()) {
                            case SmotionCall.POSITION_LEFT:
                                Log.d("Call", "onChanged: left");
                                askForContactPermission(textNumber.getText().toString());
                                break;

                            case SmotionCall.POSITION_RIGHT:
                                Log.d("Call", "onChanged: right");
                                askForContactPermission(textNumber.getText().toString());
                                break;
                        }
                    }
                };

        void doCall(String phone) {

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setPackage("com.android.server.telecom");
            intent.setData(Uri.fromParts("tel", phone, null));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            startActivity(intent);
        }
    }

