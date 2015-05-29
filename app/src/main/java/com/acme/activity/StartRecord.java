package com.acme.activity;


import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import com.acme.util.RecordHelper;
import com.acme.util.Utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class StartRecord implements Runnable {

    private MediaRecorder recorder;
    private Camera camera;
    private SurfaceHolder holder;
    private Activity activity;

    public StartRecord(MediaRecorder recorder, Camera camera, SurfaceHolder holder, Activity activity) {
        Log.i("Media", "start cons");
        this.recorder = recorder;
        this.camera = camera;
        this.holder = holder;
        this.activity = activity;
    }

    public void run() {
        startRecording();
    }


    public void startRecording() {
        if (!prepareRecorder()) {
            System.out.println("Fail in prepareMediaRecorder()!\n - Ended -");
            activity.finish();
        }
        try {
            recorder.start();
        } catch (Exception e){
            e.printStackTrace();
        }

        //try use task timer
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("task start");
                stopRecord();
            }
        };
        timer.schedule(task, 4000);
    }


    private boolean prepareRecorder(){
        System.out.println("prepareMediaRecorder");
        recorder = new MediaRecorder();

        camera.unlock();
        recorder.setCamera(camera);

        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        recorder.setOutputFile(RecordHelper.getPathForVideo());

        recorder.setPreviewDisplay(holder.getSurface());
        try {
            recorder.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            releaseRecorder();
            return false;
        }
        return true;

    }

    private void releaseRecorder() {
        System.out.println("releaseMediaRecorder");
        if (recorder != null) {
            recorder.reset(); // clear recorder configuration
            recorder.release(); // release the recorder object
            recorder = null;
            camera.lock(); // lock camera for later use
        }
    }

    public void stopRecord(){
        try{
            recorder.stop(); // stop the recording
            releaseRecorder(); // release the MediaRecorder object
            System.out.println("Video captured!");
            Utils.startSenderService();
            activity.finish();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
