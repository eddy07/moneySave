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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.app.model.Membre;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AjoutMembre extends ActionBarActivity {

    private AutoCompleteTextView mMembre;
    private List<String> membres = new ArrayList<String>();
    private String tontineId;
    public static final int TYPE_NOT_CONNECTED= 0;
    private ProgressDialog progressDialog;
    private Tontine thistontine;
    private ParseUser thisuser;
    private String date;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_membre);
        context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ajouter un membre");
        mMembre = (AutoCompleteTextView) findViewById(R.id.membre);
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        thisuser = ParseUser.getCurrentUser();
        date = DateFormat.getDateTimeInstance().format(new Date());

        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery(Tontine.class);
            tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if (e == null) {
                        /*thistontine = tontine;
                        ParseQuery<Membre> membreQuery = ParseQuery.getQuery(Membre.class);
                        membreQuery.whereEqualTo("tontine", tontine);
                        membreQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(List<Membre> membreList, ParseException e) {
                                if ((e == null) && (membreList.size() >= 0)) {
                                    ParseQuery<ParseUser> userParseQuery = ParseQuery.getQuery(ParseUser.class);
                                    userParseQuery.whereNotContainedIn("objectId", membreList);
                                    userParseQuery.findInBackground(new FindCallback<ParseUser>() {
                                        @Override
                                        public void done(List<ParseUser> users, ParseException e) {
                                            if (e == null) {
                                                for (ParseUser user : users) {
                                                    //membreList.add(membre.getAdherant());
                                                    membres.add(user.getUsername());
                                                }
                                            } else {
                                                Log.d("users", "empty");
                                            }
                                        }
                                    });
                                }
                            }
                        });*/
                        thistontine = tontine;
                        ParseQuery<Membre> membreQuery = ParseQuery.getQuery(Membre.class);
                        membreQuery.whereNotEqualTo("tontine", tontine);
                        membreQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(List<Membre> membreList, ParseException e) {
                                if ((e == null) && (membreList.size() >= 0)) {
                                    for(Membre membre : membreList){
                                        ParseUser user = membre.getAdherant();
                                        user.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                                            @Override
                                            public void done(ParseUser parseObject, ParseException e) {

                                                membres.add(parseObject.getUsername());
                                            }
                                        });

                                    }

                                }
                            }
                        });
                    }
                }
            });
        }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, membres);
            mMembre.setAdapter(adapter);
            mMembre.setThreshold(1);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ajout_membre, menu);
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
            ajoutmembreService();
            return true;
        }else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this, ListMembreActivity.class);
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
            Toast.makeText(this,"Oups... internet error. please retry!", Toast.LENGTH_LONG).show();
        }
    }
    public void initprogress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("En cours de création ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void ajoutmembreService(){
        final String user = mMembre.getText().toString();
        final Membre membre = new Membre();
        if(!user.isEmpty()) {
            if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            } else {
                initprogress();
                ParseQuery<ParseUser> userParseQuery = ParseQuery.getQuery(ParseUser.class);
                userParseQuery.whereEqualTo("username", user);
                userParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(final ParseUser parseUser, ParseException e) {
                        if (e == null) {

                            membre.setDateInscription(date);
                            membre.setAdherant(parseUser);
                            membre.setResponsable(thisuser);
                            membre.setTontine(thistontine);
                            membre.pinInBackground();
                            membre.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        thistontine.increment("nbMembre");
                                        thistontine.saveInBackground();
                                        ParsePush push = new ParsePush();
                                        push.setChannel("idjangui"+parseUser.getObjectId());
                                        push.setMessage(thisuser.getUsername() + " vous a ajouté à sa tontine : " + thistontine.getNom());
                                        push.sendInBackground(new SendCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e==null){
                                                    Log.d("push","user is aweard!");
                                                }else{
                                                    Log.d("push","error " + e.getMessage());
                                                }
                                            }
                                        });
                                        progressDialog.dismiss();
                                        onBackPressed();
                                    } else {
                                        Log.d("Save Membre", "Fail to add new membre");
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Erreur lors de l'ajout", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        } else {
                            Log.d("Save adherant", "Fail");
                            progressDialog.dismiss();
                            Toast.makeText(context, "Erreur lors de l'ajout " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

        }else{
            Toast.makeText(this,"Entrer un membre",Toast.LENGTH_LONG).show();
        }
    }
}
