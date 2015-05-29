package com.acme.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import com.acme.task.LoggerTask;
import com.acme.util.Constants;
import com.acme.util.Utils;

import java.util.Date;

public class LocationService extends Service {

    LocationManager locationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //если слушатель включен
        if(!Utils.isLocationEnabled()){
            LoggerTask senderTask = new LoggerTask();
            senderTask.setData(new StringBuffer("Провайдеры отключены. Нет возможности получить координаты!"));
            senderTask.execute(Constants.LOGGER_URL);
            stopSelf();
        } else {
            //регистрируем слушателя для получения данных
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10,locationListener);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            /**
             * новые данные о местоположении
             */
            sendLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            /**
             * Указанный провайдер был отключен юзером.
             */
        }

        @Override
        public void onProviderEnabled(String provider) {
            /**
             * Указанный провайдер был включен юзером. Далее методом getLastKnownLocation (он может вернуть null) запрашиваем последнее доступное местоположение от включенного провайдера.
             * Оно может быть вполне актуальным, если вы до этого использовали какое-либо приложение с определением местоположения.
             */
            sendLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            /**
             * В поле status могут быть значения OUT_OF_SERVICE (данные будут недоступны долгое время), TEMPORARILY_UNAVAILABLE (данные временно недоступны), AVAILABLE (все ок, данные доступны)
             */
        }
    };

    private void sendLocation(Location location) {
        if(location!=null){
            if (location.getProvider().contentEquals(LocationManager.GPS_PROVIDER )|| location.getProvider().contentEquals(LocationManager.NETWORK_PROVIDER)) {
                LoggerTask senderTask = new LoggerTask();
                senderTask.setData(new StringBuffer(formatLocation(location)));
                senderTask.execute(Constants.LOGGER_URL);
                stopSelf();
            }
        }
    }

    private String formatLocation(Location location) {
        if (location == null){
            return "";
        } else {
            return String.format("Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",location.getLatitude(), location.getLongitude(), new Date(location.getTime()));
        }
    }

}
