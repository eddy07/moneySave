package com.parse.app;

import android.app.ActivityOptions;
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
import com.parse.app.adapter.TabsPagerAdapter;
import com.parse.app.adapter.TabsTontinePagerAdapter;
import com.parse.app.asynctask.SendAnnonceAsyncTask;
import com.parse.app.model.Compte;
import com.parse.app.model.Membre;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.ArrayList;
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
    private String nom;
    private boolean started;
    private String currDate;
    private SendAnnonceAsyncTask sendAnnonceAsyncTask;
    public static final int TYPE_NOT_CONNECTED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Receiver receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter(MyService.EVENT_ACTION);
        registerReceiver(receiver, intentFilter);
        context = this;
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        nom = getIntent().getExtras().getString("NOM");
        date = DateFormat.getDateTimeInstance().format(new Date());
        getSupportActionBar().setTitle("Tontine");
        getSupportActionBar().setSubtitle(nom);
        user = ParseUser.getCurrentUser();
        final ActionBar actionBar = getSupportActionBar();
        tabsPagerAdapter = new TabsTontinePagerAdapter(getSupportFragmentManager(), tontineId, currDate);
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

    }

    public class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MyService.EVENT_ACTION)){
                Bundle bundle = intent.getExtras();
                if(bundle!=null){
                    currDate = bundle.getString("DATE");
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
                                    if(currDate.startsWith(tontine.getJourCotisation())){
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
            }
        }
    }

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
            cotiserService();
            return true;
        }/*else if (id == R.id.action_settings) {
            parametresService();
        }*/

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    public void cotiserService(){
        Intent i = new Intent(this, TontinerActivity.class);
        i.putExtra("TONTINE_ID", tontineId);
        startActivity(i);
    }
    public void annonceService(){
        Intent i = new Intent(this, AnnonceActivity.class);
        i.putExtra("TONTINE_ID", tontineId);
        startActivity(i);
    }
    public void parametresService(){
        Intent i = new Intent(this, TontineSetting.class);
        i.putExtra("TONTINE_ID", tontineId);
        startActivity(i);
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

    /*@Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
    }*/

    public void snackBar(){
        snackbar = new SnackBar(this, "Erreur: veuillez rééssayer !", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        snackbar.show();
    }
}
