package com.parse.app;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import me.drakeet.materialdialog.MaterialDialog;


public class ChangePasswordActivity extends ActionBarActivity {

    private EditText mEmail;
    private Button mSendBtn, mOkBtn;
    private String email;
    private SnackBar snackbar;
    private TextView tv;
    public static int TYPE_NOT_CONNECTED = 0;
    private ProgressWheel progressWheel;
    private Context context;
    private LinearLayout formresult;
    private LinearLayout form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide();
        context =this;
        form = (LinearLayout)findViewById(R.id.form);
        formresult = (LinearLayout)findViewById(R.id.formresult);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);
        mEmail = (EditText)findViewById(R.id.email);
        mSendBtn = (Button)findViewById(R.id.sendBtn);
        mOkBtn = (Button)findViewById(R.id.okBtn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSendBtn.setClickable(false);
                if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
                    snackBar(false);
                    mSendBtn.setClickable(true);
                } else {
                    email = mEmail.getText().toString();
                    if (!email.isEmpty()) {
                        if(!email.contains("@")){
                            emailError();
                            mSendBtn.setClickable(true);
                        }else {
                            progressWheel.setVisibility(View.VISIBLE);
                            ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // An email was successfully sent with reset instructions.
                                        progressWheel.setVisibility(View.GONE);
                                        mSendBtn.setClickable(true);
                                        form.setVisibility(View.GONE);
                                        formresult.setVisibility(View.VISIBLE);
                                        mOkBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                finish();
                                            }
                                        });

                                    } else {
                                        // Something went wrong. Look at the ParseException to see what's up.

                                        progressWheel.setVisibility(View.GONE);
                                        mSendBtn.setClickable(true);
                                        snackBar(false);

                                    }
                                }
                            });
                        }
                    }else{
                        mEmail.setError("Entrer votre email!");
                        progressWheel.setVisibility(View.GONE);
                        mSendBtn.setClickable(true);
                    }
                }
            }
        });

    }

    public void snackBar(boolean statu){
        if(statu == true) {
            snackbar = new SnackBar(this, "Adresse Email invalide", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.setBackgroundSnackBar(Color.RED);
        }else{
            snackbar = new SnackBar(this, "Erreur Réseau ! Verifiez votre connexion internet.", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });

        }

        snackbar.show();
    }

    public void emailError(){
        snackbar = new SnackBar(this, "Email invalide", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.setBackgroundSnackBar(Color.RED);
        snackbar.show();
    }

}
