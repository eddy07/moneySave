package com.parse.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.model.Annonce;
import com.parse.app.model.Membre;
import com.parse.app.model.Tontine;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.text.DateFormat;
import java.util.Date;


public class InscriptionActivity extends ActionBarActivity {

    private String tontineId;
    private String annonceId;
    private String auteurId;
    private ParseUser thisuser, auteur;
    private TextView nom, prenom, age, profession;
    private Button accept, decline;
    private ProgressWheel progressWheel;
    private String date;
    private SnackBar snackBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Demande d'inscription");
        setContentView(R.layout.activity_inscription);
        date = DateFormat.getDateTimeInstance().format(new Date());
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        annonceId = getIntent().getExtras().getString("ANNONCE_ID");
        auteurId= getIntent().getExtras().getString("AUTEUR_ID");
        thisuser = ParseUser.getCurrentUser();
        nom = (TextView)findViewById(R.id.nom);
        prenom = (TextView)findViewById(R.id.prenom);
        age = (TextView)findViewById(R.id.age);
        prenom = (TextView)findViewById(R.id.profession);
        setFont(nom,prenom,age,profession);
        accept = (Button)findViewById(R.id.acceptBtn);
        decline = (Button)findViewById(R.id.declineBtn);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);
        ParseQuery<ParseUser> parseUserParseQuery = ParseQuery.getQuery(ParseUser.class);
        parseUserParseQuery.getInBackground(auteurId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(e==null && parseUser!=null){
                    nom.setText(parseUser.getString("nom"));
                    prenom.setText(parseUser.getString("prenom"));
                    age.setText(parseUser.getString("age"));
                    profession.setText(parseUser.getString("profession"));
                }
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept.setClickable(false);
                decline.setClickable(false);
                progressWheel.setVisibility(View.VISIBLE);
                ParseQuery<Annonce> annonceParseQuery = ParseQuery.getQuery(Annonce.class);
                annonceParseQuery.getInBackground(annonceId, new GetCallback<Annonce>() {
                    @Override
                    public void done(final Annonce annonce, ParseException e) {
                        if(e==null && annonce!=null){
                            annonce.fetchIfNeededInBackground(new GetCallback<Annonce>() {
                                @Override
                                public void done(Annonce an, ParseException e) {
                                    an.setAccept(true);
                                    an.setStatu("Read");
                                    an.saveInBackground();
                                    final Membre membre = new Membre();
                                    membre.setAdherant(annonce.getAuteur());
                                    membre.setDateInscription(date);
                                    membre.setResponsable(thisuser);
                                    ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                                    tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                                        @Override
                                        public void done(Tontine tontine, ParseException e) {
                                            if (e == null && tontine != null) {
                                                membre.setTontine(tontine);
                                                membre.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            isError("no");
                                                        } else {
                                                            isError("yes");
                                                            accept.setClickable(true);
                                                            decline.setClickable(true);
                                                        }
                                                        progressWheel.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept.setClickable(false);
                decline.setClickable(false);
                progressWheel.setVisibility(View.VISIBLE);
                ParseQuery<Annonce> annonceParseQuery = ParseQuery.getQuery(Annonce.class);
                annonceParseQuery.getInBackground(annonceId,new GetCallback<Annonce>() {
                    @Override
                    public void done(Annonce annonce, ParseException e) {
                        if(e==null && annonce!=null){
                            annonce.fetchIfNeededInBackground(new GetCallback<Annonce>() {
                                @Override
                                public void done(Annonce an, ParseException e) {
                                    an.setAccept(false);
                                    an.setStatu("Read");
                                    an.saveInBackground();
                                    progressWheel.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    public void isError(String statu) {
    if(statu == "yes") {
        snackBar = new SnackBar(this, "Une erreur est survenue. Veillez reesayer!", "Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
    }else if(statu == "noInternet"){
        snackBar = new SnackBar(this, getResources().getString(R.string.no_internet), "Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
    }else if(statu == "no"){
        snackBar = new SnackBar(this, "Un nouveau membre ajouté.", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
    }
        snackBar.show();
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this, AnnonceActivity.class);
        if(tontineId != null) {
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
            isError("noInternet");
        }
    }

    public void setFont(TextView tv1,TextView tv2,TextView tv3,TextView tv4) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface tf3 = Typeface.createFromAsset(tv3.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface tf4 = Typeface.createFromAsset(tv4.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv1.setTypeface(tf1);tv2.setTypeface(tf2);tv3.setTypeface(tf3);tv4.setTypeface(tf4);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inscription, menu);
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
}
