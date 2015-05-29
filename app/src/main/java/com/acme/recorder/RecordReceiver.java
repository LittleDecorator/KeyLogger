package com.acme.recorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.acme.util.Constants;
import com.acme.util.RecordHelper;

/**
 *  Слушатель широковещательной рассылки
 */

public class RecordReceiver extends BroadcastReceiver {

    private String phoneNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("IN CALL RECORDER");
        phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        System.out.println("phone number -> "+phoneNumber);
        String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        System.out.println("extra state -> "+extraState);
        Log.d(Constants.CALL_RECORD_TAG, "RecordReceiver phoneNumber "+phoneNumber);

        if (RecordHelper.updateExternalStorageState() == Constants.MEDIA_MOUNTED) {
            try {
                if (extraState != null) {
                    if (extraState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        Intent myIntent = new Intent(context, CallRecorder.class);
                        myIntent.putExtra("commandType", Constants.STATE_CALL_START);
                        context.startService(myIntent);
                    } else if (extraState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        Log.d(Constants.CALL_RECORD_TAG, "RecordReceiver | IDLE | phoneNumber -> "+phoneNumber);
                        Intent myIntent = new Intent(context, CallRecorder.class);
                        myIntent.putExtra("commandType",Constants.STATE_CALL_END);
                        context.startService(myIntent);
                    } else if (extraState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        if (phoneNumber == null){
                            phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            Log.d(Constants.CALL_RECORD_TAG, "RecordReceiver | RINGING | phoneNumber ->"+phoneNumber);
                        }
                        Intent myIntent = new Intent(context,CallRecorder.class);
                        myIntent.putExtra("commandType",Constants.STATE_INCOMING_NUMBER);
                        myIntent.putExtra("phoneNumber", phoneNumber);
                        context.startService(myIntent);
                    }
                } else if (phoneNumber != null) {
                    Intent myIntent = new Intent(context, CallRecorder.class);
                    myIntent.putExtra("commandType",Constants.STATE_INCOMING_NUMBER);
                    myIntent.putExtra("phoneNumber", phoneNumber);
                    context.startService(myIntent);
                }
            } catch (Exception e) {
                Log.e(Constants.CALL_RECORD_TAG, "Exception");
                e.printStackTrace();
            }
        }
    }
}
