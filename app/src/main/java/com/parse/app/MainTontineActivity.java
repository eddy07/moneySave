package com.parse.app;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.adapter.TabsPagerAdapter;
import com.parse.app.adapter.TabsTontinePagerAdapter;
import com.parse.app.asynctask.SendAnnonceAsyncTask;
import com.parse.app.model.Compte;
import com.parse.app.model.Cotisation;
import com.parse.app.model.Membre;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;
import com.parse.app.proxy.IdjanguiProxy;
import com.parse.app.proxy.IdjanguiProxyException;
import com.parse.app.proxy.IdjanguiProxyImpl;
import com.parse.app.utilities.NetworkUtil;
import com.parse.app.utilities.UIUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


public class MainTontineActivity extends ActionBarActivity implements ActionBar.TabListener {

    private ViewPager mViewPager;
    private TabsTontinePagerAdapter tabsPagerAdapter;
    private Context context;
    private String tontineId;
    private ParseUser user;
    private SnackBar snackbar;
    private String date;
    private String dateToDay;
    private String nom;
    private Tontine tontine;
    private boolean started;
    //private Receiver receiver;
    private IntentFilter intentFilter;
    private Cotisation cotisation;
    private ParseUser payeur;
    private SendAnnonceAsyncTask sendAnnonceAsyncTask;
    public static final int TYPE_NOT_CONNECTED = 0;
    private String dayOfWeek;
    private AlertDialog alertDialog;
    private IdjanguiProxyImpl IdjanguiProxy = IdjanguiProxyImpl.getInstance();
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        startService();
        alertDialog = UIUtils.getProgressDialog(this, R.layout.progress_dialog_payement);
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        nom = getIntent().getExtras().getString("NOM");
        date = DateFormat.getDateTimeInstance().format(new Date());
        getSupportActionBar().setTitle(nom);
        user = ParseUser.getCurrentUser();
        if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this,getResources().getString(R.string.no_connected),Toast.LENGTH_LONG).show();
        }else{
            ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
            tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine t, ParseException e) {
                    if(e==null){
                        tontine = t;
                    }else{
                        Log.i("Tontine","Not found");
                    }
                }
            });
        }
        /*receiver = new Receiver();
        intentFilter = new IntentFilter(MyService.EVENT_ACTION);
        registerReceiver(receiver, intentFilter);*/
        createSession(tontineId,date,user);
        final ActionBar actionBar = getSupportActionBar();
        tabsPagerAdapter = new TabsTontinePagerAdapter(getSupportFragmentManager(), nom,tontineId, date);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(tabsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Bundle b = getIntent().getExtras();
        ActionBar.Tab tab = actionBar
                .newTab()
                .setText("Discussions")
                .setTabListener(this);

        ActionBar.Tab tab1 = actionBar
                .newTab()
                .setText("Sessions")
                .setTabListener(this);

        ActionBar.Tab tab2 = actionBar
                .newTab()
                .setText("Membres")
                .setTabListener(this);

        actionBar.addTab(tab, 0, false);
        actionBar.addTab(tab1, 1, false);
        actionBar.addTab(tab2, 2, false);

            if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
                Toast.makeText(this,getResources().getString(R.string.no_connected),Toast.LENGTH_LONG).show();
               //noConnected();
            } else {
                storeUsersInLocalDataStore();
                storeMembreInLocalDataStore();
            }


    }

    /*public class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(GetDateService.EVENT_ACTION)){
                Bundle bundle = intent.getExtras();
                if(bundle!=null){
                    dayOfWeek = bundle.getString("DAY_OF_WEEK");
                    if (NetworkUtil.getConnectivityStatus(context) != TYPE_NOT_CONNECTED) {
                        ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                        tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                            @Override
                            public void done(final Tontine tontine, ParseException e) {
                                final List<ParseUser> listMembre = new ArrayList<ParseUser>();
                                ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
                                membreParseQuery.whereEqualTo("tontine", tontine);
                                membreParseQuery.findInBackground(new FindCallback<Membre>() {
                                    @Override
                                    public void done(List<Membre> membres, ParseException e) {
                                        for(Membre membre:membres){
                                            listMembre.add(membre.getAdherant());
                                        }
                                    }
                                });
                                if(tontine.getPresident().equals(user)){
                                    if(dayOfWeek.equals(tontine.getJourCotisation())){
                                        Session oldSession = new Session();
                                        ParseQuery<Session> sessionParseQuery = ParseQuery.getQuery(Session.class);
                                        sessionParseQuery.whereEqualTo("tontine", tontine);
                                        sessionParseQuery.orderByDescending("tour");
                                        sessionParseQuery.findInBackground(new FindCallback<Session>() {
                                            @Override
                                            public void done(List<Session> sessions, ParseException e) {
                                                if(e==null && sessions.size()>0){
                                                    final Session oldSession;
                                                    oldSession = sessions.get(sessions.size());
                                                    if(tontine.getType().equals("TourDeRole")){
                                                        if (oldSession.getTour() == listMembre.size()){
                                                            oldSession.fetchIfNeededInBackground(new GetCallback<Session>() {
                                                                @Override
                                                                public void done(Session session, ParseException e) {
                                                                    session.setStatu("close");
                                                                    session.setDateFin(date);
                                                                    session.saveInBackground();
                                                                    //ouverture d'une nouvelle session
                                                                    Session newSession = new Session();
                                                                    newSession.setPresident(user);
                                                                    newSession.setStatu("open");
                                                                    newSession.setTontine(tontine);
                                                                    newSession.setBeneficiaire(listMembre.get(1));
                                                                    newSession.setDateCreation(date);
                                                                    newSession.setDateDebut(date);
                                                                    newSession.setTour(1);
                                                                    newSession.saveInBackground();
                                                                    sendNotification(tontine.getPresident().getObjectId(), "idjangui".concat(tontine.getObjectId()),
                                                                            tontine.getNom(), "Nouvelle session ouverte." );

                                                                }
                                                            });
                                                        }else{
                                                            oldSession.fetchIfNeededInBackground(new GetCallback<Session>() {
                                                                @Override
                                                                public void done(Session session, ParseException e) {
                                                                    int tour = session.getTour()+1, lastTour = tour-1;
                                                                    ParseUser beneficiaire = listMembre.get(listMembre.indexOf(session.getBeneficiaire())+1);
                                                                    session.setTour(tour);
                                                                    session.setBeneficiaire(beneficiaire);
                                                                    session.saveInBackground();
                                                                    sendNotification(tontine.getPresident().getObjectId(), "idjangui".concat(tontine.getObjectId()),
                                                                            tontine.getNom(), "Tour "+lastTour+" Fermé et Tour "+tour+" Ouvert. Le bénéficiaire est: "+beneficiaire.getString("nom").concat(" ").concat(beneficiaire.getString("prenom")));
                                                                }
                                                            });
                                                        }

                                                    }else{

                                                    }

                                                }
                                            }
                                        });

                                    }
                                 }
                            }
                        });
                    }

                }
            }else{
                Toast.makeText(getApplicationContext(),"intent = "+intent.getAction(),Toast.LENGTH_LONG).show();
            }
        }
    }*/

    public void sendNotification(String userId, String channel, String title, String alert){
        sendAnnonceAsyncTask = new SendAnnonceAsyncTask(userId,channel,title,alert,this);
        sendAnnonceAsyncTask.execute((Void)null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tontine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.notification) {
            annonceService();
            return true;
        }else if (id == R.id.cotiser) {
            payerService();
            return true;
        }else if (id == R.id.action_settings) {
            parametresService();
        }else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this,MainActivity.class);
        //unregisterReceiver(receiver);
        startActivity(i);
    }
    public void cotiserService(){
        Intent i = new Intent(this, TontinerActivity.class);
        i.putExtra("TONTINE_ID", tontineId);
        startActivity(i);
    }
    public void payerService(){
            if(NetworkUtil.getConnectivityStatus(this)==0) {
                Toast.makeText(this, R.string.no_connected, Toast.LENGTH_SHORT).show();

            }else {
                alertDialog.show();
                payeur = ParseUser.getCurrentUser();
                ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
                tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                    @Override
                    public void done(final Tontine tontine, ParseException e) {
                        if (e == null) {
                            ParseQuery<Session> sessionQuery = (new Session()).getQuery();
                            sessionQuery.whereEqualTo("tontine", tontine);
                            sessionQuery.findInBackground(new FindCallback<Session>() {
                                @Override
                                public void done(List<Session> sessions, ParseException e) {
                                    if (e == null && sessions.size()>0) {
                                        final Session session = sessions.get(sessions.size() - 1);
                                        if (session.getStatu().equalsIgnoreCase("open")) {
                                            Log.i("Session", "is open");
                                            ParseQuery<Cotisation> cotisationParseQuery = ParseQuery.getQuery(Cotisation.class);
                                            cotisationParseQuery.whereEqualTo("session", session);
                                            cotisationParseQuery.whereEqualTo("beneficiaire", session.getBeneficiaire());
                                            cotisationParseQuery.whereEqualTo("adherant", payeur);
                                            cotisationParseQuery.getFirstInBackground(new GetCallback<Cotisation>() {
                                                @Override
                                                public void done(Cotisation c, ParseException e) {
                                                    if (e == null) {
                                                        Toast.makeText(context, "Vous avez déjà cotisé !", Toast.LENGTH_LONG).show();
                                                        alertDialog.dismiss();
                                                    } else {
                                                        ParseQuery<Compte> compteParseQuery = ParseQuery.getQuery(Compte.class);
                                                        compteParseQuery.whereEqualTo("userId", payeur.getObjectId());
                                                        compteParseQuery.getFirstInBackground(new GetCallback<Compte>() {
                                                            @Override
                                                            public void done(Compte compte, ParseException e) {
                                                                if (e == null) {
                                                                    compte.setSolde(compte.getSolde() - tontine.getMontant());
                                                                    compte.pinInBackground();
                                                                    compte.saveInBackground();
                                                                    Log.d("Payeur", "updated");
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
                                                                                compte.setSolde(compte.getSolde() + tontine.getMontant());
                                                                                compte.pinInBackground();
                                                                                compte.saveInBackground();
                                                                                Log.d("Beneficiaire", "updated");
                                                                                cotisation = new Cotisation();
                                                                                cotisation.setSession(session);
                                                                                cotisation.setDate(date);
                                                                                cotisation.setAdherant(payeur);
                                                                                cotisation.setBeneficiaire(beneficiaire);
                                                                                cotisation.setMontant(tontine.getMontant());
                                                                                cotisation.saveInBackground(new SaveCallback() {
                                                                                    @Override
                                                                                    public void done(ParseException e) {
                                                                                        if (e == null) {
                                                                                            alertDialog.dismiss();
                                                                                            Log.d("Cotisation", "Effectuer");
                                                                                            Toast.makeText(context, "Opération effectuée", Toast.LENGTH_LONG).show();

                                                                                        } else {
                                                                                            alertDialog.dismiss();
                                                                                            Toast.makeText(context, "Erreur lors de la cotisation : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                            Log.d("Cotisation", "Fail to save with error : " + e.getMessage());
                                                                                        }
                                                                                    }
                                                                                });

                                                                            } else {
                                                                                alertDialog.dismiss();
                                                                                Log.d("Compte beneficiaire", "Not found");
                                                                                Toast.makeText(context, "Erreur", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });

                                                                } else {
                                                                    alertDialog.dismiss();
                                                                    Log.d("Update user", "Error " + e.getMessage());
                                                                    Toast.makeText(context, "Erreur", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                }
                                            });

                                            } else {
                                            alertDialog.dismiss();
                                            Log.i("Session", "not open");
                                            Toast.makeText(getApplicationContext(), "Aucune Session ouverte !", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        alertDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Aucune session ouverte !",Toast.LENGTH_LONG).show();
                                        Log.i("Sessions", "Empty");
                                    }
                                }
                            });
                        } else {
                            alertDialog.dismiss();
                            Log.i("Tontine", "Not found");
                        }

                    }
                });
            }
    }
    public void annonceService(){
        Intent i = new Intent(this, AnnonceActivity.class);
        i.putExtra("TONTINE_ID", tontineId);
        startActivity(i);
    }
    public void parametresService(){
        Intent i = new Intent(this, MaTontineInfoActivity.class);
        i.putExtra("TONTINE_ID", tontineId);
        if(tontine!=null){
            i.putExtra("NOM", tontine.getNom());
            i.putExtra("AMANDE", tontine.getAmande());
            i.putExtra("TYPE", tontine.getType());
            i.putExtra("ACTIVATED",tontine.getisActivated());
            i.putExtra("JOUR", tontine.getJourCotisation());
            i.putExtra("MONTANT", tontine.getMontant());
            i.putExtra("DESCRIPTION", tontine.getDescription());
            i.putExtra("PRESIDENT", tontine.getPresident().getString("nom")+" "+tontine.getPresident().getString("prenom"));
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(
                                this,
                                R.anim.anim_left_right,
                                R.anim.anim_right_left).toBundle();
                startActivity(i, bndlanimation);
            }else{
                startActivity(i);
            }
        }else{
            Log.i("Tontine",""+tontine);
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public void noInternet(){
        snackbar = new SnackBar(this,getResources().getString(R.string.no_internet), "Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
    public void noConnected(){
        snackbar = new SnackBar(this,getResources().getString(R.string.no_connected), "Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    public void storeMembreInLocalDataStore(){
        ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
        membreParseQuery.findInBackground(new FindCallback<Membre>() {
            @Override
            public void done(List<Membre> membres1, ParseException e) {
                if(e==null){
                    Membre.pinAllInBackground(membres1);
                }

            }
        });
    }

    public void storeUsersInLocalDataStore(){
        ParseQuery<ParseUser> parseUserParseQuery = ParseQuery.getQuery(ParseUser.class);
        parseUserParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                ParseUser.pinAllInBackground(parseUsers);
            }
        });
    }
    public void startService(){
        System.out.println("startService");
        startService(new Intent(this,GetDateService.class));
    }
    public void stopService(){
        stopService(new Intent(this,GetDateService.class));
    }
    public void createSession(String tontineId, final String date, final ParseUser user){
        try {
            dayOfWeek = IdjanguiProxy.getDateNow();
            if (NetworkUtil.getConnectivityStatus(context) != TYPE_NOT_CONNECTED) {
                ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
                tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                    @Override
                    public void done(final Tontine tontine, ParseException e) {
                        if(e==null){
                            final List<ParseUser> listMembre = new ArrayList<ParseUser>();
                            ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
                            membreParseQuery.whereEqualTo("tontine", tontine);
                            membreParseQuery.findInBackground(new FindCallback<Membre>() {
                                @Override
                                public void done(List<Membre> membres, ParseException e) {
                                    if(e==null){
                                        for(Membre membre:membres){
                                            listMembre.add(membre.getAdherant());
                                        }
                                        Log.i("ListeMembre size",""+listMembre.size());
                                        if(tontine.getPresident().equals(user)){
                                            Log.i("Jour cotisation",tontine.getJourCotisation());
                                            Log.i("dayOfWeek",dayOfWeek);
                                            if(tontine.getJourCotisation().equalsIgnoreCase(dayOfWeek)){
                                                Log.i("dayOfWeek equals",tontine.getJourCotisation());
                                                Session oldSession = new Session();
                                                ParseQuery<Session> sessionParseQuery = ParseQuery.getQuery(Session.class);
                                                sessionParseQuery.whereEqualTo("tontine", tontine);
                                                sessionParseQuery.orderByDescending("tour");
                                                sessionParseQuery.findInBackground(new FindCallback<Session>() {
                                                    @Override
                                                    public void done(List<Session> sessions, ParseException e) {
                                                        if(e==null && sessions.size()>0){
                                                            Log.i("SessionSize",""+sessions.size());
                                                            int n = sessions.size() - 1;
                                                            Log.i("old session tour ",""+sessions.get(0).getTour());

                                                            Log.i("TypeTontine",tontine.getType());
                                                            if(tontine.getType().equals("Tour de role")){
                                                                if (sessions.get(n).getTour() == listMembre.size()){
                                                                    Log.i("oldSession tour ",""+sessions.get(n).getTour());
                                                                    sessions.get(n).fetchIfNeededInBackground(new GetCallback<Session>() {
                                                                        @Override
                                                                        public void done(Session session, ParseException e) {
                                                                            if (e == null) {
                                                                                session.setStatu("close");
                                                                                session.setDateFin(date);
                                                                                session.saveInBackground();
                                                                                Log.i("oldSession","closed");
                                                                                Log.i("NewSession","creating");
                                                                                ParseQuery<Session> query = (new Session()).getQuery();
                                                                                query.whereEqualTo("tontine",tontine);
                                                                                query.whereEqualTo("date_creation",date);
                                                                                query.getFirstInBackground(new GetCallback<Session>() {
                                                                                    @Override
                                                                                    public void done(Session session, ParseException e) {
                                                                                        if (e == null) {
                                                                                            Log.i("NewSession", "Already created");
                                                                                        } else {
                                                                                            Session newSession = new Session();
                                                                                            newSession.setPresident(user);
                                                                                            newSession.setStatu("open");
                                                                                            newSession.setTontine(tontine);
                                                                                            newSession.setBeneficiaire(listMembre.get(0));
                                                                                            newSession.setDateCreation(date);
                                                                                            newSession.setDateDebut(date);
                                                                                            newSession.setTour(1);
                                                                                            newSession.saveInBackground(new SaveCallback() {
                                                                                                @Override
                                                                                                public void done(ParseException e) {
                                                                                                    if(e==null){
                                                                                                        Log.i("NewSession","Created");
                                                                                                    }else{
                                                                                                        Log.i("NewSession","Fail to create");
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                            //sendNotification(tontine.getPresident().getObjectId(), "idjangui".concat(tontine.getObjectId()),
                                                                                            //       tontine.getNom(), "Nouvelle session ouverte." );

                                                                                        }
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                Log.i("Session", "empty");
                                                                            }

                                                                        }
                                                                    });
                                                                }else{
                                                                    sessions.get(n).fetchIfNeededInBackground(new GetCallback<Session>() {
                                                                        @Override
                                                                        public void done(Session session, ParseException e) {
                                                                            //int tour = session.getTour()+1, lastTour = tour-1;
                                                                            ParseUser beneficiaire = listMembre.get(listMembre.indexOf(session.getBeneficiaire()) + 1);
                                                                            //session.setTour(tour);
                                                                            session.increment("tour");
                                                                            session.setBeneficiaire(beneficiaire);
                                                                            session.saveInBackground();
                                                                            //sendNotification(tontine.getPresident().getObjectId(), "idjangui".concat(tontine.getObjectId()),
                                                                            //        tontine.getNom(), "Tour "+lastTour+" Fermé et Tour "+tour+" Ouvert. Le bénéficiaire est: "+beneficiaire.getString("nom").concat(" ").concat(beneficiaire.getString("prenom")));
                                                                        }
                                                                    });
                                                                }

                                                            }else{
                                                                Log.i("Type tontine","Enchere");
                                                            }

                                                        }else{
                                                            Log.i("Sessions","Not found");
                                                            Log.i("Sessions","creating new session");
                                                            ParseQuery<Session> query = (new Session()).getQuery();
                                                            query.whereEqualTo("tontine",tontine);
                                                            query.whereEqualTo("date_creation",date);
                                                            query.getFirstInBackground(new GetCallback<Session>() {
                                                                @Override
                                                                public void done(Session session, ParseException e) {
                                                                    if(e==null){
                                                                        Log.i("Session","Already created");
                                                                    }else{
                                                                        Session newSession = new Session();
                                                                        newSession.setPresident(user);
                                                                        newSession.setStatu("open");
                                                                        newSession.setTontine(tontine);
                                                                        newSession.setBeneficiaire(listMembre.get(0));
                                                                        newSession.setDateCreation(date);
                                                                        newSession.setDateDebut(date);
                                                                        newSession.setTour(1);
                                                                        newSession.saveInBackground(new SaveCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if(e==null){
                                                                                    Log.i("newSession","Created");
                                                                                    //sendNotification(tontine.getPresident().getObjectId(), "idjangui".concat(tontine.getObjectId()),
                                                                                    //        tontine.getNom(), "Nouvelle session ouverte." );
                                                                                }else{
                                                                                    Log.i("newSession","Fail to create");
                                                                                }
                                                                            }
                                                                        });


                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                });

                                            }else{
                                                Log.i("dayOfWeek notEquals",tontine.getJourCotisation());
                                            }
                                        }

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


        } catch (IdjanguiProxyException e) {
            Log.d("getDateNow",e.getMessage());
        }

    }
    public void createSessionTontine(String tontineId, final String date, final ParseUser user){
        try {
            dayOfWeek = IdjanguiProxy.getDateNow();
            if (NetworkUtil.getConnectivityStatus(context) != TYPE_NOT_CONNECTED) {
                ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
                tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                    @Override
                    public void done(final Tontine tontine, ParseException e) {
                        if(e==null){
                            final List<ParseUser> listMembre = new ArrayList<ParseUser>();
                            ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
                            membreParseQuery.whereEqualTo("tontine", tontine);
                            membreParseQuery.findInBackground(new FindCallback<Membre>() {
                                @Override
                                public void done(List<Membre> membres, ParseException e) {
                                    if(e==null){
                                        for(Membre membre:membres){
                                            listMembre.add(membre.getAdherant());
                                        }
                                        Log.i("ListeMembre size",""+listMembre.size());
                                        if(tontine.getPresident().equals(user)){
                                            Log.i("Jour cotisation",tontine.getJourCotisation());
                                            Log.i("dayOfWeek",dayOfWeek);
                                                Log.i("dayOfWeek equals",tontine.getJourCotisation());
                                                ParseQuery<Session> sessionParseQuery = ParseQuery.getQuery(Session.class);
                                                sessionParseQuery.whereEqualTo("tontine", tontine);
                                                sessionParseQuery.orderByDescending("tour");
                                                sessionParseQuery.findInBackground(new FindCallback<Session>() {
                                                    @Override
                                                    public void done(List<Session> sessions, ParseException e) {
                                                        if(e==null && sessions.size()>0){
                                                            Log.i("SessionSize",""+sessions.size());
                                                            int n = sessions.size() - 1;
                                                            Log.i("old session tour ",""+sessions.get(0).getTour());

                                                            Log.i("TypeTontine",tontine.getType());
                                                            if(tontine.getType().equals("Tour de role")){
                                                                if((sessions.get(n).getTour() < listMembre.size())&&(sessions.get(n).getTourIsFinal()==false)){

                                                                }else if((sessions.get(n).getTour() < listMembre.size())&&(sessions.get(n).getTourIsFinal()==true)){

                                                                }else if((sessions.get(n).getTour() == listMembre.size())&&(sessions.get(n).getTourIsFinal()==false)){

                                                                }else if ((sessions.get(n).getTour() == listMembre.size())&&(sessions.get(n).getTourIsFinal()==true)){
                                                                    Log.i("oldSession tour ",""+sessions.get(n).getTour());
                                                                    sessions.get(n).fetchIfNeededInBackground(new GetCallback<Session>() {
                                                                        @Override
                                                                        public void done(Session session, ParseException e) {
                                                                            if (e == null) {
                                                                                if(tontine.getJourCotisation().equalsIgnoreCase(dayOfWeek)){
                                                                                    session.setTourIsFinal(true);
                                                                                }else{
                                                                                    session.setTourIsFinal(false);
                                                                                }
                                                                                session.setStatu("close");
                                                                                session.setDateFin(date);
                                                                                session.saveInBackground();
                                                                                Log.i("oldSession","closed");
                                                                                Log.i("NewSession","creating");
                                                                                ParseQuery<Session> query = (new Session()).getQuery();
                                                                                query.whereEqualTo("tontine",tontine);
                                                                                query.whereEqualTo("date_creation",date);
                                                                                query.getFirstInBackground(new GetCallback<Session>() {
                                                                                    @Override
                                                                                    public void done(Session session, ParseException e) {
                                                                                        if (e == null) {
                                                                                            Log.i("NewSession", "Already created");
                                                                                        } else {
                                                                                            Session newSession = new Session();
                                                                                            newSession.setPresident(user);
                                                                                            newSession.setStatu("open");
                                                                                            newSession.setTontine(tontine);
                                                                                            newSession.setBeneficiaire(listMembre.get(0));
                                                                                            newSession.setDateCreation(date);
                                                                                            newSession.setDateDebut(date);
                                                                                            newSession.setTour(1);
                                                                                            newSession.saveInBackground(new SaveCallback() {
                                                                                                @Override
                                                                                                public void done(ParseException e) {
                                                                                                    if(e==null){
                                                                                                        Log.i("NewSession","Created");
                                                                                                    }else{
                                                                                                        Log.i("NewSession","Fail to create");
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                            //sendNotification(tontine.getPresident().getObjectId(), "idjangui".concat(tontine.getObjectId()),
                                                                                            //       tontine.getNom(), "Nouvelle session ouverte." );

                                                                                        }
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                Log.i("Session", "empty");
                                                                            }

                                                                        }
                                                                    });
                                                                }else{
                                                                    sessions.get(n).fetchIfNeededInBackground(new GetCallback<Session>() {
                                                                        @Override
                                                                        public void done(Session session, ParseException e) {
                                                                            //int tour = session.getTour()+1, lastTour = tour-1;
                                                                            ParseUser beneficiaire = listMembre.get(listMembre.indexOf(session.getBeneficiaire()) + 1);
                                                                            //session.setTour(tour);
                                                                            session.increment("tour");
                                                                            session.setBeneficiaire(beneficiaire);
                                                                            session.saveInBackground();
                                                                            //sendNotification(tontine.getPresident().getObjectId(), "idjangui".concat(tontine.getObjectId()),
                                                                            //        tontine.getNom(), "Tour "+lastTour+" Fermé et Tour "+tour+" Ouvert. Le bénéficiaire est: "+beneficiaire.getString("nom").concat(" ").concat(beneficiaire.getString("prenom")));
                                                                        }
                                                                    });
                                                                }

                                                            }else{
                                                                Log.i("Type tontine","Enchere");
                                                            }

                                                        }else{
                                                            Log.i("Sessions","Not found");
                                                            Log.i("Sessions","creating new session");
                                                            ParseQuery<Session> query = (new Session()).getQuery();
                                                            query.whereEqualTo("tontine",tontine);
                                                            query.whereEqualTo("date_creation",date);
                                                            query.getFirstInBackground(new GetCallback<Session>() {
                                                                @Override
                                                                public void done(Session session, ParseException e) {
                                                                    if(e==null){
                                                                        Log.i("Session","Already created");
                                                                    }else{
                                                                        Session newSession = new Session();
                                                                        newSession.setPresident(user);
                                                                        newSession.setStatu("open");
                                                                        newSession.setTontine(tontine);
                                                                        newSession.setBeneficiaire(listMembre.get(0));
                                                                        newSession.setDateCreation(date);
                                                                        newSession.setDateDebut(date);
                                                                        newSession.setTour(1);
                                                                        newSession.saveInBackground(new SaveCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if(e==null){
                                                                                    Log.i("newSession","Created");
                                                                                    //sendNotification(tontine.getPresident().getObjectId(), "idjangui".concat(tontine.getObjectId()),
                                                                                    //        tontine.getNom(), "Nouvelle session ouverte." );
                                                                                }else{
                                                                                    Log.i("newSession","Fail to create");
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


        } catch (IdjanguiProxyException e) {
            Log.d("getDateNow",e.getMessage());
        }

    }
}
