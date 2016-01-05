package com.parse.app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.app.utilities.NetworkUtil;
import com.parse.app.utilities.UIUtils;
import com.parse.app.view.FloatLabeledEditText;
import com.pnikosis.materialishprogress.ProgressWheel;

import me.drakeet.materialdialog.MaterialDialog;


public class LoginActivity extends ActionBarActivity {

    private EditText mPassword;
    private EditText mEmail;
    private Button mSendBtn;
    private Button loginBtn;
    private EditText mUsername;
    private String password;
    private String email;
    private String username;
    private TextView pswRecover,noCompte;
    private LinearLayout bloc;
    private MaterialDialog materialDialog;
    //private ProgressDialog progressDialog;
    private Context context;
    private TextView textLostPsw, appdes;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private SnackBar snackbar;
    private TextView tv;
    private Activity activity;
    public static int TYPE_NOT_CONNECTED = 0;
    private ProgressWheel progressWheel;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        alertDialog = UIUtils.getProgressDialog(this, R.layout.progress_dialog_loggin);
        getSupportActionBar().hide();
        mPassword = (EditText)findViewById(R.id.password);
        mUsername = (EditText)findViewById(R.id.username);
        noCompte= (TextView)findViewById(R.id.noCompte);
        appdes = (TextView)findViewById(R.id.appDes);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);
        bloc = (LinearLayout)findViewById(R.id.bloc);
        scrollView = (ScrollView)findViewById(R.id.scroll);
        tv = (TextView)findViewById(R.id.wel);
        setFont2(tv);
        mEmail = (EditText)findViewById(R.id.email);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        textLostPsw = (TextView)findViewById(R.id.pswforget);
        setFontDes(appdes);
        setFont(mPassword);
        setFont(mUsername);
        textLostPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChangePasswordActivity.class);
                startActivity(i);
            }
        });

        noCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inscription();
            }
        });
        mSendBtn = (Button)findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSendBtn.setClickable(false);
                if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
                    //Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    snackBar(false);
                    mSendBtn.setClickable(true);
                } else {
                    email = mEmail.getText().toString();
                    if (!email.isEmpty()) {
                        if(!email.contains("@")){
                            emailError();
                            mSendBtn.setClickable(true);
                        }else {
                            //passwordReset();
                            progressWheel.setVisibility(View.VISIBLE);
                            ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // An email was successfully sent with reset instructions.
                                        progressWheel.setVisibility(View.GONE);
                                        mSendBtn.setClickable(true);
                                        bloc.setVisibility(View.GONE);
                                        scrollView.setVisibility(View.VISIBLE);
                                       // progressDialog.dismiss();
                                        materialDialog = new MaterialDialog(context);
                                        materialDialog.setMessage("Vous allez reçevoir un mail de confirmation. Connectez-vous à votre " +
                                                "compte de messagerie et réinitialiser votre mot de passe i-djangui :) ");

                                        materialDialog.setPositiveButton("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                materialDialog.dismiss();
                                            }
                                        });

                                        materialDialog.show();

                                    } else {
                                        // Something went wrong. Look at the ParseException to see what's up.
                                        //progressDialog.dismiss();
                                        progressWheel.setVisibility(View.GONE);
                                        //Toast.makeText(context, "Error : Something went wrong", Toast.LENGTH_LONG).show();
                                        mSendBtn.setClickable(true);
                                       // requestError(e.getMessage());
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
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
                    //Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    snackBar(false);
                } else {

                    alertDialog.show();
                    //initprogress();
                    password = mPassword.getText().toString();
                    username = mUsername.getText().toString();
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if ((e == null) && (user != null)) {
                                // Hooray! The user is logged in.
                                user.pinInBackground();
                                //progressDialog.dismiss();
                                alertDialog.dismiss();
                                Intent i = new Intent(context, MainActivity.class);
                                if (android.os.Build.VERSION.SDK_INT >= 16) {
                                    Bundle bndlanimation =
                                            ActivityOptions.makeCustomAnimation(
                                                    context,
                                                    R.anim.anim_left_right,
                                                    R.anim.anim_right_left).toBundle();
                                    startActivity(i, bndlanimation);
                                    finish();
                                } else {
                                    startActivity(i);
                                    finish();
                                }
                            } else {
                                //progressDialog.dismiss();
                                alertDialog.dismiss();
                                // Signup failed. Look at the ParseException to see what happened.
                                Log.d("login", "error");
                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                mEmail.setError("Email invalide");
                                mPassword.setError("username invalide");
                            }
                        }
                    });

                }
            }
        });
    }

    public void inscription(){
        Intent i = new Intent(this, RegisterStep1.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }
    public void snackBar(boolean statu){
        if(statu == true) {
            snackbar = new SnackBar(this, "Adresse Email invalide", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });

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
    public void requestError(String message){
            snackbar = new SnackBar(this, message, "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });


        snackbar.show();
    }
    public void emailError(){
        snackbar = new SnackBar(this, "Email invalide", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }
   /* public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Connexion ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
    public void setFont(EditText tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);

    }
    public void setFontDes(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Thin.ttf");
        tv.setTypeface(tf);

    }
    public void setFont2(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }

}
