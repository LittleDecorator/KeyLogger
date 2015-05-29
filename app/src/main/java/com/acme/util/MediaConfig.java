package com.acme.util;

import android.content.Intent;
import android.hardware.Camera;

public class MediaConfig {

    //our custom configuration
    private boolean pictureMode = true;
    private boolean videoMode = false;
    private boolean audioMode = false;
    private boolean useFlash = false;
    private boolean disableSound = false;
    private boolean useFrontCamera = false;
    private int mediaHeight = 288;
    private int mediaWidth = 352;
    private int duration = 6000;           // Set max duration 60 sec.
    private long fileSize = 500000;     // Set max file size 5M

    public MediaConfig() {}

    public boolean isPictureMode() {
        return pictureMode;
    }

    public boolean isVideoMode() {
        return videoMode;
    }

    public boolean isUseFlash() {
        return useFlash;
    }

    public boolean isDisableSound() {
        return disableSound;
    }

    public boolean isAudioMode() {
        return audioMode;
    }

    public int getMediaHeight() {
        return mediaHeight;
    }

    public int getMediaWidth() {
        return mediaWidth;
    }

    public int getDuration() {
        return duration;
    }

    public long getFileSize() {
        return fileSize;
    }

    //read config from intent
    public void readIntent(Intent intent){
        System.out.println("readIntent");
        if(intent!=null){
            String type = intent.getStringExtra("mediaType");
            if(type!=null){
                switch(type){
                    case "photo":
                        pictureMode = Boolean.TRUE;
                        videoMode = Boolean.FALSE;
                        audioMode = Boolean.FALSE;
                        break;
                    case "video":
                        videoMode = Boolean.TRUE;
                        pictureMode = Boolean.FALSE;
                        audioMode = Boolean.FALSE;
                        break;
                    case "audio":
                        audioMode = Boolean.TRUE;
                        videoMode = Boolean.FALSE;
                        pictureMode = Boolean.FALSE;
                        break;
                }
            }
            useFlash = intent.getBooleanExtra("flash",useFlash);
            useFrontCamera = intent.getBooleanExtra("frontCamera",useFrontCamera);
            disableSound = intent.getBooleanExtra("sound",disableSound);
            mediaHeight = intent.getIntExtra("height",mediaHeight);
            mediaWidth = intent.getIntExtra("width",mediaWidth);
        }
    }

    public int getCameraType(){
        if(useFrontCamera){
            return Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            return Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }
}
