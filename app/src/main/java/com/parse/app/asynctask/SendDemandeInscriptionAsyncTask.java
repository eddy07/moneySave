package com.parse.app.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.parse.app.R;
import com.parse.app.proxy.IdjanguiProxyException;
import com.parse.app.proxy.IdjanguiProxyImpl;
import com.parse.app.utilities.NetworkUtil;

public class SendDemandeInscriptionAsyncTask extends AsyncTask<Void, Void, Boolean> {
	
    private String userId;
    private String channel;
    private String alert;
    private String title;
    private String status;
    private TextView btnInvite;
    private String invitationChannel;
    private SnackBar snackBar;
    private ProgressDialog progressDialog;
	private IdjanguiProxyImpl IdjanguiProxy = IdjanguiProxyImpl.getInstance();
	private Activity activity;
	public static int TYPE_NOT_CONNECTED = 0;
	
	public SendDemandeInscriptionAsyncTask(String userId, String channel, String title, String alert, Activity activity, TextView btnInvite, String invitationChannel) {
		super();
		this.userId = userId;
		this.channel = channel;
		this.title = title;
		this.alert = alert;
		this.activity = activity;
        this.btnInvite = btnInvite;
        this.invitationChannel = invitationChannel;
	}

    public void snackBar(boolean statu, String context){
        if((statu == true) && (context == null)) {
            snackBar = new SnackBar(activity, "Demande envoyé !", "Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
            Log.d("Invitation", "envoyé");
            btnInvite.setText("Demande envoyé");
            btnInvite.setBackgroundResource(R.drawable.bg_invite_send);
            btnInvite.setClickable(false);
            ParsePush.subscribeInBackground(invitationChannel, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("com.parse.push", "successfully subscribed to the channel.");
                    } else {
                        Log.e("com.parse.push", "failed to subscribe for push" + e);
                    }
                }
            });
        }else if ((statu == false) && (context == null)){
            snackBar = new SnackBar(activity, "Erreur lors de l'envoi de la requete !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else{
            snackBar = new SnackBar(activity, "Erreur réseau !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }

        snackBar.show();
    }
	/*private void showProgress(boolean show){
        progressDialog  = new ProgressDialog(activity.getApplicationContext());
        progressDialog.setMessage("sending ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        if(show == true) progressDialog.show();
        else progressDialog.dismiss();
	}*/
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//showProgress(true);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {

		
		try {
			if(NetworkUtil.getConnectivityStatus(activity.getApplicationContext())==TYPE_NOT_CONNECTED){
				//Toast.makeText(activity.getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                snackBar(false,"noInternet");
			}else{
				status = IdjanguiProxy.sendPush(userId,channel,alert,title);
            }
			
		} catch (IdjanguiProxyException e) {

			e.printStackTrace();
		}
		if(status.isEmpty() || status==null || status == "push_fail")
			return false;
		else
			return true;
	}
	
	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		//showProgress(false);
		if (success) {
            //Toast.makeText(activity.getApplicationContext(), "Demande envoyé !", Toast.LENGTH_LONG).show();
            snackBar(true, null);

		} else {
			//Toast.makeText(activity.getApplicationContext(), "Erreur lors de l'envoi de la requete !", Toast.LENGTH_LONG).show();
           snackBar(false, null);
		}
	}
	
	@Override
	protected void onCancelled() {

		super.onCancelled();
		//showProgress(false);
	}


}
