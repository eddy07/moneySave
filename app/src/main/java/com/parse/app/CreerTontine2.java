package com.parse.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.model.Membre;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.ImageIndicatorView;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.Date;


public class CreerTontine2 extends ActionBarActivity implements View.OnClickListener{

    private Spinner spinner;
    private TextView mTitre;
    private TextView mEtape;
    private EditText mDescription;
    private ParseUser thisuser;
    private SnackBar snackBar;
    private String type;
    private String amande;
    private String tel;
    private ProgressDialog progressDialog;
    private String nomTontine;
    private Context context;
    private String date;
    private ImageButton createBtn;
    private EditText montant;
    public static final int TYPE_NOT_CONNECTED = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        thisuser = ParseUser.getCurrentUser();
        context = this;
        date = DateFormat.getDateTimeInstance().format(new Date());
        setContentView(R.layout.activity_creer_tontine3);
        createBtn = (ImageButton)findViewById(R.id.create);
        spinner = (Spinner)findViewById(R.id.jour_tontine);
        montant = (EditText)findViewById(R.id.montant);
        mDescription = (EditText)findViewById(R.id.description);
        mEtape = (TextView)findViewById(R.id.etape);
        mTitre = (TextView)findViewById(R.id.titre);
        nomTontine = getIntent().getExtras().getString("nom");
        amande = getIntent().getExtras().getString("amande");
        tel = getIntent().getExtras().getString("tel");
        type = getIntent().getExtras().getString("type");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter. createFromResource(this,
                R. array. jours_tontines,  R. layout. spinner_item);
        adapter. setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner. setAdapter(adapter);
        setFontText(mTitre, mEtape);
        setFontDes(montant);
        setFontDes(mDescription);
        createBtn.setOnClickListener(this);
    }

    public void setFontDes(EditText tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv.setTypeface(tf);
    }
    public void setFontText(TextView tv1,TextView tv2) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv1.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-ThinItalic.ttf");
        tv2.setTypeface(tf2);
    }


    public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("En cours de création ...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }
    public void createTontine(String tel, String nomTontine, String type, String amande, final String date) {
        String description = mDescription.getText().toString();
        String jourCotisation = String.valueOf(spinner.getSelectedItem());
        String jour = String.valueOf(spinner.getItemAtPosition(0));
        Integer montantTontine = Integer.parseInt(montant.getText().toString());
        if (jour.equalsIgnoreCase(jourCotisation)) {
               erreurjour();
        } else {
            final Tontine tontine = new Tontine();
            if (!nomTontine.isEmpty() && !jourCotisation.isEmpty() && !description.isEmpty() && !type.isEmpty() && !amande.isEmpty()) {
                if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
                    //Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    erreurReseau();
                } else {
                    initprogress();
                    tontine.setNom(nomTontine);
                    tontine.setJourCotisation(jourCotisation);
                    tontine.setDescription(description);
                    tontine.setNbMembre(1);
                    tontine.setTel(tel);
                    tontine.setMontant(montantTontine);
                    tontine.setType(type);
                    tontine.setAmande(amande);
                    tontine.setSessionStatu("close");
                    tontine.setPresident(thisuser);
                    tontine.setCreation_date(date);
                    tontine.pinInBackground();
                    tontine.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("tontine", "created.");
                                String tontineChannel = "idjangui" + tontine.getObjectId();
                                ParsePush.subscribeInBackground(tontineChannel, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            ParseInstallation.getCurrentInstallation().saveInBackground();
                                            Log.d("com.parse.push", "successfully subscribed to the tontine.");
                                            //Toast.makeText(context, "successfully subscribed to the tontine.", Toast.LENGTH_LONG).show();
                                        } else {
                                            Log.e("com.parse.push", "failed to subscribe for push with error = " + e.getMessage());
                                            //Toast.makeText(context, "failed to subscribe for push with error = " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                String presidentChannel = "idjangui" + tontine.getObjectId() + thisuser.getObjectId();
                                ParsePush.subscribeInBackground(presidentChannel, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            ParseInstallation.getCurrentInstallation().saveInBackground();
                                            Log.d("com.parse.push", "successfully subscribed to the presidentChannel.");
                                            //Toast.makeText(context, "successfully subscribed to the presidentChannel.", Toast.LENGTH_LONG).show();
                                        } else {
                                            Log.e("com.parse.push", "failed to subscribe for push presidentChannel with error = " + e.getMessage());
                                            //Toast.makeText(context, "failed to subscribe for push presidentChannel with error = " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                ParseRelation<ParseObject> relation = thisuser.getRelation("AdherantTontine");
                                relation.add(tontine);
                                thisuser.saveInBackground();
                                Membre membre = new Membre();
                                membre.setTontine(tontine);
                                membre.setResponsable(thisuser);
                                membre.setAdherant(thisuser);
                                membre.setDateInscription(date);
                                membre.pinInBackground();
                                membre.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d("membre", "successfully create membre.");

                                            progressDialog.dismiss();
                                            Intent i = new Intent(context, MainActivity.class);
                                            startActivity(i);

                                        } else {
                                            Log.d("membre", "Fail to create membre." + e.getMessage());
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                            } else {
                                progressDialog.dismiss();
                                Log.d("tontine", "Fail to create tontine." + e.getMessage());
                                if(e.getMessage().equals("invalid session token")){
                                    sessionError(e.getMessage());
                                }

                            }

                        }
                    });
                }
            } else {
                error();
            }
        }
    }

    public void sessionError(String message) {

    if (message.equals("invalid session token")) {

        snackBar = new SnackBar(this, "Oups ... Erreur de session, veillez vous reconnectez SVP!", "Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
        snackBar.setIndeterminate(true);

    }else{
        snackBar = new SnackBar(this, "Erreur, veillez réessayer SVP!", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
    }

        snackBar.show();
    }

    public void error(){

        snackBar = new SnackBar(this, "Données manquantes", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });


        snackBar.show();
    }
    public void erreurjour(){

        snackBar = new SnackBar(this, "Selectionner un jour de Tontine !", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });

        snackBar.show();
    }
    public void erreurReseau(){

        snackBar = new SnackBar(this, "Erreur réseau !", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });

        snackBar.show();
    }
    @Override
    public void onClick(View view) {
        createTontine(tel,nomTontine,type,amande,date);
    }
}
