package com.parse.app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.adapter.MembreListAdapter;
import com.parse.app.model.Membre;
import com.parse.app.model.Presence;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;


public class ListMembreActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private MembreListAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private List<Membre> membres = new ArrayList<Membre>();
    public static final int TYPE_NOT_CONNECTED = 0;
    private String tontineId;
    private Activity a;
    private ParseUser thisuser;
    private Context context;
    private boolean reachedTop;
    private Presence presence;
    private ProgressWheel progressWheel;
    private boolean status;
    private TextView textNoMembre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_membre);
        getSupportActionBar().setTitle("Membres");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        a = this;
        thisuser = ParseUser.getCurrentUser();
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        listView = (ListView) findViewById(R.id.listviewMembre);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);
        adapter = new MembreListAdapter(tontineId,this,this, membres);
        listView.setAdapter(adapter);
        //listView.setDividerHeight(0);
        adapter.notifyDataSetChanged();
        ScrollView emptyView = (ScrollView) findViewById(R.id.emptyList);
        listView.setEmptyView(emptyView);

        textNoMembre = (TextView) findViewById(R.id.textNoMembre);
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
                        ParseQuery<Membre> membreQuery = ParseQuery.getQuery(Membre.class);
                        membreQuery.whereEqualTo("tontine", tontine);
                        membreQuery.orderByAscending("date_inscription");
                        membreQuery.whereNotEqualTo("adherant", thisuser);
                        membreQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(List<Membre> membreList, ParseException e) {
                                if(( e == null) && (membreList.size()>=0)){
                                    membres = membreList;
                                    if(membres.size() == 0){
                                        MembreListAdapter sa = new MembreListAdapter(tontineId,context,a, membres);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                        progressWheel.setVisibility(View.GONE);
                                        textNoMembre.setVisibility(View.VISIBLE);

                                    }else{
                                        progressWheel.setVisibility(View.GONE);
                                        textNoMembre.setVisibility(View.GONE);
                                        listView.setAdapter(new MembreListAdapter(tontineId,context,a, membres));
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    Log.d("Membre", "error "+ e.getMessage());
                                    progressWheel.setVisibility(View.GONE);
                                    textNoMembre.setVisibility(View.VISIBLE);
                                    swipeLayout.setRefreshing(false);
                                }
                            }
                        });
                    }else{
                        Log.d("Tontine", "error "+ e.getMessage());
                    }
                }
            });

        }
        listView.setDividerHeight(1);
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
                Membre membre = membres.get(position);
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
        //onRefresh();
        //swipeLayout.setRefreshing(false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this, SessionActivity.class);
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
            Toast.makeText(this,"Oups... internet error. please retry!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                            if(status == true){
                                MenuItem item = menu.add("Ajouter un membre");
                                //item.setIcon(R.drawable.icon); //your desired icon here
                                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        startAddMembre();
                                        return true;
                                    }

                                });
                                getMenuInflater().inflate(R.menu.menu_list_membre, menu);
                            }
                        }
                    });
                } else {
                    Log.d("Tontine","not found");
                }
            }
        });

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConf){

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
        }else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void startAddMembre(){
        Intent i = new Intent(this,AjoutMembre.class);
        if(tontineId != null) {
            i.putExtra("TONTINE_ID", tontineId);
            startActivity(i);
        }else{
            Toast.makeText(this,"Oups... internet error. please retry!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);

        } else {
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery(Tontine.class);
            tontineQuery.whereEqualTo("objectId", tontineId);
            tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if((e == null) && (tontine != null)){
                        ParseQuery<Membre> membreQuery = ParseQuery.getQuery(Membre.class);
                        membreQuery.whereEqualTo("tontine", tontine);
                        membreQuery.orderByAscending("date_inscription");
                        membreQuery.whereNotEqualTo("adherant", thisuser);
                        membreQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(List<Membre> membreList, ParseException e) {
                                if(( e == null) && (membreList.size()>=0)){
                                    membres = membreList;
                                    listView.setAdapter(new MembreListAdapter(tontineId,context,a, membres));
                                    if(membres.size() == 0){
                                        MembreListAdapter sa = new MembreListAdapter(tontineId,context,a, membres);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    }
                                    System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    swipeLayout.setRefreshing(false);
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
