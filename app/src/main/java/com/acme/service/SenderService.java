package com.acme.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.acme.socket.SocketClient;
import com.acme.task.HttpTask;
import com.acme.task.listener.TaskListener;
import com.acme.util.Constants;
import com.acme.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import roboguice.util.Strings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SenderService extends Service{

    private SocketClient socketClient;
    private Map<Long,String> map;
    private List<Long> keys;
    String path = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkAndSend();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /* Task Listener class */
    public class TaskCallbackListener implements TaskListener<String> {

        @Override
        public void taskComplete(String result) {
            JSONObject object = null;
            try {
                object = new JSONObject(result);
                int port = object.getInt("port");
                String server = object.getString("server");
                Boolean ready = object.getBoolean("ready");
                if(ready!=null){
                    socketClient = new SocketClient(port,server);
                    try {
                        socketClient.sendData(path);
                        socketClient.getSoketThread().join();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    Utils.getHelper().deleteFileRow(keys.get(0));
                    //try remove file itself
                    new File(path).delete();
                    keys.remove(0);
                    path = "";
                    Utils.getHelper().getAllFromFileTable();
                    checkAndSend();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void checkAndSend(){
        if(Utils.isConnected()){
            String id = Utils.restoreId();

            if(keys!=null && keys.size()>0){
                path = map.get(keys.get(0));
            } else if(map == null && Utils.getHelper().couFilePointers()>0) {
                map = Utils.getHelper().getFilePath();
                keys = new ArrayList<>(map.keySet());
                path = map.get(keys.get(0));
            } else {
                stopSelf();
                //Знаю что тупо, но пока пусть будет здесь
                Utils.startTaskService();
            }
            if(Strings.notEmpty(path)){
                new HttpTask(null,path.substring(path.lastIndexOf(".")+1),id,new TaskCallbackListener()).execute(Constants.SOCKET_URL);
            }
        }
    }
}
