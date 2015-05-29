package com.acme.task;

import android.os.AsyncTask;
import android.util.Log;
import com.acme.task.listener.TaskListener;
import com.acme.util.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import roboguice.util.Strings;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpTask extends AsyncTask<String, Void, String> {

    TaskListener listener;
    private String data;
    private String type;
    private String id;

    public HttpTask(String data, String type, String id, TaskListener listener) {
        this.data = data;
        this.type = type;
        this.id = id;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        System.out.println("in http back");
        InputStream inputStream;
        String result = "";
        String url = urls[0];

        try {
            System.out.println("create http client");
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse;

            if ((data != null && !data.isEmpty())) {
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> maps = new ArrayList<>();
                maps.add(new BasicNameValuePair("data",data));
                maps.add(new BasicNameValuePair("targetId",id));
                maps.add(new BasicNameValuePair("mime",type));
                httpPost.setEntity(new UrlEncodedFormEntity(maps, "UTF-8"));
                httpResponse = httpclient.execute(httpPost);
                System.out.println("httpPostResponse came");
                inputStream = httpResponse.getEntity().getContent();
            } else{
                //if use sender service, then we need mime type here
                String compositUrl;
                if(Strings.notEmpty(type)){
                    compositUrl = Utils.getUrlWithId(url, Arrays.asList(new NameValuePair[]{new BasicNameValuePair("mime",type)}));
                } else {
                    compositUrl = Utils.getUrlWithId(url);
                }
                HttpGet httpGet = new HttpGet(compositUrl);
                httpResponse = httpclient.execute(httpGet);
                System.out.println("httpGetResponse came");
                inputStream = httpResponse.getEntity().getContent();
            }
            if (inputStream != null) {
                result = Utils.convertInputStreamToString(inputStream);
                System.out.println("request result -> "+result);
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if(listener!=null){
            listener.taskComplete(result);
        } else {
            System.out.println(HttpTask.class + " : no callback listener");
        }
    }


}
