package fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.CreerTontine1;
import com.parse.app.InfoTontineActivity;
import com.parse.app.MainTontineActivity;
import com.parse.app.R;
import com.parse.app.SessionActivity;
import com.parse.app.TontineActivity;
import com.parse.app.adapter.MesTontinesAdapter;
import com.parse.app.model.Compte;
import com.parse.app.model.Cotisation;
import com.parse.app.model.Membre;
import com.parse.app.model.Presence;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

public class MesTontinesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private List<Tontine> mItems = new ArrayList<Tontine>();
    private ListView listview;
    private MesTontinesAdapter mAdapter;
    private LinearLayout infoLayout;
    private MesTontinesAdapter adapter;
    private ProgressBar loadingData;
    private Context context;
    private ButtonFloat addButton;
    //private FloatingActionButton fab;
    private SwipeRefreshLayout swipeLayout;
    private boolean reachedTop = true;
    private ParseUser user;
    private SnackBar snackBar;
    private ProgressWheel progressWheel;
    private TextView textNoMesTontine;

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
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_mestontines,container, false);
        listview = (ListView) rootView.findViewById(R.id.listviewTontine);
        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        adapter = new MesTontinesAdapter(getActivity().getApplicationContext(), mItems);
        /*addButton = (ButtonFloat)rootView.findViewById(R.id.addBtn);
        addButton.setDrawableIcon(getResources().getDrawable(R.drawable.add_fab));*/
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        listview.setAdapter(adapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 startAddGroup();
            }
        });
        fab.attachToListView(listview, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                Log.d("ListViewFragment", "onScrollDown()");
                fab.show();

            }

            @Override
            public void onScrollUp() {
                Log.d("ListViewFragment", "onScrollUp()");
                fab.hide();
            }
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("ListViewFragment", "onScrollStateChanged()");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("ListViewFragment", "onScroll()");
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = listview.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:
                        reachedTop = true;
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem){
                    View v =  listview.getChildAt(totalItemCount-1);
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

        user = ParseUser.getCurrentUser();
        ScrollView emptyView = (ScrollView) rootView.findViewById(R.id.emptyList);
        listview.setEmptyView(emptyView);
        listview.setDividerHeight(0);
        textNoMesTontine = (TextView) rootView.findViewById(R.id.textNoMesTontine);
        listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_broadcast);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.app_color, R.color.app_color, R.color.app_color);
        swipeLayout.setEnabled(true);
        //onRefresh();
        loadMesTontines();

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        //Toast.makeText(getActivity().getApplicationContext(), "FLING !", Toast.LENGTH_SHORT).show();
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
                                swipeLayout.setEnabled(false);
                            } else if(e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
                                //Toast.makeText(getActivity().getApplicationContext(), "Up to Down !", Toast.LENGTH_LONG).show();
                                swipeLayout.setEnabled(true);
                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        listview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return gesture.onTouchEvent(event);
            }

        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Tontine tontine = mItems.get(position);
                Intent i = new Intent(getActivity().getApplicationContext(), MainTontineActivity.class);
                if(tontine.getObjectId()!=null) {
                    i.putExtra("TONTINE_ID", tontine.getObjectId());
                    i.putExtra("NOM", tontine.getNom());
                    if (android.os.Build.VERSION.SDK_INT >= 16) {
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(
                                        getActivity().getApplicationContext(),
                                        R.anim.anim_left_right,
                                        R.anim.anim_right_left).toBundle();
                        getActivity().startActivity(i, bndlanimation);
                    } else {
                        getActivity().startActivity(i);
                    }
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"Oups ... internet error, please try egain !", Toast.LENGTH_LONG).show();
                }

            }
        });

        return rootView;
    }

    public void startAddGroup() {
        Intent i = new Intent(getActivity().getApplicationContext(), CreerTontine1.class);
        startActivity(i);
    }
    public void pinDataInLocalDataStore() {
        if (NetworkUtil.getConnectivityStatus(getActivity().getApplicationContext()) == 0) {
            //Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            snackBar();
            swipeLayout.setRefreshing(false);
            Log.d("Pinning in LocalDataStore"," Fail");
        } else {
            Log.d("Pinning in LocalDataStore"," start");
            ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
            tontineParseQuery.findInBackground(new FindCallback<Tontine>() {
                @Override
                public void done(List<Tontine> tontines, ParseException e) {
                    if(e==null && tontines.size()>0){
                        Log.d("Pinning in LocalDataStore"," tontines");
                        for(int i =0; i < tontines.size(); i++){
                            tontines.get(i).pinInBackground();
                        }
                    }else{
                        Log.d("Fail to pin in LocalDataStore"," tontines");
                    }
                }
            });
            ParseQuery<Session> sessionParseQuery = ParseQuery.getQuery(Session.class);
            sessionParseQuery.findInBackground(new FindCallback<Session>() {
                @Override
                public void done(List<Session> sessions, ParseException e) {
                    if(e==null && sessions.size()>0){
                        Log.d("Pinning in LocalDataStore"," sessions");
                        for(int i =0; i < sessions.size(); i++){
                            sessions.get(i).pinInBackground();
                        }
                    }else{
                        Log.d("Fail to pin in LocalDataStore"," sessions");
                    }
                }
            });
            ParseQuery<Compte> compteParseQuery = ParseQuery.getQuery(Compte.class);
            compteParseQuery.findInBackground(new FindCallback<Compte>() {
                @Override
                public void done(List<Compte> comptes, ParseException e) {
                    if(e==null && comptes.size()>0){
                        Log.d("Pinning in LocalDataStore"," comptes");
                        for(int i =0; i < comptes.size(); i++){
                            comptes.get(i).pinInBackground();
                        }
                    }else{
                        Log.d("Fail to pin in LocalDataStore"," comptes");
                    }
                }
            });
            ParseQuery<Cotisation> cotisationParseQuery = ParseQuery.getQuery(Cotisation.class);
            cotisationParseQuery.findInBackground(new FindCallback<Cotisation>() {
                @Override
                public void done(List<Cotisation> cotisations, ParseException e) {
                    if(e==null && cotisations.size()>0){
                        Log.d("Pinning in LocalDataStore"," cotisations");
                        for(int i =0; i < cotisations.size(); i++){
                            cotisations.get(i).pinInBackground();
                        }
                    }else{
                        Log.d("Fail to pin in LocalDataStore"," cotisations");
                    }
                }
            });
            ParseQuery<Presence> presenceParseQuery = ParseQuery.getQuery(Presence.class);
            presenceParseQuery.findInBackground(new FindCallback<Presence>() {
                @Override
                public void done(List<Presence> presences, ParseException e) {
                    if(e==null && presences.size()>0){
                        Log.d("Pinning in LocalDataStore"," presences");
                        for(int i =0; i < presences.size(); i++){
                            presences.get(i).pinInBackground();
                        }
                    }else{
                        Log.d("Fail to pin in LocalDataStore"," presences");
                    }
                }
            });
            Log.d("Pinning in LocalDataStore"," End");
        }
    }
    public void snackBar(){
        snackBar = new SnackBar(getActivity(), "Erreur réseau !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        snackBar.show();
    }
    public void loadMesTontinesFromLocalDataStore(){
        Log.d("Loading mesTontines", " From loacalDataStore");
        ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
        membreParseQuery.whereEqualTo("adherant",user);
        membreParseQuery.fromLocalDatastore();
        membreParseQuery.findInBackground(new FindCallback<Membre>() {
            @Override
            public void done(List<Membre> membres, ParseException e) {
                if(e==null) {
                    final List<Tontine> membreTontines = new ArrayList<Tontine>();
                    for (Membre membre : membres) {
                        membreTontines.add(membre.getTontine());
                    }
                    mItems = membreTontines;
                    if(mItems.size()>0) {
                        textNoMesTontine.setVisibility(View.GONE);
                        progressWheel.setVisibility(View.GONE);
                        listview.setAdapter(new MesTontinesAdapter(getActivity().getApplicationContext(), mItems));
                    }
                    else if (mItems.size()==0) {
                        MesTontinesAdapter ta = new MesTontinesAdapter(getActivity().getApplicationContext(), mItems);
                        ta.clear();
                        progressWheel.setVisibility(View.GONE);
                        listview.setAdapter(ta);
                        ta.notifyDataSetChanged();
                        System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());
                        textNoMesTontine.setVisibility(View.VISIBLE);
                    }
                    swipeLayout.setRefreshing(false);
                }else{
                    Log.d("Membre","not found");
                    progressWheel.setVisibility(View.GONE);
                    textNoMesTontine.setVisibility(View.VISIBLE);
                    swipeLayout.setRefreshing(false);
                }

            }
        });
    }
    public void loadMesTontines(){
        if (NetworkUtil.getConnectivityStatus(getActivity().getApplicationContext()) == 0) {
            //Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);
            snackBar();
            //loadMesTontinesFromLocalDataStore();
        } else {
            pinDataInLocalDataStore();
            Log.d("Loading mesTontines", " From Parse Server");
            ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
            membreParseQuery.whereEqualTo("adherant",user);
            membreParseQuery.findInBackground(new FindCallback<Membre>() {
                @Override
                public void done(List<Membre> membres, ParseException e) {
                    if(e==null) {
                        final List<Tontine> membreTontines = new ArrayList<Tontine>();
                        for (Membre membre : membres) {
                            membreTontines.add(membre.getTontine());
                        }
                        mItems = membreTontines;
                        if(mItems.size()>0) {
                            textNoMesTontine.setVisibility(View.GONE);
                            progressWheel.setVisibility(View.GONE);
                            listview.setAdapter(new MesTontinesAdapter(getActivity().getApplicationContext(), mItems));
                        }
                        else if (mItems.size()==0) {
                            MesTontinesAdapter ta = new MesTontinesAdapter(getActivity().getApplicationContext(), mItems);
                            ta.clear();
                            progressWheel.setVisibility(View.GONE);
                            listview.setAdapter(ta);
                            ta.notifyDataSetChanged();
                            System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());
                            textNoMesTontine.setVisibility(View.VISIBLE);
                        }
                        swipeLayout.setRefreshing(false);
                    }else{
                        Log.d("Membre","not found");
                        progressWheel.setVisibility(View.GONE);
                        textNoMesTontine.setVisibility(View.VISIBLE);
                        swipeLayout.setRefreshing(false);
                    }

                }
            });

        }
    }
    @Override
    public void onRefresh() {
        if (NetworkUtil.getConnectivityStatus(getActivity().getApplicationContext()) == 0) {
            //Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);
            snackBar();
            //loadMesTontinesFromLocalDataStore();
        } else {
           // pinDataInLocalDataStore();
            Log.d("Refreshing mesTontines", " From Parse Server");
            ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
            membreParseQuery.whereEqualTo("adherant",user);
            membreParseQuery.findInBackground(new FindCallback<Membre>() {
                @Override
                public void done(List<Membre> membres, ParseException e) {
                    if(e==null) {
                        final List<Tontine> membreTontines = new ArrayList<Tontine>();
                        for (Membre membre : membres) {
                            membreTontines.add(membre.getTontine());
                        }
                        mItems = membreTontines;
                        if(mItems.size()>0) {
                            listview.setAdapter(new MesTontinesAdapter(getActivity().getApplicationContext(), mItems));
                        }
                        else if (mItems.size()==0) {
                            MesTontinesAdapter ta = new MesTontinesAdapter(getActivity().getApplicationContext(), mItems);
                            ta.clear();
                            listview.setAdapter(ta);
                            ta.notifyDataSetChanged();
                            System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());
                        }
                        swipeLayout.setRefreshing(false);
                    }else{
                        Log.d("Membre","not found");
                        swipeLayout.setRefreshing(false);
                    }

                }
            });

        }
    }




}
