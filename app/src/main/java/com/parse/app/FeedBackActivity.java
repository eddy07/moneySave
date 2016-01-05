package com.parse.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.model.FeedBack;


public class FeedBackActivity extends ActionBarActivity {

    private EditText mMessage;
    private TextView mTextFeed;
    private Button mSend;
    private ParseUser user;
    private Context context;
    private FeedBack feedBack;
    public static final int TYPE_NOT_CONNECTED = 0;
    private ProgressDialog progressDialog;
    private SnackBar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nous contacter");
        context = this;
        mMessage = (EditText)findViewById(R.id.feedback);
        mSend = (Button)findViewById(R.id.sendBtn);
        mTextFeed = (TextView)findViewById(R.id.textFeed);
        setFontTile(mTextFeed);
        user = ParseUser.getCurrentUser();
        feedBack = new FeedBack();
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSend.setClickable(false);
                String message = mMessage.getText().toString();
                if (!message.isEmpty()) {
                    initprogress();
                    feedBack.setUser(user);
                    feedBack.setMessage(message);
                    feedBack.pinInBackground();
                    feedBack.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                progressDialog.dismiss();
                                snackBar(true);
                                mSend.setClickable(true);
                                //Toast.makeText(context,"Thanks for your feedback :)",Toast.LENGTH_LONG).show();
                            } else {
                                progressDialog.dismiss();
                                snackBar(false);
                                //Toast.makeText(context,"Erreur d'envoi",Toast.LENGTH_LONG).show();
                                mSend.setClickable(true);
                            }
                        }
                    });

                } else {
                    mMessage.setError("Champ vide");
                    mSend.setClickable(true);
                }
            }


        });

    }

    public void snackBar(boolean statu){
        if(statu == true) {
            snackbar = new SnackBar(this, "Thanks for your feedback :)", "Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        }else{
            snackbar = new SnackBar(this, "Oups... somethink went wrong, please try egain !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        }

        snackbar.show();
    }
    public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Sending ...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void setFontTile(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv.setTypeface(tf);
    }
}
