package com.acme.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordHelper {

    public static int updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return Constants.MEDIA_MOUNTED;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return Constants.MEDIA_MOUNTED_READ_ONLY;
        } else {
            return Constants.NO_MEDIA;
        }
    }

    private static String getFilePath(String prefix,String postfix){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.FILE_DIRECTORY);
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(Constants.FILE_DIRECTORY, "failed to create directory");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + prefix +"_"+ timeStamp + "."+ postfix).getPath();
    }

    public static String getPathForAudio(){
        String path = getFilePath("AUD","3gp");
        stashFilePointer(path);
        return path;
    }

    public static String getPathForImage(){
        String path = getFilePath("IMG","jpeg");
        stashFilePointer(path);
        return path;
    }

    public static String getPathForRecord(){
        String path = getFilePath("REC","3gp");
        stashFilePointer(path);
        return path;
    }

    public static String getPathForVideo(){
        String path = getFilePath("VID","mp4");
        stashFilePointer(path);
        return path;
    }

    private static void stashFilePointer(String path){
        Utils.getHelper().addFileRecord(path);
    }
}
