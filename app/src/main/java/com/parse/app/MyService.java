package com.parse.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Task;

public class MyService extends Service {
    public static final String EVENT_ACTION = "MyServiceEventType";
    boolean started;
    Timer timer;
    TimerTask task;
    public MyService() {
    }

    public class MyBinder extends Binder{
        MyService getSerivce(){
            return MyService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return new MyBinder();
    }
    @Override
    public void onCreate(){
        timer = new Timer();
        final String date = DateFormat.getDateTimeInstance().format(new Date());
        task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(EVENT_ACTION);
                intent.putExtra("DATE",date);
                MyService.this.sendBroadcast(intent);
            }
        };

        super.onCreate();
    }
    @Override
    public void onDestroy(){
        timer.cancel();
        super.onDestroy();
    }
    @Override
    public void onStart(Intent intent, int startId){
        if(started == false){
            timer.scheduleAtFixedRate(task,0,3000);
            started = true;
        }
        super.onStart(intent, startId);
    }

}
