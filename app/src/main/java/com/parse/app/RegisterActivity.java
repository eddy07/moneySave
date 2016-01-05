package com.parse.app;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.app.model.Compte;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.Date;


public class RegisterActivity extends ActionBarActivity {

    private String phoneNumber;
    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPhone;
    private Button mSign;
    private Context context;
    public static int TYPE_NOT_CONNECTED = 0;
    private boolean status = false;
    private SnackBar snackBar;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //setTitle("Sign Up");
        getSupportActionBar().hide();
        context = this;
        mName = (EditText)findViewById(R.id.nom);
        mEmail = (EditText)findViewById(R.id.email);
        mPassword = (EditText)findViewById(R.id.password);
        mPhone = (EditText)findViewById(R.id.phone);
        mSign = (Button)findViewById(R.id.registerBtn);
        mSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMain();
            }
        });
    }


    public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_main, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent homeIntent= new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_done) {
            startMain();
        }

        return super.onOptionsItemSelected(item);
    }

    public void snackBar(boolean status, String context){
        if (status==true && context==null) {
            snackBar = new SnackBar(this, getResources().getString(R.string.no_internet), "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else if(status==false && context=="empty_field") {
            snackBar = new SnackBar(this, "Erreur : Champs incomplets !", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else if(status==false && context=="signup_error"){
            snackBar = new SnackBar(this, "Error : " + context, "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else{
            snackBar = new SnackBar(this, getResources().getString(R.string.no_internet), "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }
    }

    public void startMain(){
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            //Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            snackBar(false, "noInternet");
        } else {
            String name = mName.getText().toString();
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            String phone = mPhone.getText().toString();
            String date = DateFormat.getDateTimeInstance().format(new Date());
            final ParseUser user = new ParseUser();
            if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !password.isEmpty()) {
                initprogress();
                user.setUsername(name);
                user.setEmail(email);
                user.put("solde", 100000);
                user.setPassword(password);
                user.put("phoneNumber", phone);
                user.put("date_creation", date);
                user.put("date_update", date);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            Compte compteUser = new Compte();
                            compteUser.setUserId(user.getObjectId());
                            compteUser.setSolde(100000);
                            compteUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("Create user account", "success");
                                    } else {
                                        Log.d("Create user account", "Fail with error : " + e.getMessage());
                                    }
                                }
                            });
                            ParsePush.subscribeInBackground("idjangui" + user.getObjectId(), new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("com.parse.push", "user " + user.getUsername() + " successfully subscribed to  user push notification.");
                                    } else {
                                        Log.e("com.parse.push", "failed to subscribe for push error = " + e);
                                    }
                                }
                            });
                            ParsePush.subscribeInBackground("idjangui", new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("com.parse.push", "user " + user.getUsername() + " successfully subscribed to  idjangui push notification.");
                                    } else {
                                        Log.e("com.parse.push", "failed to subscribe for push idjangui error = " + e);
                                    }
                                }
                            });
                            progressDialog.dismiss();
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
                            progressDialog.dismiss();
                            //Toast.makeText(context, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            snackBar(false, "signup_error");

                        }
                    }
                });


            } else {
               //Toast.makeText(context,"Champs incomplets",Toast.LENGTH_LONG).show();
                snackBar(false, "empty_field");
            }
        }
    }
}
