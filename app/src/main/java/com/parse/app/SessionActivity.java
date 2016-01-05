package com.parse.app;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.adapter.SessionAdapter;
import com.parse.app.model.Tontine;
import com.parse.app.model.Presence;
import com.parse.app.model.Session;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SessionActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private SessionAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private List<Session> sessions = new ArrayList<Session>();
    public static final int TYPE_NOT_CONNECTED = 0;
    private String tontineId;
    private Context context;
    private boolean status;
    private ParseUser thisuser;
    private ProgressBar progressBar;
    private ProgressWheel progressWheel;
    private boolean reachedTop;
    private Presence presence;
    private Tontine thistontine;
    private TextView contentLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        getSupportActionBar().setTitle("Sessions");
        context = this;
        thisuser = ParseUser.getCurrentUser();
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        listView = (ListView) findViewById(R.id.listviewSession);
        //progressBar = (ProgressBar)findViewById(R.id.load);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);
        /*progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);*/
        adapter = new SessionAdapter(this, sessions);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        adapter.notifyDataSetChanged();
        ScrollView emptyView = (ScrollView) findViewById(R.id.emptyList);
        listView.setEmptyView(emptyView);
        thistontine = new Tontine();
        contentLoad = (TextView) findViewById(R.id.textNoSession);
        contentLoad.setVisibility(View.GONE);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_broadcast);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.app_color, R.color.app_color, R.color.app_color,R.color.app_color);
        swipeLayout.setEnabled(true);

        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);

        } else {
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
            tontineQuery.whereEqualTo("objectId", tontineId);
            tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if((e == null) && (tontine != null)){
                        thistontine = tontine;
                        ParseQuery<Session> sessionQuery = ParseQuery.getQuery("Session");
                        sessionQuery.whereEqualTo("tontine", tontine);
                        sessionQuery.orderByDescending("statu");
                        sessionQuery.findInBackground(new FindCallback<Session>() {
                            @Override
                            public void done(List<Session> sessionList, ParseException e) {
                                if(( e == null) && (sessionList.size()>=0)){
                                     sessions = sessionList;
                                    contentLoad.setVisibility(View.GONE);
                                    progressWheel.setVisibility(View.GONE);
                                    listView.setAdapter(new SessionAdapter(context, sessions));
                                    if(sessions.size() == 0){
                                        SessionAdapter sa = new SessionAdapter(context, sessions);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                        progressWheel.setVisibility(View.GONE);
                                        contentLoad.setVisibility(View.VISIBLE);
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                  Log.d("Session", "error "+ e.getMessage());
                                    swipeLayout.setRefreshing(false);
                                    progressWheel.setVisibility(View.GONE);
                                    contentLoad.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }else{
                        Log.d("Tontine", "error "+ e.getMessage());
                    }
                }
            });

        }
        listView.setDividerHeight(0);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = listView.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:
                        reachedTop = true;
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem){
                    View v =  listView.getChildAt(totalItemCount-1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:
                        reachedTop = true;
                        return;
                    }
                } else {
                    reachedTop = false;
                    return;
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Session session = sessions.get(position);
                final String date = DateFormat.getDateTimeInstance().format(new Date());
                //Toast.makeText(context,"statu = " + session.getStatu(),Toast.LENGTH_LONG).show();
                if(session.getStatu().equals("ouverte")){
                    ParseQuery<Presence> presenceParseQuery = ParseQuery.getQuery(Presence.class);
                    presenceParseQuery.whereEqualTo("session",session);
                    presenceParseQuery.whereEqualTo("statu","present");
                    presenceParseQuery.whereEqualTo("membre",ParseUser.getCurrentUser());
                    presenceParseQuery.getFirstInBackground(new GetCallback<Presence>() {
                        @Override
                        public void done(Presence presence, ParseException e) {
                            if(e==null){
                                Log.d("Presence","ok");
                            }else{
                                Log.d("Presence","not ok");
                                presence = new Presence();
                                presence.setStatu("present");
                                presence.setDate(date);
                                presence.setMembre(ParseUser.getCurrentUser());
                                presence.setSession(session);
                                presence.setMontantCotise(0);
                                presence.saveInBackground();
                            }
                            Intent i = new Intent(context, TontineActivity.class);
                            i.putExtra("SESSION_ID", session.getObjectId());
                            i.putExtra("TONTINE_ID", tontineId);
                            i.putExtra("DATE",session.getDateCreation());
                            if (android.os.Build.VERSION.SDK_INT >= 16) {
                                Bundle bndlanimation =
                                        ActivityOptions.makeCustomAnimation(
                                                context,
                                                R.anim.anim_left_right,
                                                R.anim.anim_right_left).toBundle();
                                startActivity(i, bndlanimation);
                            } else {
                                startActivity(i);

                            }
                        }
                    });

                }else {
                    Intent i = new Intent(context, InfoSessionActivity.class);
                    if((session.getObjectId()!=null)&&(tontineId!=null)) {
                        i.putExtra("SESSION_ID", session.getObjectId());
                        i.putExtra("TONTINE_ID", tontineId);
                        if (android.os.Build.VERSION.SDK_INT >= 16) {
                            Bundle bndlanimation =
                                    ActivityOptions.makeCustomAnimation(
                                            context,
                                            R.anim.anim_left_right,
                                            R.anim.anim_right_left).toBundle();
                            startActivity(i, bndlanimation);
                        } else {
                            startActivity(i);

                        }
                    }else{
                        Toast.makeText(context,"Hohoo... internet error. please retry!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
       // onRefresh();
        //swipeLayout.setRefreshing(false);
    }


            @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
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
                                    MenuItem item = menu.add("Ouvrir une session");
                                    item.setIcon(R.drawable.ic_add); //your desired icon here
                                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                                    item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem menuItem) {
                                            //closeSessionService();
                                            startAddSession();
                                            return true;
                                        }

                                    });
                                    getMenuInflater().inflate(R.menu.menu_session, menu);
                                } else {
                                    getMenuInflater().inflate(R.menu.menu_session, menu);
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
    public void onBackPressed(){
        Intent i = new Intent(context, MainActivity.class);
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

        }else if (id == R.id.soutient) {
            startSoutient();
            return true;
        }else if (id == R.id.annonce) {
            startAnnonce();
            return true;
        }else if (id == R.id.notification) {
            startNotification();
            return true;
        }else if(id == R.id.membres){
            getmembresService();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void startSoutient(){
        Intent i = new Intent(this, DemandeSoutientActivity.class);
        if(tontineId!=null) {
            i.putExtra("TONTINE_ID", tontineId);
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
        }else{
            Toast.makeText(this,"Oups... internet error. please retry!", Toast.LENGTH_LONG).show();
        }
    }
    public void startAnnonce(){
        Intent i = new Intent(this, AnnonceActivity.class);
        if(tontineId!=null) {
            i.putExtra("TONTINE_ID", tontineId);

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
        }else{
            Toast.makeText(this,"Oups... internet error, please try egain!",Toast.LENGTH_LONG).show();
        }
    }
    public void startNotification(){
        Intent i = new Intent(this, NotificationActivity.class);
        if(tontineId!=null){
            i.putExtra("TONTINE_ID", tontineId);
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
        }else{
            Toast.makeText(this,"Oups... internet error, please try egain!",Toast.LENGTH_LONG).show();
        }
    }

    public void startAddSession(){
        Intent i = new Intent(this,CreerSessionActivity.class);
        if(tontineId!=null) {
            i.putExtra("TONTINE_ID", tontineId);
            startActivity(i);
        }else{
            Toast.makeText(this,"Oups... internet error, please try egain!",Toast.LENGTH_LONG).show();
        }
    }

    public void getmembresService(){
        Intent i = new Intent(this,ListMembreActivity.class);
        if(tontineId!=null) {
            i.putExtra("TONTINE_ID", tontineId);
            startActivity(i);
        }else{
            Toast.makeText(this,"Oups... internet error, please try egain!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);

        } else {
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
            tontineQuery.whereEqualTo("objectId", tontineId);
            tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if((e == null) && (tontine != null)){
                        ParseQuery<Session> sessionQuery = ParseQuery.getQuery("Session");
                        sessionQuery.whereEqualTo("tontine", tontine);
                        sessionQuery.addDescendingOrder("statu");
                        sessionQuery.findInBackground(new FindCallback<Session>() {
                            @Override
                            public void done(List<Session> sessionList, ParseException e) {
                                if(( e == null) && (sessionList.size()>=0)){
                                    sessions = sessionList;
                                    progressWheel.setVisibility(View.GONE);
                                    contentLoad.setVisibility(View.GONE);
                                    listView.setAdapter(new SessionAdapter(context, sessions));
                                    if(sessions.size() == 0){
                                        SessionAdapter sa = new SessionAdapter(context, sessions);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                        progressWheel.setVisibility(View.GONE);
                                        contentLoad.setVisibility(View.VISIBLE);
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    swipeLayout.setRefreshing(false);
                                    progressWheel.setVisibility(View.GONE);
                                    contentLoad.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }else{

                    }
                }
            });
        }
    }
}
