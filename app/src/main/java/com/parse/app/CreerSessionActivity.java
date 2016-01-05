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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.model.Membre;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CreerSessionActivity extends ActionBarActivity {

    private AutoCompleteTextView mBeneficiaire;
    private EditText mMontantCotisation;
    private EditText mAmande;
    private String tontineId;
    private Tontine thistontine;
    private ParseUser president;
    private ParseUser beneficiaireObj = new ParseUser();
    private Context context;
    private List<String> beneficiaires = new ArrayList<String>();
    public static final int TYPE_NOT_CONNECTED = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_session);
        context = this;
        getSupportActionBar().setTitle("Créer une Session");
        mBeneficiaire = (AutoCompleteTextView)findViewById(R.id.beneficiaire);
        mMontantCotisation = (EditText)findViewById(R.id.montantCotisation);
        mAmande = (EditText)findViewById(R.id.montantAmande);
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        //Toast.makeText(this, "totnineid = " + tontineId, Toast.LENGTH_LONG).show();
        president = ParseUser.getCurrentUser();
        thistontine = new Tontine();
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
        } else {

            ParseQuery<Tontine> groupeParseQuery = ParseQuery.getQuery(Tontine.class);
            groupeParseQuery.getInBackground(tontineId,new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    thistontine = tontine;
                    Log.d("Tontine","id = " + tontine.getObjectId());
                    ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
                    membreParseQuery.whereEqualTo("tontine", tontine);
                    membreParseQuery.findInBackground(new FindCallback<Membre>() {
                        @Override
                        public void done(List<Membre> membres, ParseException e) {
                            if (e == null) {
                                for (Membre membre : membres) {
                                    //membreList.add(membre.getAdherant());
                                    ParseUser adherant = new ParseUser();
                                    adherant = membre.getAdherant();
                                    adherant.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                                        @Override
                                        public void done(ParseUser parseObject, ParseException e) {
                                            beneficiaires.add(parseObject.getUsername());
                                        }
                                    });

                                }
                            } else {

                            }
                        }
                    });
                }
            });
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, beneficiaires);
            mBeneficiaire.setAdapter(adapter);
            mBeneficiaire.setThreshold(1);

        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this, SessionActivity.class);
        if(tontineId!=null) {
            i.putExtra("TONTINE_ID", tontineId);
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
            Toast.makeText(this,"Oups... internet error, please try egain!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_creer_session, menu);
        return true;
    }

    public void initprogress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("En cours de création ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
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
        }else if(id == R.id.action_done){
            if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            } else {
                createSessionService();
                return true;
            }
        }else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createSessionService() {
        String beneficiaire = mBeneficiaire.getText().toString();
        //Toast.makeText(context,"ben = " + beneficiaire,Toast.LENGTH_LONG).show();
        Integer amande = Integer.parseInt(mAmande.getText().toString());
        Integer montant = Integer.parseInt(mMontantCotisation.getText().toString());
        String date = DateFormat.getDateTimeInstance().format(new Date());
        String statu = "ouverte";
        Integer montantTotal = 0;
        Session session = new Session();
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
        } else {
            initprogress();
            ParseQuery<ParseUser> parseUserParseQuery = ParseQuery.getQuery(ParseUser.class);
            parseUserParseQuery.whereEqualTo("username", beneficiaire);
            parseUserParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        beneficiaireObj = parseUser;
                        Log.d("parseUser", "id = " + beneficiaireObj.getObjectId());
                        //Toast.makeText(context,"bene = " + beneficiaireObj.getUsername(),Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("parseUser", "not found");
                    }
                }
            });

                session.setTontine(thistontine);
                session.setAmande(amande);
                session.setBeneficiaire(beneficiaireObj);
                session.setDateCreation(date);
                session.setDateDebut(date);
                session.setDateFin(date);
                session.setMontantCotisation(montant);
                session.setStatu(statu);
                session.setPresident(president);
                session.setMontantTotal(montantTotal);
                session.pinInBackground();
                session.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ParseQuery pushQuery = ParseInstallation.getQuery();
                            pushQuery.whereNotEqualTo("user",president);
                            ParsePush push = new ParsePush();
                            push.setQuery(pushQuery);
                            push.setChannel("idjangui" + tontineId);
                            push.setMessage("Session ouverte pour la tontine : " + thistontine.getNom());
                            push.sendInBackground();
                            progressDialog.dismiss();
                            Log.d("session", "created");
                            Intent i = new Intent(context, SessionActivity.class);
                            if(tontineId!=null) {
                                i.putExtra("TONTINE_ID", tontineId);
                                startActivity(i);
                            }else{
                                Toast.makeText(context,"Oups... internet error, please try error!",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //progressDialog.dismiss();
                            //Toast.makeText(context,"Erreur de creation " + e.getMessage(),Toast.LENGTH_LONG).show();
                            Log.d("session", "Error while saving error = " + e.getMessage());
                            createSessionService();

                        }
                    }
                });
            }
        }

}
