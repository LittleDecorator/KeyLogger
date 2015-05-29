package com.acme.task;

import android.os.AsyncTask;
import com.acme.task.listener.TaskListener;
import com.acme.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import roboguice.util.Strings;

public class LoggerTask extends AsyncTask<String,Void,String>{

    private String id="";
    private StringBuffer data = new StringBuffer();

    public LoggerTask() {
        if(Strings.isEmpty(id)){
            id = Utils.restoreId();
        }
    }

    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        String url = urls[0];

        if(Utils.isConnected()){
            try {
                new HttpTask(data.toString(), null, id, new LoggerTaskListener()).execute(url);
            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        return result;
    }

    public void setData(StringBuffer data) {
        this.data = data;
    }

    class LoggerTaskListener implements TaskListener<String>{

        @Override
        public void taskComplete(String result) {
            data.delete(0,data.length());
            readResponse(result);
        }

        //read post response
        public void readResponse(String input){
            if(input.length()>0){
                System.out.println(input);
                try {
                    JSONObject object = new JSONObject(input);
                    String targetId = object.getString("targetId");
                    if(Strings.isEmpty(id) && Strings.notEmpty(targetId)){
                        id = targetId;
                        Utils.storeId(id);
                        Utils.startTaskService();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
