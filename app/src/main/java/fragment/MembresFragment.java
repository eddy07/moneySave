package fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.melnykov.fab.ScrollDirectionListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.AjoutMembre;
import com.parse.app.CreerAnnonceActivity;
import com.parse.app.MainActivity;
import com.parse.app.MembreProfile;
import com.parse.app.R;
import com.parse.app.adapter.MembreAdapter;
import com.parse.app.adapter.MembreListAdapter;
import com.parse.app.model.Membre;
import com.parse.app.model.Tontine;
import com.parse.app.model.Presence;
import com.parse.app.model.Session;
import com.parse.app.utilities.NetworkUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

public class MembresFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
	private List<Membre> membres = new ArrayList<Membre>();
    private ListView listView;
    private MembreListAdapter adapter;
    private int mPreviousVisibleItem;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fabMenu;
    private SwipeRefreshLayout swipeLayout;
    private boolean reachedTop = true;
    private String tontineId;
    private Activity a;
    private TextView textNoMembre;
    private String nom;
    private Context context;
    public static final int TYPE_NOT_CONNECTED = 0;
    private ProgressWheel progressWheel;
    private ParseUser thisuser;
    private Tontine tontine;
    private Session tontineSession;
    private SnackBar snackBar;
    private ParseUser user;

    public MembresFragment(String tontineId){

        this.tontineId = tontineId;
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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }
    public void snackBar(){
        snackBar = new SnackBar(getActivity(),getResources().getString(R.string.no_internet), "Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }
    public void loadMembresFromLocalDataStore(){
        ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery("Tontine");
        tontineQuery.whereEqualTo("objectId", tontineId);
        tontineQuery.fromLocalDatastore();
        tontineQuery.getFirstInBackground(new GetCallback<Tontine>() {
            @Override
            public void done(Tontine tontine, ParseException e) {
                if((e == null) && (tontine != null)){
                    nom = tontine.getNom();
                    ParseQuery<Membre> membreQuery = ParseQuery.getQuery(Membre.class);
                    membreQuery.whereEqualTo("tontine", tontine);
                    membreQuery.orderByAscending("date_inscription");
                    membreQuery.fromLocalDatastore();
                    membreQuery.whereNotEqualTo("adherant", thisuser);
                    membreQuery.findInBackground(new FindCallback<Membre>() {
                        @Override
                        public void done(List<Membre> membreList, ParseException e) {
                            if(( e == null) && (membreList.size()>=0)){
                                membres = membreList;
                                if(membres.size() == 0){
                                    MembreListAdapter sa = new MembreListAdapter(tontineId, context,a, membres);
                                    sa.clear();
                                    listView.setAdapter(sa);
                                    sa.notifyDataSetChanged();
                                    System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    progressWheel.setVisibility(View.GONE);
                                    textNoMembre.setVisibility(View.VISIBLE);

                                }else{
                                    progressWheel.setVisibility(View.GONE);
                                    textNoMembre.setVisibility(View.GONE);
                                    listView.setAdapter(new MembreListAdapter(tontineId, context,a, membres));
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
    public void loadMembres(){
        if (NetworkUtil.getConnectivityStatus(getActivity()) == TYPE_NOT_CONNECTED) {
            //Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            snackBar();
            swipeLayout.setRefreshing(false);
            //loadMembresFromLocalDataStore();

        } else {
            //Toast.makeText(getActivity().getApplicationContext(), "TontineId = "+tontineId, Toast.LENGTH_SHORT).show();
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery(Tontine.class);
            tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {

                    if ((e == null) && (tontine != null)) {
                        ParseQuery<Membre> membreQuery = ParseQuery.getQuery(Membre.class);
                        membreQuery.whereEqualTo("tontine", tontine);
                        membreQuery.orderByAscending("date_inscription");
                        //membreQuery.whereNotEqualTo("adherant", thisuser);
                        membreQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(List<Membre> membreList, ParseException e) {
                                if ((e == null) && (membreList.size() >= 0)) {
                                    membres = membreList;
                                    if (membres.size() == 0) {
                                        MembreListAdapter sa = new MembreListAdapter(tontineId, context,a, membres);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: " + listView.getAdapter().getCount());
                                        progressWheel.setVisibility(View.GONE);
                                        textNoMembre.setVisibility(View.VISIBLE);

                                    } else {
                                        progressWheel.setVisibility(View.GONE);
                                        textNoMembre.setVisibility(View.GONE);
                                        listView.setAdapter(new MembreListAdapter(tontineId, context,a, membres));
                                        System.out.println("Nombre d'element dans l'adapter: " + listView.getAdapter().getCount());

                                    }
                                    swipeLayout.setRefreshing(false);
                                } else {
                                    Log.d("Membre", "error " + e.getMessage());
                                    progressWheel.setVisibility(View.GONE);
                                    textNoMembre.setVisibility(View.VISIBLE);
                                    swipeLayout.setRefreshing(false);
                                    System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());

                                }
                            }
                        });
                    } else {
                        progressWheel.setVisibility(View.GONE);
                        textNoMembre.setVisibility(View.VISIBLE);
                        Log.d("Tontine", "error " + e.getMessage());
                        System.out.println("Nombre d'element dans l'adapter: " + listView.getAdapter().getCount());

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
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery(Tontine.class);
            //tontineQuery.whereEqualTo("objectId", tontineId);
            tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {

                    if((e == null) && (tontine != null)){
                        ParseQuery<Membre> membreQuery = ParseQuery.getQuery(Membre.class);
                        membreQuery.whereEqualTo("tontine", tontine);
                        membreQuery.orderByAscending("date_inscription");
                        //membreQuery.whereNotEqualTo("adherant", thisuser);
                        membreQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(List<Membre> membreList, ParseException e) {
                                if(( e == null) && (membreList.size()>=0)){
                                    membres = membreList;
                                    listView.setAdapter(new MembreListAdapter(tontineId, context,a, membres));
                                    if(membres.size() == 0){
                                        MembreListAdapter sa = new MembreListAdapter(tontineId, context,a, membres);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    }
                                    System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    swipeLayout.setRefreshing(false);
                                }
                            }
                        });
                    }else{
                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_membres,container, false);
        context = getActivity().getApplicationContext();
        thisuser = ParseUser.getCurrentUser();
        a = getActivity();
        listView = (ListView) rootView.findViewById(R.id.listviewMembre);
        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        final FloatingActionMenu menu = (FloatingActionMenu) rootView.findViewById(R.id.menu);
        //final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab_search_membre);
        fab2 = (FloatingActionButton) rootView.findViewById(R.id.fab_message);
        fab3 = (FloatingActionButton) rootView.findViewById(R.id.fab_add_membre);
        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);
        adapter = new MembreListAdapter(tontineId, getActivity().getApplicationContext(),a, membres);
        listView.setAdapter(adapter);
        menu.setClosedOnTouchOutside(true);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > mPreviousVisibleItem) {
                    //fab.hide(true);
                } else if (firstVisibleItem < mPreviousVisibleItem) {
                    //fab.show(true);
                }
                mPreviousVisibleItem = firstVisibleItem;
            }
        });
        listView.setDividerHeight(0);
        adapter.notifyDataSetChanged();
        ScrollView emptyView = (ScrollView) rootView.findViewById(R.id.emptyList);
        listView.setEmptyView(emptyView);
        textNoMembre = (TextView) rootView.findViewById(R.id.textNoMembre);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_broadcast);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.app_color, R.color.app_color, R.color.app_color, R.color.app_color);
        swipeLayout.setEnabled(true);
        loadMembres();

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
                ParseUser user = membres.get(position).getAdherant();
                Intent i = new Intent(getActivity(), MembreProfile.class);
                i.putExtra("NOM", user.getString("nom").concat(" ").concat(user.getString("prenom")));
                i.putExtra("TEL", user.getString("phoneNumber"));
                i.putExtra("NAME",nom);
                i.putExtra("TONTINE_ID",tontineId);
                i.putExtra("PROFESSION", user.getString("profession"));
                i.putExtra("PSEUDO", user.getUsername());
                i.putExtra("POSTE", membres.get(position).getString("fonction"));
                i.putExtra("EMAIL", user.getEmail());
                startActivity(i);

            }
        });
        listView.setOnCreateContextMenuListener(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                    @Override
                    public void done(Tontine tontine, ParseException e) {
                        if(tontine.getPresident().equals(thisuser)){
                            registerForContextMenu(view);
                            PopupMenu popupMenu = new PopupMenu(getActivity().getBaseContext(), view);
                            popupMenu.getMenuInflater().inflate(R.menu.function_popup_menu, popupMenu.getMenu());
                            popupMenu.show();
                            Toast.makeText(getActivity().getBaseContext(), "OK long press",
                                    Toast.LENGTH_LONG).show();


                        }
                    }
                });
                return true;
            }
        });


        return rootView;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

        }
        return super.onOptionsItemSelected(menuItem);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = "";

            switch (v.getId()) {
                case R.id.fab_search_membre:
                    text = fab1.getLabelText();
                    break;
                case R.id.fab_message:
                    //text = fab2.getLabelText();
                    startSendEmail(tontineId);
                    break;
                case R.id.fab_add_membre:
                    text = fab3.getLabelText();
                    startAddMembre(tontineId);
                    break;
            }

        }
    };

    public void startSendEmail(String tontineId){
        Intent i = new Intent(getActivity(), CreerAnnonceActivity.class);
        i.putExtra("TONTINE_ID",tontineId);
        startActivity(i);
    }
    public void startAddMembre(String tontineId){
        Intent i = new Intent(getActivity(), AjoutMembre.class);
        i.putExtra("TONTINE_ID",tontineId);
        startActivity(i);
    }
}
