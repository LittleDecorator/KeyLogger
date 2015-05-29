package com.acme.util;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.acme.Base;
import com.acme.db.ProjectHelper;
import com.acme.service.LocationService;
import com.acme.service.SenderService;
import com.acme.service.TaskService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import roboguice.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Utils {

    private static Context context;
    private static ProjectHelper helper;

    static {
        System.out.println("Init utils");
        context = Base.getContext();
        helper = new ProjectHelper(context);
    }

    //check if connected to internet
    public static boolean isConnected(){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    //check if extra "data" exist in intent
    public static boolean isDataPresent(Intent intent){
        boolean result = false;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("data") || extras.containsKey("id")) {
                result = true;
            }
        }
        return result;
    }

    public static String restoreId(){
        String id = Base.getId();
        if(Strings.isEmpty(id)){
            id = helper.getKeyValue("targetId");
        }
        return id;
    }

    public static void storeId(String id){
        Base.setId(id);
        helper.addKeyRecord("targetId",id);
    }

    public static String getUrlWithId(String url,List<NameValuePair> list){
        if(!url.endsWith("?")){
            url += "?";
        }
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("targetId", Base.getId()));
        params.addAll(list);

        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += paramString;
        System.out.println(url);
        return url;
    }

    public static String getUrlWithId(String url){
        return getUrlWithId(url,null);
    }

    public static void startTaskService(){
        new Thread(new ServiceStarter(TaskService.class)).start();
    }

    public static void startSenderService(){
        new Thread(new ServiceStarter(SenderService.class)).start();
    }

    public static void startLocationService(){
        new Thread(new ServiceStarter(LocationService.class)).start();
    }

    public static boolean initRequire(){
        return Strings.isEmpty(restoreId());
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static ProjectHelper getHelper() {
        return helper;
    }

    public static ContentResolver getContentResolver(){
        return context.getContentResolver();
    }

    private static class ServiceStarter implements Runnable {

        private Class clazz;

        public ServiceStarter(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public void run() {
            boolean isRunning = false;
            System.out.println("look up for "+clazz.getName());
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (clazz.getName().equals(service.service.getClassName())) {
                    //running
                    System.out.println(clazz.getName()+" is running");
                    isRunning = true;
                    break;
                }
            }
            if(!isRunning){
                System.out.println(clazz.getName()+" is NOT running");
                Intent intent = new Intent(context, clazz);
                context.startService(intent);
                System.out.println("try start service -> " +clazz.getName());
            }
        }
    }

    public static boolean isLocationEnabled(){
        boolean res;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        res = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(res){
            return true;
        } else {
            return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }

    }
}
