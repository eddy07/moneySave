package fragment;

import android.app.ActivityOptions;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.InfoTontineActivity;
import com.parse.app.R;
import com.parse.app.adapter.TontinesAdapter;
import com.parse.app.model.Membre;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TontinesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
	private List<Tontine> mItems = new ArrayList<Tontine>();
    private ListView listview;
    private TontinesAdapter mAdapter;
    private LinearLayout infoLayout;
    private TontinesAdapter adapter;
    private ProgressBar loadingData;
    private SwipeRefreshLayout swipeLayout;
    private boolean reachedTop = true;
    private ParseUser thisuser;
    private SnackBar snackBar;
    private ProgressWheel progressWheel;
    private TextView textNoTontine;

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    public void loadTontinesFromLocalDataStore(){
        Log.d("Loading Tontines", " From LocalDataStore");
        ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
        membreParseQuery.whereEqualTo("adherant", thisuser);
        membreParseQuery.fromLocalDatastore();
        membreParseQuery.findInBackground(new FindCallback<Membre>() {
            @Override
            public void done(List<Membre> membres, ParseException e) {

                if (e == null && membres.size() > 0) {
                    int nbMembre = membres.size();
                    String[] tontineId = {};

                    for (int i = 0; i < nbMembre; i++) {
                        tontineId[i] = membres.get(i).getTontine().getObjectId();
                    }
                    List<String> idList = new ArrayList<String>();
                    ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                    for (int i = 0; i < tontineId.length; i++) {
                        //tontineParseQuery.whereNotEqualTo("objectId", tontineId[i]);
                        idList.add(tontineId[i]);
                    }
                    tontineParseQuery.fromLocalDatastore();
                    tontineParseQuery.whereNotContainedIn("objectId", idList);
                    tontineParseQuery.findInBackground(new FindCallback<Tontine>() {
                        @Override
                        public void done(List<Tontine> tontines, ParseException e) {
                            if (e == null) {
                                mItems = tontines;
                                if (mItems.size() > 0) {
                                    progressWheel.setVisibility(View.GONE);
                                    textNoTontine.setVisibility(View.GONE);
                                    listview.setAdapter(new TontinesAdapter(getActivity().getApplicationContext(), mItems));
                                } else if (mItems.size() == 0) {
                                    TontinesAdapter ta = new TontinesAdapter(getActivity().getApplicationContext(), mItems);
                                    ta.clear();
                                    progressWheel.setVisibility(View.GONE);
                                    listview.setAdapter(ta);
                                    ta.notifyDataSetChanged();
                                    System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());
                                    textNoTontine.setVisibility(View.VISIBLE);
                                }
                                swipeLayout.setRefreshing(false);
                            } else {
                                Log.d("Tontine", "Not found");
                                swipeLayout.setRefreshing(false);
                                progressWheel.setVisibility(View.GONE);
                                textNoTontine.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }else{
                    Log.d("Membre", "not found");
                    swipeLayout.setRefreshing(false);
                    progressWheel.setVisibility(View.GONE);
                    textNoTontine.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void loadTontines(ParseUser user){
        if(NetworkUtil.getConnectivityStatus(getActivity().getApplicationContext())==0) {
            //Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            //loadTontinesFromLocalDataStore();
            swipeLayout.setRefreshing(false);
            snackBar();
        }else {
            Log.d("Loading Tontines", " From Parse Server");
            final ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
            membreParseQuery.whereEqualTo("adherant", user);
            membreParseQuery.findInBackground(new FindCallback<Membre>() {
                @Override
                public void done(List<Membre> membres, ParseException e) {

                    if (e == null && membres.size() > 0) {
                        List<String> tontineIdList = new ArrayList<String>();
                        int n = membres.size();
                        String[] list = {};
                        for (Membre membre : membres) {
                            tontineIdList.add(membre.getTontine().getObjectId());

                        }
                        /*for(int i=0; i<n; i++){
                            list[i] = membres.get(i).getTontine().getObjectId();
                        }*/

                    ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                    tontineParseQuery.whereNotContainedIn("objectId", Arrays.asList(tontineIdList));
                    tontineParseQuery.setLimit(20);
                    tontineParseQuery.findInBackground(new FindCallback<Tontine>() {
                        @Override
                        public void done(List<Tontine> tontines, ParseException e) {
                            if (e == null) {
                                mItems = tontines;
                                if (mItems.size() > 0) {
                                    progressWheel.setVisibility(View.GONE);
                                    textNoTontine.setVisibility(View.GONE);
                                    listview.setAdapter(new TontinesAdapter(getActivity().getApplicationContext(), mItems));
                                } else {
                                    progressWheel.setVisibility(View.GONE);
                                    TontinesAdapter ta = new TontinesAdapter(getActivity().getApplicationContext(), mItems);
                                    ta.clear();
                                    listview.setAdapter(ta);
                                    ta.notifyDataSetChanged();
                                    textNoTontine.setVisibility(View.VISIBLE);
                                    System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());

                                }
                                swipeLayout.setRefreshing(false);
                            } else {
                                Log.d("Tontine", "Not found");
                                swipeLayout.setRefreshing(false);
                                progressWheel.setVisibility(View.GONE);
                                textNoTontine.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    /*if(e==null) {

                        for (Membre membre : membres) {
                            ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                            tontineParseQuery.whereNotEqualTo("objectId",membre.getTontine().getObjectId());
                            tontineParseQuery.findInBackground(new FindCallback<Tontine>() {
                                @Override
                                public void done(List<Tontine> tontines, ParseException e) {
                                    if(e==null){
                                        mItems = tontines;
                                        if(mItems.size()>0) {
                                            progressWheel.setVisibility(View.GONE);
                                            textNoTontine.setVisibility(View.GONE);
                                            listview.setAdapter(new TontinesAdapter(getActivity().getApplicationContext(), mItems));
                                        }
                                        else if (mItems.size()==0) {
                                            TontinesAdapter ta = new TontinesAdapter(getActivity().getApplicationContext(), mItems);
                                            ta.clear();
                                            progressWheel.setVisibility(View.GONE);
                                            listview.setAdapter(ta);
                                            ta.notifyDataSetChanged();
                                            System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());
                                            textNoTontine.setVisibility(View.VISIBLE);
                                        }
                                        swipeLayout.setRefreshing(false);
                                    }else{
                                       Log.d("Tontine","Not found");
                                        progressWheel.setVisibility(View.GONE);
                                        textNoTontine.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }*/

                    }if (e == null && membres.size() == 0) {

                        ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                        tontineParseQuery.setLimit(20);
                        tontineParseQuery.findInBackground(new FindCallback<Tontine>() {
                            @Override
                            public void done(List<Tontine> tontines, ParseException e) {
                                if (e == null) {
                                    mItems = tontines;
                                    if (mItems.size() > 0) {
                                        progressWheel.setVisibility(View.GONE);
                                        textNoTontine.setVisibility(View.GONE);
                                        listview.setAdapter(new TontinesAdapter(getActivity().getApplicationContext(), mItems));
                                    } else {
                                        progressWheel.setVisibility(View.GONE);
                                        TontinesAdapter ta = new TontinesAdapter(getActivity().getApplicationContext(), mItems);
                                        ta.clear();
                                        listview.setAdapter(ta);
                                        ta.notifyDataSetChanged();
                                        textNoTontine.setVisibility(View.VISIBLE);
                                        System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());

                                    }
                                    swipeLayout.setRefreshing(false);
                                } else {
                                    Log.d("Tontine", "Not found");
                                    swipeLayout.setRefreshing(false);
                                    progressWheel.setVisibility(View.GONE);
                                    textNoTontine.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    } else{
                        Log.d("Membre", "not found");
                        swipeLayout.setRefreshing(false);
                        progressWheel.setVisibility(View.GONE);
                        textNoTontine.setVisibility(View.VISIBLE);
                    }

                }
            });
        }
        }

    @Override
    public void onRefresh() {
        if(NetworkUtil.getConnectivityStatus(getActivity().getApplicationContext())==0) {
            //Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);
            snackBar();

        }else {
            Log.d("Refreshing Tontines", " From Parse Server");
            ParseQuery<Membre> membreParseQuery = ParseQuery.getQuery(Membre.class);
            membreParseQuery.whereEqualTo("adherant", thisuser);
            membreParseQuery.findInBackground(new FindCallback<Membre>() {
                @Override
                public void done(List<Membre> membres, ParseException e) {

                    if (e == null && membres.size() > 0) {
                        List<String> tontineId = new ArrayList<String>();
                        List<String> tontineIdList = new ArrayList<String>();
                        int n = membres.size();
                        String[] list = null;
                        for (Membre membre : membres) {
                            tontineIdList.add(membre.getTontine().getObjectId());

                        }
                        /*for(int i=0; i<n; i++){
                            list[i] = membres.get(i).getTontine().getObjectId();
                        }*/

                        ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                        tontineParseQuery.whereNotContainedIn("objectId", Arrays.asList(tontineIdList));
                        tontineParseQuery.findInBackground(new FindCallback<Tontine>() {
                            @Override
                            public void done(List<Tontine> tontines, ParseException e) {
                                if(e==null){
                                    mItems = tontines;
                                    if(mItems.size()>0) {
                                        listview.setAdapter(new TontinesAdapter(getActivity().getApplicationContext(), mItems));
                                    }
                                    else if (mItems.size()==0) {
                                        TontinesAdapter ta = new TontinesAdapter(getActivity().getApplicationContext(), mItems);
                                        ta.clear();
                                        listview.setAdapter(ta);
                                        ta.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    Log.d("Tontine","Not found");
                                    swipeLayout.setRefreshing(false);
                                }
                            }
                        });
                        /*for (Membre membre : membres) {
                            ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                            tontineParseQuery.whereNotEqualTo("objectId",membre.getTontine().getObjectId());
                            tontineParseQuery.findInBackground(new FindCallback<Tontine>() {
                                @Override
                                public void done(List<Tontine> tontines, ParseException e) {
                                    if(e==null){
                                        mItems = tontines;
                                        if(mItems.size()>0) {
                                            listview.setAdapter(new TontinesAdapter(getActivity().getApplicationContext(), mItems));
                                        }
                                        else if (mItems.size()==0) {
                                            TontinesAdapter ta = new TontinesAdapter(getActivity().getApplicationContext(), mItems);
                                            ta.clear();
                                            listview.setAdapter(ta);
                                            ta.notifyDataSetChanged();
                                            System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());
                                        }
                                        swipeLayout.setRefreshing(false);
                                    }else{
                                        Log.d("Tontine","Not found");
                                        swipeLayout.setRefreshing(false);
                                    }
                                }
                            });
                        }*/

                    }else{
                        Log.d("Membre", "not found");
                        swipeLayout.setRefreshing(false);
                        progressWheel.setVisibility(View.GONE);
                        textNoTontine.setVisibility(View.VISIBLE);
                    }

                }
            });

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_tontines,container, false);
        listview = (ListView) rootView.findViewById(R.id.listviewTontine);
        adapter = new TontinesAdapter(getActivity().getApplicationContext(), mItems);
        listview.setAdapter(adapter);
        listview.setDividerHeight(0);
        thisuser = ParseUser.getCurrentUser();
        ScrollView emptyView = (ScrollView) rootView.findViewById(R.id.emptyList);
        listview.setEmptyView(emptyView);

        textNoTontine = (TextView) rootView.findViewById(R.id.textNoTontine);
        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_broadcast);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.app_color, R.color.app_color, R.color.app_color);
        swipeLayout.setEnabled(true);
        swipeLayout.setRefreshing(true);
        loadTontines(thisuser);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
                ParseUser president = tontine.getPresident();
                Intent i = new Intent(getActivity().getApplicationContext(), InfoTontineActivity.class);
                i.putExtra("TONTINE_ID", tontine.getObjectId());
                i.putExtra("NOM", tontine.getNom());
                i.putExtra("AMANDE", tontine.getAmande());
                i.putExtra("TYPE", tontine.getType());
                i.putExtra("JOUR", tontine.getJourCotisation());
                i.putExtra("DESCRIPTION", tontine.getDescription());
                i.putExtra("DATE", tontine.getCreation_date());
                //i.putExtra("PRESIDENT_NOM", president.getUsername());
                i.putExtra("PRESIDENT_ID", president.getObjectId());
                //i.putExtra("PRESIDENT_EMAIL", president.getEmail());

                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(
                                    getActivity().getApplicationContext(),
                                    R.anim.anim_left_right,
                                    R.anim.anim_right_left).toBundle();
                    getActivity().startActivity(i, bndlanimation);
                }else{
                    getActivity().startActivity(i);
                }
            }
        });

        return rootView;
    }

}
