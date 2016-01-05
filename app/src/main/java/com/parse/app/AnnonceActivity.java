package com.parse.app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.adapter.AnnonceAdapter;
import com.parse.app.model.Annonce;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


public class AnnonceActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private AnnonceAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private List<Annonce> annonces = new ArrayList<Annonce>();
    public static final int TYPE_NOT_CONNECTED = 0;
    private String tontineId;
    private Context context;
    private String nom;
    private boolean reachedTop;
    private ProgressWheel progressWheel;
    private TextView textNoAnnonce;
    private SnackBar snackBar;
    private ParseUser thisuser;
    private Activity activity;
    private ImageButton mEditBtn;
    private boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce);
        getSupportActionBar().setTitle("Annonces");
        context = this;
        activity = this;
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        listView = (ListView) findViewById(R.id.listviewAnnonce);
        thisuser = ParseUser.getCurrentUser();
        adapter = new AnnonceAdapter(activity, this, annonces, tontineId);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        adapter.notifyDataSetChanged();
        ScrollView emptyView = (ScrollView) findViewById(R.id.emptyList);
        listView.setEmptyView(emptyView);
        mEditBtn = (ImageButton)findViewById(R.id.editButton);
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            snakBar(false, "noInternet");
        } else {
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery(Tontine.class);
            tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if (e == null) {
                        ParseUser pr = tontine.getPresident();
                        nom = tontine.getNom();
                        pr.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser parseObject, ParseException e) {
                                status = parseObject.getUsername().equals(thisuser.getUsername());
                                if (status == true) {
                                    mEditBtn.setVisibility(View.VISIBLE);
                                    mEditBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startAddAnnonce();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        Log.d("Tontine", "not found");
                    }
                }
            });
        }
        textNoAnnonce = (TextView) findViewById(R.id.textNoAnnonce);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_broadcast);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.app_color, R.color.app_color, R.color.app_color,R.color.app_color);
        swipeLayout.setEnabled(true);
        loadAnnonce();
        //onRefresh();
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
                Annonce annonce = annonces.get(position);
                if(annonce.getType().equals("inscription")){
                    Intent i = new Intent(activity,InscriptionActivity.class);
                    i.putExtra("TONTINE_ID", tontineId);
                    i.putExtra("ANNONCE_ID", annonce.getObjectId());
                    activity.startActivity(i);
                }

            }
        });
        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                deleteAnnonce(annonces.get(position));
                return true;
            }
        });*/
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                // TODO Auto-generated method stub

                listView.setItemChecked(position, !adapter.isPositionChecked(position));
                return false;
            }
        });
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private int nr = 0;
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
                if (checked) {
                    nr++;
                    adapter.setNewSelection(position, checked);
                } else {
                    nr--;
                    adapter.removeSelection(position);
                }
                actionMode.setTitle(nr + " selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                nr = 0;
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.item_delete:
                        nr = 0;
                        adapter.clearSelection();;
                        actionMode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
               adapter.clearSelection();
            }
        });
       }
public void deleteAnnonce(final Annonce annonce){
final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setMessage("Supprimer l'annonce ?");
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
                    //Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    snakBar(false, "noInternet");
                } else {
                    AnnonceAdapter annonceAdapter = new AnnonceAdapter(activity, context, annonces, tontineId);
                    annonceAdapter.remove(annonce);
                }
                materialDialog.show();
            }
        });
}
    public void snakBar(boolean status, String context){
        if(status==true && context==null) {
            snackBar = new SnackBar(this, "Une annonce supprimée !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else if(status==false && context==null) {
            snackBar = new SnackBar(this, "Erreur lors de la suppression !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else{
            snackBar = new SnackBar(this, getResources().getString(R.string.no_internet), "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }
        snackBar.show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_annonce, menu);
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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this, MainTontineActivity.class);
        if(tontineId != null) {
            i.putExtra("TONTINE_ID", tontineId);
            i.putExtra("NOM", nom);
            startActivity(i);
            finish();
        }else{
            snakBar(false, "noInternet");
        }
    }
    public void startAddAnnonce(){

        Intent i = new Intent(this,CreerAnnonceActivity.class);
        if(tontineId != null) {
            i.putExtra("TONTINE_ID", tontineId);
            startActivity(i);
        }else{
            snakBar(false, "noInternet");
        }
    }
    public void loadAnnonce(){
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            //Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            snakBar(false, "noInternet");
            swipeLayout.setRefreshing(false);

        } else {
            ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
            tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if(e==null){
                        ParseQuery<Annonce> annonceParseQuery = ParseQuery.getQuery(Annonce.class);
                        annonceParseQuery.whereEqualTo("tontine", tontine);
                        annonceParseQuery.findInBackground(new FindCallback<Annonce>() {
                            @Override
                            public void done(List<Annonce> annonceList, ParseException e) {
                                if(e==null){
                                    annonces = annonceList;
                                    if(annonces.size() == 0){
                                        progressWheel.setVisibility(View.GONE);
                                        textNoAnnonce.setVisibility(View.VISIBLE);
                                        AnnonceAdapter sa = new AnnonceAdapter(activity, context, annonces, tontineId);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    }else{
                                        progressWheel.setVisibility(View.GONE);
                                        textNoAnnonce.setVisibility(View.GONE);
                                       listView.setAdapter(new AnnonceAdapter(activity, context, annonces, tontineId));
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    progressWheel.setVisibility(View.GONE);
                                    textNoAnnonce.setVisibility(View.VISIBLE);
                                    Log.d("Annonces", "not found with error : " + e.getMessage());
                                    swipeLayout.setRefreshing(false);
                                }
                            }
                        });
                    }else{
                        progressWheel.setVisibility(View.GONE);
                        textNoAnnonce.setVisibility(View.VISIBLE);
                        Log.d("tontine", "Not found with error : " + e.getMessage());
                    }
                }
            });

        }
    }
    @Override
    public void onRefresh() {
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            //Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            snakBar(false, "noInternet");
            swipeLayout.setRefreshing(false);

        } else {
            ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
            tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if(e==null){
                        ParseQuery<Annonce> annonceParseQuery = ParseQuery.getQuery(Annonce.class);
                        annonceParseQuery.whereEqualTo("tontine", tontine);
                        annonceParseQuery.findInBackground(new FindCallback<Annonce>() {
                            @Override
                            public void done(List<Annonce> annonceList, ParseException e) {
                                if(e==null){
                                    annonces = annonceList;
                                    listView.setAdapter(new AnnonceAdapter(activity, context, annonces, tontineId));
                                    if(annonces.size() == 0){
                                        AnnonceAdapter sa = new AnnonceAdapter(activity, context, annonces, tontineId);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    Log.d("Annonces", "not found with error : " + e.getMessage());
                                    swipeLayout.setRefreshing(false);
                                }
                            }
                        });
                    }else{
                        Log.d("tontine", "Not found with error : " + e.getMessage());
                    }
                }
            });

        }
    }
}
