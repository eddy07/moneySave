package com.parse.app;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.app.asynctask.SendAnnonceAsyncTask;
import com.parse.app.model.Annonce;
import com.parse.app.model.Membre;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;


public class CreerAnnonceActivity extends ActionBarActivity {
    private EditText mMessage;
    private Context context;
    public static int TYPE_NOT_CONNECTED = 0;
    private String tontineId;
    private EditText mTitre;
    ParseUser thisuser;
    private ImageButton create;
    private SnackBar snackBar;
    private boolean status = false;
    private Tontine tontine;
    private SendAnnonceAsyncTask sendAnnonceAsyncTask = null;
    private TextView mNom, mFonction;
    private ProgressWheel progressWheel;
    //private List<ParseUser> listeUsers = new ArrayList<ParseUser>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_annonce1);
        getSupportActionBar().hide();
        context = this;
        mTitre = (EditText)findViewById(R.id.titre);
        mMessage = (EditText)findViewById(R.id.message);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);
        mNom = (TextView)findViewById(R.id.nom);
        mFonction = (TextView)findViewById(R.id.fonction);
        create = (ImageButton)findViewById(R.id.create);
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        thisuser = ParseUser.getCurrentUser();
        mNom.setText(thisuser.getString("nom").concat(" ").concat(thisuser.getString("prenom")));
        setFontLabel(mFonction,mNom);
        setFontTitre(mTitre);
        setFontMessage(mMessage);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creerannonce();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_creer_annonce, menu);
        return true;
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
                                R.anim.zoom_exit).toBundle();
                startActivity(i, bndlanimation);
                finish();
            } else {
                startActivity(i);
                finish();
            }
        }else{
            snackBar(false, "noInternet");
        }
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
        }else if(id == R.id.send){
           creerannonce();
        }

        return super.onOptionsItemSelected(item);
    }

    public void creerannonce(){
        final String message = mMessage.getText().toString();
        final String titre = mTitre.getText().toString();
        if(!message.isEmpty() && !titre.isEmpty()){
            if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
                //Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                snackBar(false, "noInternet");
            } else {
                progressWheel.setVisibility(View.VISIBLE);
                getIntent();
                //final List<ParseUser> userList = getAdherant(tontineId);
                //Toast.makeText(context,"user 0"+userList.get(1).getUsername(), Toast.LENGTH_LONG).show();
                final List<ParseUser> users = new ArrayList<ParseUser>();
                ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                    @Override
                    public void done(final Tontine t, ParseException e) {
                        if((e==null) && t != null){

                            ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
                            //membreParseQuery.whereNotEqualTo("adherant", thisuser);
                            membreParseQuery.whereEqualTo("tontine", t);
                            membreParseQuery.findInBackground(new FindCallback<Membre>() {
                                @Override
                                public void done(List<Membre> membres, ParseException e) {
                                    if((e==null) && membres.size()>0){
                                        for(Membre membre : membres){
                                            users.add(membre.getAdherant());
                                        }
                                        if (users.size() > 0) {
                                            for (int i = 0; i < users.size(); i++) {
                                                Annonce annonce = new Annonce();
                                                annonce.setAuteur(thisuser);
                                                annonce.setTitre(titre);
                                                annonce.setAdherant(users.get(i));
                                                annonce.setTontine(t);
                                                annonce.setMessage(message);
                                                annonce.setType("annonce");
                                                annonce.saveInBackground();
                                            }
                                            String userChannel = "idjangui" + tontineId;
                                            String title = "Annonce de la tontine : " + t.getNom();
                                            String alert = message;
                                            String userId = thisuser.getObjectId();
                                            //sendAnnonceToTheTontineMembers(userId, alert, title, userChannel);
                                            progressWheel.setVisibility(View.GONE);
                                            snackBar(true, null);
                                            finish();

                                        }else{
                                            progressWheel.setVisibility(View.GONE);
                                            snackBar(false, null);
                                        }

                                    }
                                }
                            });
                        }
                    }
                });

            }
        }
    }
    public void sendAnnonceToTheTontineMembers(String userId, String alert, String title, String channel){
        sendAnnonceAsyncTask = new SendAnnonceAsyncTask(userId, channel, title, alert, this);
        sendAnnonceAsyncTask.execute((Void)null);
    }

    public void snackBar(boolean statu, String context){
        if((statu == true) && (context == null)) {
            snackBar = new SnackBar(this, "Annonce envoyée !", "Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
            Log.d("Annonce", "envoyé");
        }else if ((statu == false) && (context == null)){
            snackBar = new SnackBar(this, "Erreur lors de l'envoi de l'annonce", "Cancel", new View.OnClickListener() {
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
    public void setFontTitre(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv.setTypeface(tf);
    }
    public void setFontMessage(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
    public void setFontLabel(TextView tv1,TextView tv2) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-RegularItalic.ttf");
        tv1.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-Black.ttf");
        tv2.setTypeface(tf2);
    }
}
