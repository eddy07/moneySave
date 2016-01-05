package com.parse.app;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.app.model.DemandeSoutient;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.Date;


public class DemandeSoutientActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private EditText mMotif;
    private Context context;
    public static int TYPE_NOT_CONNECTED = 0;
    private String tontineId;
    ParseUser user;
    DemandeSoutient demandeSoutient;
    private boolean status = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demande_soutient);
        mMotif = (EditText)findViewById(R.id.motif);
        context = this;
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        setTitle("Demande de soutient");
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this, SessionActivity.class);
        i.putExtra("TONTINE_ID",tontineId);
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(
                            this,
                            R.anim.anim_left_right,
                            R.anim.anim_right_left).toBundle();
            startActivity(i, bndlanimation);
            finish();
        } else {
            startActivity(i);
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demande_soutient, menu);
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
        }else if (id == R.id.send) {
            sendDemandeSoutient();
            return true;
        }else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void sendDemandeSoutient(){
        if(mMotif.getText().toString().isEmpty()){
            mMotif.setError("Champ incorrect");
        }else if(mMotif.getText().toString().length()<10 ){
            mMotif.setText("Entrer plus de 10 caracteres");
        }else {
            if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            } else {
                user = ParseUser.getCurrentUser();
                String date = DateFormat.getDateTimeInstance().format(new Date());
                String motif = mMotif.getText().toString();
                demandeSoutient = new DemandeSoutient();
                demandeSoutient.setAuteur(user);
                demandeSoutient.setDateCreation(date);
                demandeSoutient.setMotif(motif);
                demandeSoutient.pinInBackground();
                demandeSoutient.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            status = true;
                           Toast.makeText(context,"Message envoyé " + demandeSoutient.getMotif(), Toast.LENGTH_SHORT).show();
                        }else{
                            status = false;
                            Toast.makeText(context,"Erreur! message non envoyé", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if(status == true) {
                    ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Groupe");
                    tontineQuery.whereEqualTo("objectId", tontineId);
                    tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
                        @Override
                        public void done(Tontine tontine, ParseException e) {
                            if (tontine == null) {
                                Log.d("tontine", "The getFirst request failed.");
                            } else {
                                Log.d("tontine", "Retrieved the object.");
                                ParseRelation<ParseObject> relation = demandeSoutient.getRelation("Concerne");
                                relation.add(tontine);
                                demandeSoutient.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d("relation", "Linked demandesoutient to tontine.");
                                            ParsePush push = new ParsePush();
                                            push.setChannel(tontineId);
                                            push.setMessage(user + " : " + demandeSoutient.getMotif());
                                            push.sendInBackground(new SendCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Log.d("demande push", "send");
                                                    } else {
                                                        Log.d("demande push", "not send");
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d("relation", "Fail to linked demandesoutient to tontine.");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            }
        }

    }
}
