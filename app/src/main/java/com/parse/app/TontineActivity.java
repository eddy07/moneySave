package com.parse.app;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.adapter.TontinePagerAdapter;
import com.parse.app.model.Cotisation;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;

import me.drakeet.materialdialog.MaterialDialog;


public class TontineActivity extends ActionBarActivity implements ActionBar.TabListener{

    ViewPager mViewPager;
    TontinePagerAdapter tontinePagerAdapter;
    Context context;
    private String tontineId;
    private String sessionId;
    private boolean status;
    private  String sessionDate;
    private String date;
    private ParseUser thisuser;
    private ProgressDialog progressDialog;

    public static final int TYPE_NOT_CONNECTED = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tontine);
        getSupportActionBar().setTitle("Session en cours");
        thisuser = ParseUser.getCurrentUser();
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        sessionId = getIntent().getExtras().getString("SESSION_ID");
        date = getIntent().getExtras().getString("DATE");
        context = this;
        final ActionBar actionBar = getSupportActionBar();
        tontinePagerAdapter = new TontinePagerAdapter(getSupportFragmentManager(),sessionId);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(tontinePagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        getSupportActionBar().setSubtitle(date.substring(0,12));

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Bundle b = getIntent().getExtras();
        ActionBar.Tab tab = actionBar
                .newTab()
                .setText("Discussions")
                .setTabListener(this);

        ActionBar.Tab tab1 = actionBar
                .newTab()
                .setText("Membres")
                .setTabListener(this);

        actionBar.addTab(tab, 0, false);
        actionBar.addTab(tab1, 1, false);

    }

    public void initprogress(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("En cours de création ...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public void initprogress2(){
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("En cours ...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public boolean isPresident(){

        ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
        tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
            @Override
            public void done(Tontine tontine, ParseException e) {
                if (e == null) {
                    ParseUser pr =  tontine.getPresident();
                    pr.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseObject, ParseException e) {
                            status = parseObject.getUsername().equals(thisuser.getUsername());
                            //Toast.makeText(context,"isPresident = " + status, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Log.d("Tontine","not found");
                }
            }
        });
        return status;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(NetworkUtil.getConnectivityStatus(this)==0) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();

        }else {
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
            tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if (e == null) {
                        ParseUser pr = tontine.getPresident();
                        pr.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser parseObject, ParseException e) {
                                status = parseObject.getUsername().equals(thisuser.getUsername());
                                //Toast.makeText(context, "isPresident = " + status, Toast.LENGTH_LONG).show();
                                if (status == true) {
                                    MenuItem item = menu.add("Fermer la session");
                                    //item.setIcon(R.drawable.icon); //your desired icon here
                                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                                    item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem menuItem) {
                                            closeSessionService();

                                            return true;
                                        }

                                    });
                                    getMenuInflater().inflate(R.menu.menu_tontine, menu);
                                } else {
                                    getMenuInflater().inflate(R.menu.menu_tontine, menu);
                                }
                            }
                        });
                    } else {
                        Log.d("Tontine", "not found");
                    }
                }
            });
        }
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
        }else if (id == R.id.cotiser) {
            if(NetworkUtil.getConnectivityStatus(this)==0) {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();

            }else {
                ParseQuery<Session> sessionQuery = ParseQuery.getQuery(Session.class);
                sessionQuery.whereEqualTo("statu", "ouverte");
                sessionQuery.getInBackground(sessionId, new GetCallback<Session>() {
                    @Override
                    public void done(final Session session, ParseException e) {
                        if (e == null) {
                            //Toast.makeText(context, "enter", Toast.LENGTH_LONG).show();
                            ParseQuery<Cotisation> cotisationParseQuery = ParseQuery.getQuery(Cotisation.class);
                            cotisationParseQuery.whereEqualTo("session", session);
                            cotisationParseQuery.whereEqualTo("beneficiaire", session.getBeneficiaire());
                            cotisationParseQuery.whereEqualTo("adherant", thisuser);
                            cotisationParseQuery.getFirstInBackground(new GetCallback<Cotisation>() {
                                @Override
                                public void done(Cotisation c, ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(context, "Vous avez déjà cotisé !", Toast.LENGTH_LONG).show();
                                    } else {
                                        cotiserService();

                                    }
                                }
                            });
                        } else {
                           Log.d("Session","Not found with error : " + e.getMessage());
                        }
                    }
                });
            }

            return true;
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


    public void closeSessionService(){
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setMessage("Voulez-vous vraiment fermer la session en cours ?");

        materialDialog.setNegativeButton("Non", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });
        materialDialog.setPositiveButton("Oui", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
                if (NetworkUtil.getConnectivityStatus(context) == 0) {
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();

                } else {
                    initprogress2();
                    ParseQuery<Session> sessionParseQuery = ParseQuery.getQuery(Session.class);
                    sessionParseQuery.whereEqualTo("president", thisuser);
                    sessionParseQuery.whereEqualTo("statu", "ouverte");
                    sessionParseQuery.getInBackground(sessionId, new GetCallback<Session>() {
                        @Override
                        public void done(Session session, ParseException e) {
                            session.setStatu("fermée");
                            session.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("click","enter3");
                                        progressDialog.dismiss();
                                        onBackPressed();
                                    } else {
                                        Log.d("click","enter4");
                                        progressDialog.dismiss();
                                        Log.d("Session", "Fail to close with error : " + e.getMessage());
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });
        materialDialog.show();
    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(context, SessionActivity.class);
        if(tontineId != null) {
            i.putExtra("TONTINE_ID", tontineId);
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(
                                context,
                                R.anim.anim_right_left,
                                R.anim.anim_left_right).toBundle();
                startActivity(i, bndlanimation);
                finish();
            } else {
                startActivity(i);
                finish();
            }
        }else{
            Toast.makeText(this,"Hohoo... internet error. please retry!", Toast.LENGTH_LONG).show();
        }
    }

            public void cotiserService() {
                Intent i = new Intent(this, CotiserActivity.class);
                if((tontineId !=null)&&(sessionId!=null)&&(date!=null)) {
                    i.putExtra("TONTINE_ID", tontineId);
                    i.putExtra("SESSION_ID", sessionId);
                    i.putExtra("DATE", date);
                    if (android.os.Build.VERSION.SDK_INT >= 16) {
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(
                                        this,
                                        R.anim.anim_left_right,
                                        R.anim.anim_right_left).toBundle();
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
        }
