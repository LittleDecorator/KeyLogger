package com.acme.service;

import android.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

public class InfoService {

    public static final String OSVERSION = System.getProperty("os.version");
    public static final String RELEASE = android.os.Build.VERSION.RELEASE;
    public static final String DEVICE = android.os.Build.DEVICE;
    public static final String MODEL = android.os.Build.MODEL;
    public static final String PRODUCT = android.os.Build.PRODUCT;
    public static final String BRAND = android.os.Build.BRAND;
    public static final String DISPLAY = android.os.Build.DISPLAY;
    public static final String CPU_ABI = android.os.Build.CPU_ABI;
    public static final String CPU_ABI2 = android.os.Build.CPU_ABI2;
    public static final String UNKNOWN = android.os.Build.UNKNOWN;
    public static final String HARDWARE = android.os.Build.HARDWARE;
    public static final String ID = android.os.Build.ID;
    public static final String MANUFACTURER = android.os.Build.MANUFACTURER;
    public static final String SERIAL = android.os.Build.SERIAL;
    public static final String USER = android.os.Build.USER;
    public static final String HOST = android.os.Build.HOST;

    public static int currentApiVersion(){
        return Build.VERSION.SDK_INT;
    }

    public static JSONObject getInfo() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("device",DEVICE);
        jsonObject.put("model",MODEL);
        jsonObject.put("api",currentApiVersion());
        return jsonObject;
    }

}
