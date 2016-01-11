package com.parse.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.parse.app.proxy.IdjanguiProxyException;
import com.parse.app.proxy.IdjanguiProxyImpl;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GetDateService extends Service {
    public static final String EVENT_ACTION = "getDateService";
    boolean started;
    Timer timer;
    TimerTask task;
    private String dayOfWeek;
    private IdjanguiProxyImpl IdjanguiProxy = IdjanguiProxyImpl.getInstance();
    private Calendar calendar;
    public GetDateService() {
    }

    public class MyBinder extends Binder{
        GetDateService getSerivce(){
            return GetDateService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return new MyBinder();
    }
    @Override
    public void onCreate(){
        Log.d("Service","onCreate");
        System.out.println("service create");
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                try {
                    dayOfWeek = IdjanguiProxy.getDateNow();
                    Intent intent = new Intent(EVENT_ACTION);
                    intent.putExtra("DAY_OF_WEEK",dayOfWeek);
                    GetDateService.this.sendBroadcast(intent);
                } catch (IdjanguiProxyException e) {
                    Log.d("getDateNow", e.getMessage());
                }
            }
        };

        super.onCreate();
    }
    @Override
    public void onDestroy(){
        timer.cancel();
        super.onDestroy();
        Log.d("Service","onDestroy");
        System.out.println("service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("Service","started");
        System.out.println("service started");
        if(started == false){
            timer.scheduleAtFixedRate(task,0,1000);
            started = true;
        }
        return START_STICKY;
    }

}
