package com.parse.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.parse.app.proxy.IdjanguiProxyException;
import com.parse.app.proxy.IdjanguiProxyImpl;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import bolts.Task;

public class MyService extends Service {
    public static final String EVENT_ACTION = "getDate";
    private String dayOfWeek;
    private IdjanguiProxyImpl IdjanguiProxy = IdjanguiProxyImpl.getInstance();
    private Calendar calendar;
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
        Log.d("service","onCreate");

        super.onCreate();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("Service","stoped");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("Service","started");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayOfWeek = IdjanguiProxy.getDateNow();
                    Intent intent = new Intent(EVENT_ACTION);
                    intent.putExtra("DAY_OF_WEEK",dayOfWeek);
                    MyService.this.sendBroadcast(intent);
                } catch (IdjanguiProxyException e) {
                    Log.d("getDateNow",e.getMessage());
                }

            }
        });


        return START_STICKY;
    }

}
