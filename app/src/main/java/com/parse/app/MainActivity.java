package com.parse.app;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
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
import com.parse.app.adapter.TabsPagerAdapter;
import com.parse.app.model.Compte;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;
import com.parse.app.proxy.IdjanguiProxy;
import com.parse.app.proxy.IdjanguiProxyException;
import com.parse.app.proxy.IdjanguiProxyImpl;
import com.parse.app.utilities.NetworkUtil;

import java.util.Calendar;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    private ViewPager mViewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private Context context;
    private ParseUser user;
    public static int TYPE_NOT_CONNECTED = 0;
    private Integer montant;
    private Compte compteUser;
    private SnackBar snackbar;
    private Integer thiscompte;
    private MaterialDialog materialDialog;
    private String dayOfWeek;
    private IdjanguiProxyImpl IdjanguiProxy = IdjanguiProxyImpl.getInstance();
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        user = ParseUser.getCurrentUser();
        try {
            dayOfWeek = IdjanguiProxy.getDateNow();
            //Toast.makeText(this,"dateNow = " + dayOfWeek,Toast.LENGTH_LONG).show();
        } catch (IdjanguiProxyException e) {
            Log.d("getDateNow",e.getMessage());
        }
        startService();
        getCompte(user);
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this, R.string.no_connected, Toast.LENGTH_SHORT).show();
        } else {
            storeTontineInLocalDataStore();
            storeSessionInLocalDataStore();
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", user);
            installation.saveInBackground();
        }
        final ActionBar actionBar = getSupportActionBar();
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
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
                .setText("Mes Tontines")
                .setTabListener(this);

        ActionBar.Tab tab1 = actionBar
                .newTab()
                .setText("Tontines")
                .setTabListener(this);

        actionBar.addTab(tab, 0, false);
        actionBar.addTab(tab1, 1, false);

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    public void getCompte(ParseUser user){
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            getCompteFromLocalDataStore(user);
        } else {
            ParseQuery<Compte> compteParseUser = ParseQuery.getQuery(Compte.class);
            compteParseUser.whereEqualTo("userId", user.getObjectId());
            compteParseUser.getFirstInBackground(new GetCallback<Compte>() {
                @Override
                public void done(Compte compte, ParseException e) {
                    if (e == null) {
                        if (compte.getSolde() != null) {
                            thiscompte = compte.getSolde();
                        }
                    }
                }
            });
        }
    }
    public void getCompteFromLocalDataStore(ParseUser user){
        ParseQuery<Compte> compteParseUser = ParseQuery.getQuery(Compte.class);
        compteParseUser.fromLocalDatastore();
        compteParseUser.whereEqualTo("userId", user.getObjectId());
        compteParseUser.getFirstInBackground(new GetCallback<Compte>() {
            @Override
            public void done(Compte compte, ParseException e) {
                if (e == null) {
                    if (compte.getSolde() != null) {
                        thiscompte = compte.getSolde();
                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            settingsService();
            return true;
        } else*/ if (id == R.id.action_share) {
            String message = getResources().getString(R.string.text_to_share_with_friends);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(share, getResources().getString(R.string.recommend_to_friends)));
            return true;
        } else if (id == R.id.action_moncompte) {
            monCompteService();
            return true;
        }/* else if (id == R.id.group) {
            startAddGroup();
        }*/ else if (id == R.id.action_logout) {
            logoutAction();
        }
        return super.onOptionsItemSelected(item);
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

    public void logoutAction() {
        ParseUser.logOut();
        Intent i = new Intent(this, LoginActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(
                            this,
                            R.anim.anim_right_left,
                            R.anim.anim_left_right).toBundle();
            startActivity(i, bndlanimation);

        } else {
            startActivity(i);

        }
    }


    public void startAddGroup() {
        Intent i = new Intent(this, CreerTontine1.class);
        startActivity(i);
    }

    public void settingsService() {
        Intent i = new Intent(this, SettingsActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(
                            this,
                            R.anim.anim_left_right,
                            R.anim.anim_right_left).toBundle();
            startActivity(i, bndlanimation);

        } else {
            startActivity(i);

        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    public void monCompteService() {
        if(thiscompte != null) {
            materialDialog = new MaterialDialog(context);
            materialDialog.setCanceledOnTouchOutside(true);
            materialDialog.setTitle("Le solde de votre compte est:");
            materialDialog.setMessage(" " + thiscompte + " Fcfa");
            materialDialog.setNegativeButton("", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.setPositiveButton("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }else{
          snackBar();
        }

    }
    public void snackBar(){
        snackbar = new SnackBar(this, "Erreur: veuillez rééssayer !", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        snackbar.show();
    }
    public void storeTontineInLocalDataStore(){
        ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
        tontineParseQuery.findInBackground(new FindCallback<Tontine>() {
            @Override
            public void done(List<Tontine> tontines, ParseException e) {
                if(e==null){
                    Tontine.pinAllInBackground(tontines);
                }

            }
        });
    }
    public void storeSessionInLocalDataStore(){
        ParseQuery<Session> sessionParseQuery = (new Session()).getQuery();
        sessionParseQuery.findInBackground(new FindCallback<Session>() {
            @Override
            public void done(List<Session> sessions, ParseException e) {
                if(e==null){
                    Tontine.pinAllInBackground(sessions);
                }

            }
        });
    }

    public void startService(){
        startService(new Intent(this,GetDateService.class));
    }
    public void stopService(){
        stopService(new Intent(this,GetDateService.class));
    }
}
