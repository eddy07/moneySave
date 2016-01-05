package com.parse.app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.parse.app.adapter.AnnonceAdapter;
import com.parse.app.model.Annonce;
import com.parse.app.model.Presence;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;


public class ListAnnonce extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private AnnonceAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private List<Annonce> annonces = new ArrayList<Annonce>();
    public static final int TYPE_NOT_CONNECTED = 0;
    private String tontineId;
    private ParseUser thisuser;
    private Context context;
    private boolean reachedTop;
    private Presence presence;
    private ProgressWheel progressWheel;
    private boolean status;
    private TextView textNoAnnonce;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_annonce);
        getSupportActionBar().setTitle("Infos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        activity = this;
        thisuser = ParseUser.getCurrentUser();
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        listView = (ListView) findViewById(R.id.listviewAnnonce);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);
        adapter = new AnnonceAdapter(this, this, annonces, tontineId);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        adapter.notifyDataSetChanged();
        ScrollView emptyView = (ScrollView) findViewById(R.id.emptyList);
        listView.setEmptyView(emptyView);

        textNoAnnonce = (TextView) findViewById(R.id.textNoAnnonce);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_broadcast);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.app_color, R.color.app_color, R.color.app_color,R.color.app_color);
        swipeLayout.setEnabled(true);
        loadAnnonce();
        //listView.setDividerHeight(1);
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
                Annonce annonce = annonces.get(position);
            }
        });
    }

    public void loadAnnonce(){
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            swipeLayout.setRefreshing(false);
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();

        } else {
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
            tontineQuery.whereEqualTo("objectId", tontineId);
            tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if((e == null) && (tontine != null)){
                        ParseQuery<Annonce> annonceQuery = ParseQuery.getQuery(Annonce.class);
                        annonceQuery.whereEqualTo("tontine", tontine);
                        annonceQuery.addDescendingOrder("createdAt");
                        annonceQuery.whereEqualTo("adherant",thisuser);
                        annonceQuery.findInBackground(new FindCallback<Annonce>() {
                            @Override
                            public void done(List<Annonce> annonceList, ParseException e) {
                                if(( e == null) && (annonceList.size()>=0)){
                                    annonces = annonceList;
                                    if(annonces.size() == 0){
                                        AnnonceAdapter sa = new AnnonceAdapter(activity, context, annonces,tontineId);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                        progressWheel.setVisibility(View.GONE);
                                        textNoAnnonce.setVisibility(View.VISIBLE);

                                    }else{
                                        progressWheel.setVisibility(View.GONE);
                                        textNoAnnonce.setVisibility(View.GONE);
                                        listView.setAdapter(new AnnonceAdapter(activity, context, annonces,tontineId));
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    Log.d("Annonce", "error "+ e.getMessage());
                                    progressWheel.setVisibility(View.GONE);
                                    textNoAnnonce.setVisibility(View.VISIBLE);
                                    swipeLayout.setRefreshing(false);
                                }
                            }
                        });
                    }else{
                        Log.d("Tontine", "error "+ e.getMessage());
                        progressWheel.setVisibility(View.GONE);
                        textNoAnnonce.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
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
        Intent i = new Intent(this, MainTontineActivity.class);
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
                                MenuItem item = menu.add("Ajouter un annonce");
                                item.setIcon(R.drawable.ic_add); //your desired icon here
                                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        startAddAnnonce();
                                        return true;
                                    }

                                });
                                getMenuInflater().inflate(R.menu.menu_list_annonce, menu);
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


    public void startAddAnnonce(){

        Intent i = new Intent(this,CreerAnnonceActivity.class);
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
                        ParseQuery<Annonce> annonceQuery = ParseQuery.getQuery(Annonce.class);
                        annonceQuery.whereEqualTo("tontine", tontine);
                        annonceQuery.orderByDescending("createdAt");
                        annonceQuery.whereEqualTo("adherant", thisuser);
                        annonceQuery.findInBackground(new FindCallback<Annonce>() {
                            @Override
                            public void done(List<Annonce> annonceList, ParseException e) {
                                if(( e == null) && (annonceList.size()>=0)){
                                    annonces = annonceList;
                                    listView.setAdapter(new AnnonceAdapter(activity, context, annonces,tontineId));
                                    if(annonces.size() == 0){
                                        AnnonceAdapter sa = new AnnonceAdapter(activity, context, annonces,tontineId);
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
