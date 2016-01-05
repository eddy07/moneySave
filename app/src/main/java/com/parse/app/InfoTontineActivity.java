package com.parse.app;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.melnykov.fab.FloatingActionButton;
import com.gc.materialdesign.widgets.SnackBar;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.asynctask.SendDemandeInscriptionAsyncTask;
import com.parse.app.model.Tontine;
import com.parse.app.model.DemandeInscription;
import com.parse.app.utilities.NetworkUtil;
import com.parse.app.utilities.UIUtils;

import java.text.DateFormat;
import java.util.Date;


public class InfoTontineActivity extends ActionBarActivity {

    private TextView mNom;
    private TextView mType;
    private TextView mAmande;
    private TextView mJour;
    private TextView mDescription;
    private TextView lNom;
    private TextView lType;
    private TextView lAmande;
    private TextView lJour;
    private TextView lDescription;
    private TextView btnInvite;
    private String presidentChannel;
    private String tontineId;
    private String nom;
    private String type;
    private String amande;
    private String jour;
    private String description;
    private String date;
    private String nomPresident;
    private String emailPresident;
    private String presidentId;
    private String invitationChannel;
    private ParseUser user;
    public static int TYPE_NOT_CONNECTED = 0;
    private DemandeInscription demandeInscription;
    private Tontine tontine;
    private ParseUser responsable;
    private String dateNow;
    private SnackBar snackBar;
    private Context context;
    private AlertDialog alertDialog;
    private SendDemandeInscriptionAsyncTask sendDemandeInscriptionAsyncTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tontine);
        context = this;
        getSupportActionBar().setTitle("Info de la tontine");
        alertDialog = UIUtils.getProgressDialog(this, R.layout.progress_dialog_invite);
        dateNow = DateFormat.getDateTimeInstance().format(new Date());
        btnInvite = (TextView)findViewById(R.id.btnInvite);
        user = ParseUser.getCurrentUser();
        mNom = (TextView)findViewById(R.id.nomtext);
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        mType = (TextView)findViewById(R.id.typetext);
        mAmande = (TextView)findViewById(R.id.amandetext);
        mJour = (TextView)findViewById(R.id.jour_tontinetext);
        mDescription = (TextView)findViewById(R.id.descriptiontext);
        lNom = (TextView)findViewById(R.id.labelNom);
        lType = (TextView)findViewById(R.id.labelType);
        lAmande = (TextView)findViewById(R.id.labelAmande);
        lJour = (TextView)findViewById(R.id.labelJour);
        lDescription = (TextView)findViewById(R.id.labelDescription);
        setFont(mNom);
        setFont(mType);
        setFont(mDescription);
        setFont(mAmande);
        setFont(mJour);
        setFontTitle(lNom);
        setFontTitle(lType);
        setFontTitle(lDescription);
        setFontTitle(lAmande);
        setFontTitle(lJour);
        nom = getIntent().getExtras().getString("NOM");
        type = getIntent().getExtras().getString("TYPE");
        amande = getIntent().getExtras().getString("AMANDE");
        jour = getIntent().getExtras().getString("JOUR");
        description = getIntent().getExtras().getString("DESCRIPTION");
        date = getIntent().getExtras().getString("DATE");
        nomPresident = getIntent().getExtras().getString("PRESIDENT_NOM");
        presidentId = getIntent().getExtras().getString("PRESIDENT_ID");
        emailPresident = getIntent().getExtras().getString("PRESIDENT_EMAIL");
        presidentChannel = "idjangui" + tontineId + presidentId;
        //Toast.makeText(context,"tontineid = " + tontineId + " presi = " + presidentId + " presichanel = " + presidentChannel,Toast.LENGTH_LONG).show();
        user = ParseUser.getCurrentUser();
        mNom.setText(nom);
        mType.setText(type);
        mAmande.setText(amande);
        mJour.setText(jour);
        mDescription.setText(description);
        if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
            //Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
            snackBar(false, "noInternet");
        } else {
            ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
            tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if (e == null) {

                        ParseQuery<DemandeInscription> invitationChannel = ParseQuery.getQuery(DemandeInscription.class);
                        invitationChannel.whereEqualTo("statu", "en attente");
                        invitationChannel.whereEqualTo("demandeur", user);
                        invitationChannel.whereEqualTo("tontine", tontine);
                        invitationChannel.getFirstInBackground(new GetCallback<DemandeInscription>() {
                            @Override
                            public void done(DemandeInscription demandeInscription, ParseException e) {
                                if (e == null) {
                                    btnInvite.setText("Demande envoyé !");
                                    btnInvite.setBackgroundResource(R.drawable.bg_invite_send);
                                    btnInvite.setClickable(false);
                                } else {
                                    Log.d("Invitation", "Not found with error : " + e.getMessage());
                                }
                            }
                        });

                    } else {
                        Log.d("Tontine", "Not found with error : " + e.getMessage());
                    }
                }
            });
        }
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
                    //Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    snackBar(false, "noInternet");
                } else {
                    //Toast.makeText(context, "tontineId = " + tontineId, Toast.LENGTH_SHORT).show();
                    alertDialog.show();
                    demandeInscription = new DemandeInscription();
                    ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
                    tontineQuery.whereEqualTo("objectId", tontineId);
                    tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
                        @Override
                        public void done(final Tontine tontine, ParseException e) {
                            if ((e == null) && (tontine != null)) {
                                responsable = tontine.getPresident();
                                demandeInscription.setTontine(tontine);
                                demandeInscription.setDemandeur(user);
                                demandeInscription.setStatu("en attente");
                                demandeInscription.setRead(false);
                                demandeInscription.setDateDemande(dateNow);
                                demandeInscription.setResponsable(responsable);
                                demandeInscription.pinInBackground();
                                demandeInscription.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            //Toast.makeText(context, "Demande envoyée", Toast.LENGTH_SHORT).show();
                                            //Log.d("Invitation", "envoyé");
                                            invitationChannel = "idjangui" + demandeInscription.getObjectId();
                                            String alert = user.getUsername() + "Souhaite rejoindre votre Tontine : " + tontine.getNom();
                                            String channel = presidentChannel;
                                            String title = "Demande d'inscription";
                                            String userId = user.getObjectId();
                                            //sendPushNotification(userId, channel, alert, title, btnInvite, invitationChannel);
                                            alertDialog.hide();
                                            btnInvite.setText("Demande envoyé");
                                            btnInvite.setBackgroundResource(R.drawable.bg_invite_send);
                                            btnInvite.setClickable(false);
                                        } else {
                                            //Toast.makeText(context, "Erreur lors de l'envoie", Toast.LENGTH_SHORT).show();
                                            snackBar(false, null);
                                            Log.d("Invitation", "Erreur lors de l'envoie");
                                            alertDialog.hide();
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

    public void snackBar(boolean statu, String context){
        if((statu == true) && (context == null)) {
            snackBar = new SnackBar(this, "Demande envoyé !", "Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
            Log.d("Invitation", "envoyé");
        }else if ((statu == false) && (context == null)){
            snackBar = new SnackBar(this, "Erreur lors de l'envoie de la requete !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else{
            snackBar = new SnackBar(this, "Erreur réseau !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }

        snackBar.show();
    }
    public void sendPushNotification(String userId, String channel, String alert, String title, TextView btnInvite, String invitationChannel){
        sendDemandeInscriptionAsyncTask = new SendDemandeInscriptionAsyncTask(userId, channel, title, alert, this, btnInvite, invitationChannel);
        sendDemandeInscriptionAsyncTask.execute((Void) null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info_tontine, menu);
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
    public void setFontTitle(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        tv.setTypeface(tf);
    }
    public void setFont(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv.setTypeface(tf);
    }
}
