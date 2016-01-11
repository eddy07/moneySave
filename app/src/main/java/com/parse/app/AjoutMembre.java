package com.parse.app;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
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
import com.parse.app.utilities.UIUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;


public class AjoutMembre extends ActionBarActivity {

    private AutoCompleteTextView mMembre;
    private List<String> membres = new ArrayList<String>();
    private String tontineId;
    public static final int TYPE_NOT_CONNECTED= 0;
    private Tontine thistontine;
    private SnackBar snackBar;
    private ParseUser thisuser;
    private String date;
    private String nom;
    private Context context;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_membre);
        context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = UIUtils.getProgressDialog(this, R.layout.progress_dialog_adding);
        getSupportActionBar().setTitle("Ajouter un membre");
        mMembre = (AutoCompleteTextView) findViewById(R.id.membre);
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        nom = getIntent().getExtras().getString("NOM");
        thisuser = ParseUser.getCurrentUser();
        date = DateFormat.getDateTimeInstance().format(new Date());

        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            noInternet();
        } else {
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery(Tontine.class);
            tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if (e == null) {
                        thistontine = tontine;
                        ParseQuery<Membre> membreQuery = (new Membre()).getQuery();
                        membreQuery.whereEqualTo("tontine", tontine);
                        membreQuery.fromLocalDatastore();
                        membreQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(List<Membre> membreList, ParseException e) {
                                if (e == null) {
                                    String[] idList = new String[membreList.size()];
                                    for (int i = 0; i < membreList.size(); i++) {
                                        idList[i] = membreList.get(i).getAdherant().getObjectId();
                                    }
                                    ParseQuery<ParseUser> parseUserParseQuery = ParseQuery.getQuery(ParseUser.class);
                                    parseUserParseQuery.fromLocalDatastore();
                                    parseUserParseQuery.whereNotContainedIn("objectId", Arrays.asList(idList));
                                    parseUserParseQuery.findInBackground(new FindCallback<ParseUser>() {
                                        @Override
                                        public void done(List<ParseUser> parseUsers, ParseException e) {
                                            if (e == null) {
                                                for (ParseUser user : parseUsers) {
                                                    membres.add(user.getUsername());
                                                }
                                            }
                                        }
                                    });

                                }
                            }

                        });
                    }
                }
            });
        }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, membres);
            mMembre.setAdapter(adapter);
            //mMembre.setThreshold(1);


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
        Intent i = new Intent(this, MainTontineActivity.class);
        if(tontineId!=null) {
            i.putExtra("TONTINE_ID", tontineId);
            i.putExtra("NOM", nom);
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
    public void noInternet(){
        snackBar = new SnackBar(this,getResources().getString(R.string.no_internet), "Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }
    public void ajoutmembreService(){
        final String user = mMembre.getText().toString();
        final Membre membre = new Membre();
        if(!user.isEmpty()) {
            if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
                noInternet();
            } else {
                alertDialog.show();
                ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
                tontineParseQuery.getInBackground(tontineId,new GetCallback<Tontine>() {
                    @Override
                    public void done(final Tontine tontine, ParseException e) {
                        if(e==null){
                            ParseQuery<ParseUser> parseUserParseQuery = ParseQuery.getQuery(ParseUser.class);
                            parseUserParseQuery.whereEqualTo("username",user);
                            parseUserParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                                @Override
                                public void done(final ParseUser parseUser, ParseException e) {
                                    if(e==null){
                                        ParseQuery<Membre> membreParseQuery = (new Membre()).getQuery();
                                        membreParseQuery.whereEqualTo("tontine",tontine);
                                        membreParseQuery.whereEqualTo("adherant",parseUser);
                                        membreParseQuery.getFirstInBackground(new GetCallback<Membre>() {
                                            @Override
                                            public void done(Membre membre, ParseException e) {
                                                if(e==null){
                                                    alertDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Membre existant!",Toast.LENGTH_LONG).show();
                                                }else{
                                                    membre = new Membre();
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
                                                                /*ParsePush push = new ParsePush();
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
                                                                });*/
                                                                alertDialog.dismiss();
                                                                Toast.makeText(getApplicationContext(),"Un membre ajouté !",Toast.LENGTH_LONG).show();
                                                                onBackPressed();
                                                            } else {
                                                                Log.d("Save Membre", "Fail to add new membre");
                                                                alertDialog.dismiss();
                                                                Toast.makeText(context, "Erreur lors de l'ajout", Toast.LENGTH_LONG).show();

                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }else{
                                        alertDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Membre inexistant !",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    }
                });
            }

        }else{
            Toast.makeText(this,"Entrer un membre",Toast.LENGTH_LONG).show();
        }
    }
}
