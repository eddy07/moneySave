package fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.R;
import com.parse.app.adapter.SessionAdapter;
import com.parse.app.model.Session;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;
import java.util.ArrayList;
import java.util.List;


public class SessionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private SessionAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private SnackBar snackBar;
    private List<Session> sessions = new ArrayList<Session>();
    public static final int TYPE_NOT_CONNECTED = 0;
    private String tontineId;
    private Context context;
    private ParseUser thisuser;
    private ProgressWheel progressWheel;
    private boolean reachedTop;
    private Tontine thistontine;
    private TextView contentLoad;
    private String date;


    public SessionsFragment(String tontineId, String date){

        this.tontineId = tontineId;
        this.date = date;
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

    public  void loadSessionsFromLocalDataStore(){
        ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
        tontineQuery.whereEqualTo("objectId", tontineId);
        tontineQuery.fromLocalDatastore();
        tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
            @Override
            public void done(Tontine tontine, ParseException e) {
                if((e == null) && (tontine != null)){
                    thistontine = tontine;
                    ParseQuery<Session> sessionQuery = ParseQuery.getQuery("Session");
                    sessionQuery.whereEqualTo("tontine", tontine);
                    sessionQuery.fromLocalDatastore();
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
                                Log.d("Session", "error " + e.getMessage());
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
    public void loadSessions(){
        if (NetworkUtil.getConnectivityStatus(getActivity()) == TYPE_NOT_CONNECTED) {
            //Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            //swipeLayout.setRefreshing(false);
            loadSessionsFromLocalDataStore();

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
                                    Log.d("Session", "error " + e.getMessage());
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
    }
    @Override
    public void onRefresh() {
        if (NetworkUtil.getConnectivityStatus(getActivity()) == TYPE_NOT_CONNECTED) {
            //Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            snackBar();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.activity_session,container, false);

        context = getActivity().getApplicationContext();
        thisuser = ParseUser.getCurrentUser();
        listView = (ListView) rootView.findViewById(R.id.listviewSession);
        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        adapter = new SessionAdapter(getActivity().getApplicationContext(), sessions);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        adapter.notifyDataSetChanged();
        ScrollView emptyView = (ScrollView) rootView.findViewById(R.id.emptyList);
        listView.setEmptyView(emptyView);
        thistontine = new Tontine();
        contentLoad = (TextView) rootView.findViewById(R.id.textNoSession);
        contentLoad.setVisibility(View.GONE);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_broadcast);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.app_color, R.color.app_color, R.color.app_color,R.color.app_color);
        swipeLayout.setEnabled(true);
        loadSessions();
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

        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return gesture.onTouchEvent(event);
            }

        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Session session = sessions.get(position);
            }
        });


        return rootView;
    }
}
