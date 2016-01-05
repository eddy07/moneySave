package com.parse.app;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.inputmethodservice.KeyboardView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.app.model.Compte;
import com.parse.app.utilities.NetworkUtil;
import com.parse.app.utilities.UIUtils;

import java.text.DateFormat;
import java.util.Date;


public class RegisterFinalStep extends ActionBarActivity implements View.OnClickListener{

    private TextView titre, etape;
    private EditText mPseudo, mPsw, mPswVerif;
    private ImageButton signupBtn;
    private String nom, prenom, profession, email, tel;
    private SnackBar snackBar;
    //private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    public static final int TYPE_NOT_CONNECTED = 0;
    private Context context;
    private ParseUser user = new ParseUser();
    private String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_final_step);
        getSupportActionBar().hide();
        context = this;
        alertDialog = UIUtils.getProgressDialog(this, R.layout.progress_dialog_loggin);
        date = DateFormat.getDateTimeInstance().format(new Date());
        titre = (TextView)findViewById(R.id.titre);
        etape = (TextView)findViewById(R.id.etape);
        mPseudo = (EditText)findViewById(R.id.pseudo);
        mPsw = (EditText)findViewById(R.id.password);
        mPswVerif = (EditText)findViewById(R.id.passwordConf);
        signupBtn = (ImageButton)findViewById(R.id.signUp);
        setFontTitle(titre, etape);
        setFontField(mPseudo);
        setFontField(mPsw);
        setFontField(mPswVerif);
        nom = getIntent().getExtras().getString("NOM");
        prenom = getIntent().getExtras().getString("PRENOM");
        profession = getIntent().getExtras().getString("PROFESSION");
        email = getIntent().getExtras().getString("EMAIL");
        tel = getIntent().getExtras().getString("TEL");
        signupBtn.setOnClickListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_final_step, menu);
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

    public void snackBar(boolean status, String context){
        if (status==true && context==null) {
            snackBar = new SnackBar(this, getResources().getString(R.string.no_internet), "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else if(status==false && context=="psw") {
            snackBar = new SnackBar(this, "Mot de passe incoherant !", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
            snackBar.setBackgroundSnackBar(Color.RED);
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


    public void signUpAction(String nom, String prenom, String profession, String email, String tel, String pseudo, String psw) {
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            snackBar(false, "noInternet");
        } else {

                //initprogress();
            alertDialog.show();
                user.setUsername(pseudo);
                user.put("nom", nom);
                user.put("prenom", prenom);
                user.put("profession", profession);
                user.setEmail(email);
                user.put("solde", 100000);
                user.setPassword(psw);
                user.put("phoneNumber", tel);
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
                            //progressDialog.dismiss();
                            alertDialog.dismiss();
                            Intent i = new Intent(context, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            //progressDialog.dismiss();
                            alertDialog.dismiss();
                            snackBar(false, "signup_error");

                        }
                    }
                });

        }
    }

        @Override
    public void onClick(View view) {

        String pseudo, psw, pswConf;
        pseudo = mPseudo.getText().toString();
        psw = mPsw.getText().toString();
        pswConf = mPswVerif.getText().toString();
        if(!pseudo.isEmpty() && !psw.isEmpty() && !pswConf.isEmpty() && !nom.isEmpty() && !prenom.isEmpty() && !profession.isEmpty()
                && !email.isEmpty() && !tel.isEmpty()){
             if(psw.equals(pswConf)){
                 signUpAction(nom, prenom, profession, email, tel, pseudo, psw);
             }else{
                 snackBar(false, "psw");
                 mPswVerif.setError("Mot de passe incorrect");
                 mPsw.setText("");
                 mPswVerif.setText("");
             }
        }else{
            fieldError();
        }

    }
   /* public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Inscription ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setProgress(R.layout.progress_well);
        progressDialog.show();
    }*/
    public void fieldError(){
        snackBar = new SnackBar(this, "Données manquantes ou invalides", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });

        snackBar.show();
    }

    public void setFontField(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
    public void setFontTitle(TextView tv1,TextView tv2) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv1.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-ThinItalic.ttf");
        tv2.setTypeface(tf2);
    }

    @Override
    public void onBackPressed() {
        Intent step2intent= new Intent(this, RegisterStep2.class);
        step2intent.putExtra("EMAIL", email);
        step2intent.putExtra("TEL", tel);
        step2intent.putExtra("NOM", nom);
        step2intent.putExtra("PRENOM", prenom);
        step2intent.putExtra("PROFESSION", profession);
        startActivity(step2intent);
    }

}
