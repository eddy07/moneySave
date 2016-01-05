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
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.model.Compte;
import com.parse.app.model.Cotisation;
import com.parse.app.model.Session;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.Date;


public class CotiserActivity extends ActionBarActivity {

    private EditText mPhone;
    private EditText mCode;
    private EditText mMontant;
    private Compte compte;
    private String tontineId;
    private String sessionId;
    private ParseUser payeur;
    private String date;
    private String dateSession;
    private Cotisation cotisation;
    private Context context;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotiser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        dateSession = getIntent().getExtras().getString("DATE");
        date = DateFormat.getDateTimeInstance().format(new Date());
        payeur = ParseUser.getCurrentUser();
        mPhone = (EditText)findViewById(R.id.phone);
        mCode = (EditText)findViewById(R.id.code);
        mMontant = (EditText)findViewById(R.id.montant);
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        sessionId = getIntent().getExtras().getString("SESSION_ID");
        setTitle("Cotiser");

    }

    public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("En cours de traitement ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this, TontineActivity.class);
        if((tontineId!=null)&&(sessionId!=null)&&(dateSession!=null)) {
            i.putExtra("TONTINE_ID", tontineId);
            i.putExtra("SESSION_ID", sessionId);
            i.putExtra("DATE", dateSession);
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(
                                this,
                                R.anim.anim_right_left,
                                R.anim.anim_left_right).toBundle();
                startActivity(i, bndlanimation);
                finish();
            } else {
                startActivity(i);
                finish();
            }
        }else{
            Toast.makeText(this,"Hohoo... internet error. please retry!", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cotiser, menu);
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
        }else if (id == R.id.action_done) {
            payerService();
            return true;
        }else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void payerService(){
        String phone = mPhone.getText().toString();
        Integer code = Integer.parseInt(mCode.getText().toString());
        final Integer montant = Integer.parseInt(mMontant.getText().toString());

        if(!code.toString().isEmpty() && !phone.isEmpty()&& ! montant.toString().isEmpty()){
            if(NetworkUtil.getConnectivityStatus(this)==0) {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();

            }else {
                initprogress();
                ParseQuery<Session> sessionQuery = ParseQuery.getQuery(Session.class);
                sessionQuery.whereEqualTo("statu","ouverte");
                sessionQuery.getInBackground(sessionId,new GetCallback<Session>() {
                    @Override
                    public void done(final Session session, ParseException e) {
                        if (e == null) {
                            //Toast.makeText(context, "enter", Toast.LENGTH_LONG).show();
                            ParseQuery<Cotisation> cotisationParseQuery = ParseQuery.getQuery(Cotisation.class);
                            cotisationParseQuery.whereEqualTo("session", session);
                            cotisationParseQuery.whereEqualTo("beneficiaire", session.getBeneficiaire());
                            cotisationParseQuery.whereEqualTo("adherant", payeur);
                            cotisationParseQuery.getFirstInBackground(new GetCallback<Cotisation>() {
                                @Override
                                public void done(Cotisation c, ParseException e) {
                                    if (e == null) {
                                            Toast.makeText(context, "Vous avez déjà cotisé !", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        } else {
                                        //Toast.makeText(context, "pas encore cotise payeurid = " + payeur.getObjectId(), Toast.LENGTH_LONG).show();

                                        ParseQuery<Compte> compteParseQuery = ParseQuery.getQuery(Compte.class);
                                        compteParseQuery.whereEqualTo("userId",payeur.getObjectId());
                                        compteParseQuery.getFirstInBackground(new GetCallback<Compte>() {
                                            @Override
                                            public void done(Compte compte, ParseException e) {
                                                if (e == null) {
                                                    compte.setSolde(compte.getSolde() - montant);
                                                    compte.pinInBackground();
                                                    compte.saveInBackground();
                                                    //Toast.makeText(context, "payeur updated", Toast.LENGTH_LONG).show();
                                                    Log.d("Payerur","updated");
                                                } else {
                                                    Log.d("Compte", "Not found");
                                                }
                                            }
                                        });

                                            ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
                                            query.getInBackground(session.getBeneficiaire().getObjectId(), new GetCallback<ParseUser>() {
                                                public void done(final ParseUser beneficiaire, ParseException e) {
                                                    if (e == null) {
                                                        ParseQuery<Compte> compteParseQuery = ParseQuery.getQuery(Compte.class);
                                                        compteParseQuery.whereEqualTo("userId", beneficiaire.getObjectId());
                                                        compteParseQuery.getFirstInBackground(new GetCallback<Compte>() {
                                                            @Override
                                                            public void done(Compte compte, ParseException e) {
                                                                if (e == null) {
                                                                    compte.setSolde(compte.getSolde() + montant);
                                                                    compte.pinInBackground();
                                                                    compte.saveInBackground();
                                                                    //Toast.makeText(context, "beneficiaire updated", Toast.LENGTH_LONG).show();
                                                                    Log.d("Beneficiaire","updated");
                                                                    cotisation = new Cotisation();
                                                                    cotisation.setSession(session);
                                                                    cotisation.setDate(date);
                                                                    cotisation.setAdherant(payeur);
                                                                    cotisation.setBeneficiaire(beneficiaire);
                                                                    cotisation.setMontant(montant);
                                                                    cotisation.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            if (e == null) {
                                                                                /*ParseQuery pushQuery = ParseInstallation.getQuery();
                                                                                pushQuery.whereNotEqualTo("user",payeur);
                                                                                ParsePush push = new ParsePush();
                                                                                push.setQuery(pushQuery);
                                                                                push.setChannel("idjangui" + tontineId);
                                                                                push.setMessage(payeur.getUsername() + "vient de cotiser");
                                                                                push.sendInBackground(new SendCallback() {
                                                                                    @Override
                                                                                    public void done(ParseException e) {
                                                                                        if(e==null){
                                                                                            Log.d("push","all user are aweard!");
                                                                                        }else{
                                                                                            Log.d("push","error " + e.getMessage());
                                                                                        }
                                                                                    }
                                                                                });*/
                                                                                progressDialog.dismiss();
                                                                                Log.d("Cotisation", "Effectuer");
                                                                                Toast.makeText(context, "Opération effectuée", Toast.LENGTH_LONG).show();
                                                                                onBackPressed();
                                                                            } else {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(context, "Erreur lors de la cotisation : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                Log.d("Cotisation", "Fail to save with error : " + e.getMessage());
                                                                            }
                                                                        }
                                                                    });

                                                                    //Toast.makeText(context, "beneficiaire updated", Toast.LENGTH_LONG).show();
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    Log.d("Compte beneficiaire", "Not found");
                                                                    Toast.makeText(context, "Erreur", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        progressDialog.dismiss();
                                                        Log.d("Update user", "Error " + e.getMessage());
                                                        Toast.makeText(context, "Erreur", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                        }

                                }
                            });
                        } else {
                            Log.d("Session", "Not found with error : " + e.getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(context, "Pas de session", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }else{
            Toast.makeText(context,"Champs invalides",Toast.LENGTH_LONG).show();
        }
    }
}
