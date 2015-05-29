package com.acme.task;

import com.acme.util.Constants;
import com.acme.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class LoggerTaskStarter {

    private Timer timer;
    private LoggerTask senderTask;

    public LoggerTaskStarter() {
        if(Utils.initRequire()){
            new LoggerTask().execute(Constants.LOGGER_URL);
        }
    }

    class SenderTimerTask extends TimerTask {
        public void run() {
            senderTask.execute(Constants.LOGGER_URL);
        }
    }

    public void restartTask(StringBuffer data){
        if(timer!=null){
            clear();
        }
        timer = new Timer();
        senderTask = new LoggerTask();
        senderTask.setData(data);
        timer.schedule(new SenderTimerTask(),Constants.LOGGER_INTERVAL);

    }

    public void runTask(StringBuffer data){
        senderTask = new LoggerTask();
        senderTask.setData(data);
        senderTask.execute(Constants.LOGGER_URL);
    }

    public void clear(){
        timer.cancel();
        timer.purge();
    }


}
