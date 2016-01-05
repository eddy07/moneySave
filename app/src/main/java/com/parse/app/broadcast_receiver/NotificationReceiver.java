package com.parse.app.broadcast_receiver;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.parse.ParsePushBroadcastReceiver;
import com.parse.app.MainActivity;

public class NotificationReceiver extends ParsePushBroadcastReceiver{
	
	@Override
	protected Class<? extends Activity> getActivity(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub

		return MainActivity.class;
	}

	@Override
	protected Notification getNotification(Context context, Intent intent) {
		// TODO Auto-generated method stub
		return super.getNotification(context, intent);
	}
	
	@Override
	protected void onPushDismiss(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onPushDismiss(context, intent);
	}
	
	@Override
	protected void onPushOpen(Context context, Intent intent) {
		Intent i = new Intent(context, MainActivity.class);
		i.putExtras(intent.getExtras());
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	
	@Override
	protected void onPushReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ConnectivityManager cm =
		        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		  if(intent == null)
		        return;
		  if(isConnected == true)
		super.onPushReceive(context, intent);
		  else return;
	}
	
}
