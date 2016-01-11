package com.parse.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.asynctask.SendDemandeInscriptionAsyncTask;
import com.parse.app.model.DemandeInscription;
import com.parse.app.model.Membre;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;
import com.parse.app.utilities.UIUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

//import com.melnykov.fab.FloatingActionButton;


public class MaTontineInfoActivity extends ActionBarActivity {

    private TextView mNom;
    private TextView mType;
    private TextView mAmande;
    private TextView mJour;
    private TextView mDescription;
    private TextView mMontant;
    private TextView mPresident;
    private TextView lNom;
    private TextView lType;
    private TextView lAmande;
    private TextView lMontant;
    private TextView lPresident;
    private TextView lJour;
    private TextView lDescription;
    private String tontineId;
    private String nom;
    private String type;
    private String amande;
    private int montant;
    private String president;
    private MaterialDialog materialDialog;
    private String jour;
    private String description;
    private TextView oui, non;
    private AlertDialog alertDialog, alertDialogAct;
    private boolean isActivated;
    private ParseUser thisuser;
    private LinearLayout linearLayout;
    public static final int TYPE_NOT_CONNECTED = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matontine_info);
        alertDialog = UIUtils.getProgressDialog(this, R.layout.progress_dialog_suppression);
        alertDialogAct = UIUtils.getProgressDialog(this, R.layout.progress_dialog_activation);
        getSupportActionBar().setTitle("Info de la tontine");
        mNom = (TextView) findViewById(R.id.nomtext);
        thisuser = ParseUser.getCurrentUser();
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        mType = (TextView) findViewById(R.id.typetext);
        oui = (TextView)findViewById(R.id.oui);
        non = (TextView)findViewById(R.id.non);
        linearLayout = (LinearLayout)findViewById(R.id.layoutActivate);
        mMontant = (TextView)findViewById(R.id.montanttext);
        mAmande = (TextView) findViewById(R.id.amandetext);
        mPresident = (TextView) findViewById(R.id.presidenttext);
        mJour = (TextView) findViewById(R.id.jour_tontinetext);
        mDescription = (TextView) findViewById(R.id.descriptiontext);
        lNom = (TextView) findViewById(R.id.labelNom);
        lType = (TextView) findViewById(R.id.labelType);
        lAmande = (TextView) findViewById(R.id.labelAmande);
        lPresident = (TextView) findViewById(R.id.labelPresident);
        lJour = (TextView) findViewById(R.id.labelJour);
        lMontant = (TextView)findViewById(R.id.labelMontant);
        lDescription = (TextView) findViewById(R.id.labelDescription);
        nom = getIntent().getExtras().getString("NOM");
        montant = getIntent().getExtras().getInt("MONTANT");
        type = getIntent().getExtras().getString("TYPE");
        amande = getIntent().getExtras().getString("AMANDE");
        jour = getIntent().getExtras().getString("JOUR");
        president = getIntent().getExtras().getString("PRESIDENT");
        isActivated = getIntent().getExtras().getBoolean("ACTIVATED");
        description = getIntent().getExtras().getString("DESCRIPTION");
        if(isActivated ==false && president.equals(thisuser)){
            linearLayout.setVisibility(View.VISIBLE);
        }else{
            linearLayout.setVisibility(View.GONE);
        }
        mNom.setText(nom);
        mMontant.setText(""+montant+" FCFA");
        mPresident.setText(president);
        mType.setText(type);
        mAmande.setText(amande);
        mJour.setText(jour);
        mDescription.setText(description);
        setFont(mNom);
        setFont(mMontant);
        setFont(mType);
        setFont(mDescription);
        setFont(mAmande);
        setFont(mPresident);
        setFont(mJour);
        setFontTitle(lNom);
        setFontTitle(lMontant);
        setFontTitle(lPresident);
        setFontTitle(lType);
        setFontTitle(lDescription);
        setFontTitle(lAmande);
        setFontTitle(lJour);
        non.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.GONE);
            }
        });
        oui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.getConnectivityStatus(getApplicationContext()) == TYPE_NOT_CONNECTED) {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_connected),Toast.LENGTH_LONG).show();
                }else{
                    alertDialogAct.show();
                    ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
                    tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                        @Override
                        public void done(final Tontine tontine, ParseException e) {
                            if(e==null){
                                ParseQuery<Membre> membreParseQuery = (new Membre()).getQuery();
                                membreParseQuery.whereEqualTo("tontine",tontine);
                                membreParseQuery.findInBackground(new FindCallback<Membre>() {
                                    @Override
                                    public void done(List<Membre> membres, ParseException e) {
                                        if(e==null && membres.size()>0){
                                            if(membres.size()>1){
                                                tontine.setIsActivated(true);
                                                tontine.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if(e==null){
                                                            alertDialogAct.dismiss();
                                                            Log.i("Tontine","Activated");
                                                            Toast.makeText(getApplicationContext(),"Tontine Activée !",Toast.LENGTH_LONG).show();

                                                        }else{
                                                            alertDialogAct.dismiss();
                                                            Log.i("Tontine","Fail to activated");
                                                            Toast.makeText(getApplicationContext(),"Erreur lors de l'activation",Toast.LENGTH_LONG).show();

                                                        }
                                                    }
                                                });
                                            }else{
                                                alertDialogAct.dismiss();
                                                Toast.makeText(getApplicationContext(),"Impossible d'activer la tontine: Membres insuffisants",Toast.LENGTH_LONG).show();
                                                Log.i("thisuser","Not a president");
                                            }
                                        }else{
                                            alertDialogAct.dismiss();
                                            Log.i("Membres","Empty");
                                        }
                                    }
                                });

                            }else{
                                alertDialogAct.dismiss();
                                Log.i("Tontine","Not found");
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matontine_info, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this,MainTontineActivity.class);
        i.putExtra("TONTINE_ID",tontineId);
        i.putExtra("NOM",nom);
        //unregisterReceiver(receiver);
        startActivity(i);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }else if(id == R.id.action_delete_tontine){
            supprimerTontine(tontineId);
        }

        return super.onOptionsItemSelected(item);
    }

    public void supprimerTontine(String tontineId){
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this,getResources().getString(R.string.no_connected),Toast.LENGTH_LONG).show();
        }else{
            ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
            tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(final Tontine tontine, ParseException e) {
                    if(e==null){
                        ParseQuery<Membre> membreParseQuery = (new Membre()).getQuery();
                        membreParseQuery.whereEqualTo("tontine",tontine);
                        membreParseQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(final List<Membre> membres, ParseException e) {
                                if(e==null){
                                    ParseQuery<Session> sessionParseQuery = (new Session()).getQuery();
                                    sessionParseQuery.whereEqualTo("tontine",tontine);
                                    sessionParseQuery.findInBackground(new FindCallback<Session>() {
                                        @Override
                                        public void done(List<Session> sessions, ParseException e) {
                                            if(e==null){
                                                Session session = sessions.get(sessions.size()-1);
                                                if(session.getTour() == membres.size()
                                                        && session.getTourIsFinal() == true
                                                        && session.getStatu().equalsIgnoreCase("close")){
                                                    materialDialog = new MaterialDialog(getApplicationContext());
                                                    materialDialog.setCanceledOnTouchOutside(true);
                                                    materialDialog.setTitle(" ");
                                                    materialDialog.setMessage("Supprimer la tontine?");
                                                    materialDialog.setNegativeButton("Oui", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            materialDialog.dismiss();
                                                            alertDialog.show();
                                                            tontine.deleteInBackground(new DeleteCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if(e==null){
                                                                        alertDialog.dismiss();
                                                                        Toast.makeText(getApplicationContext(),"Tontine Supprimé !",Toast.LENGTH_LONG).show();
                                                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                                    }else{
                                                                        alertDialog.dismiss();
                                                                        Log.i("Suppression tontine", "Fail");
                                                                        Toast.makeText(getApplicationContext(),"Erreur lors de la suppression !",Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                    materialDialog.setPositiveButton("Non", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            materialDialog.dismiss();
                                                        }
                                                    });
                                                    materialDialog.show();

                                                }else{
                                                    Toast.makeText(getApplicationContext(),"Impossible de supprimer la tontine!",Toast.LENGTH_LONG).show();
                                                }
                                            }else{
                                                Log.i("sessions","empty");
                                            }
                                        }
                                    });

                                }else{
                                    Log.i("Membres","Not found");
                                }
                            }
                        });
                    }else{
                        Log.i("Tontine","Not found");
                    }
                }
            });
        }
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
