package com.acme.task;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import com.acme.task.listener.TaskListener;
import com.acme.util.Utils;

public class SmsTask extends AsyncTask<String, Void,String>{

    TaskListener listener;
    StringBuilder sb = new StringBuilder();

    public SmsTask(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        getSms();
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        listener.taskComplete(result);
    }

    private void getSms(){
        Cursor cursor = Utils.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    sb.append(" " + cursor.getColumnName(idx) + ":" + cursor.getString(idx));
                }
                // use msgData
//                System.out.println("sms -> "+sb.toString());
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
            System.out.println("no sms inbox available");
        }
    }
}
