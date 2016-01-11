package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
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
import com.parse.app.utilities.UIUtils;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class MembresFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
	private List<Membre> membres = new ArrayList<Membre>();
    private ListView listView;
    private MembreListAdapter adapter;
    private int mPreviousVisibleItem;
    private FloatingActionButton fab1;
    private MaterialDialog materialDialog;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fabMenu;
    private AlertDialog alertDialogSupp,alertDialogEncours;
    private SwipeRefreshLayout swipeLayout;
    private boolean reachedTop = true;
    private String tontineId;
    private Activity a;
    private TextView textNoMembre, suppBtn;
    private String nom;
    private Context context;
    public static final int TYPE_NOT_CONNECTED = 0;
    private ProgressWheel progressWheel;
    private ParseUser thisuser;
    private Tontine tontine;
    private Session tontineSession;
    private FloatingActionMenu menu;
    private SnackBar snackBar;
    private FrameLayout frameLayout;
    private TextView nbMembre;
    private ParseUser user;
    private ParseUser president;

    public MembresFragment(String tontineId, String nom){

        this.tontineId = tontineId;
        this.nom = nom;
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
    public void loadMembres(){
        if (NetworkUtil.getConnectivityStatus(getActivity()) == TYPE_NOT_CONNECTED) {
            //snackBar();
            //swipeLayout.setRefreshing(false);
            loadMembreFromLocalDataStore();

        } else {
            Log.d("Loding membres","From parse server");
            ParseQuery<Tontine> tontineQuery = ParseQuery.getQuery(Tontine.class);
            tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {

                    if ((e == null) && (tontine != null)) {
                        if(thisuser.equals(tontine.getPresident())){
                            suppBtn.setVisibility(View.VISIBLE);
                        }else{
                            suppBtn.setVisibility(View.GONE);
                        }
                        ParseQuery<Membre> membreParseQuery = (new Membre()).getQuery();
                        membreParseQuery.whereEqualTo("tontine",tontine);
                        membreParseQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(List<Membre> membres, ParseException e) {
                                if(e==null && membres.size()>0){
                                    frameLayout.setVisibility(View.VISIBLE);
                                    nbMembre.setText(membres.size()+" membres");
                                }else{
                                    frameLayout.setVisibility(View.GONE);
                                    Log.i("Membres","Empty");
                                }
                            }
                        });
                        president = tontine.getPresident();
                        ParseQuery<Membre> membreQuery = (new Membre()).getQuery();
                        membreQuery.whereEqualTo("tontine", tontine);
                        membreQuery.fromLocalDatastore();
                        membreQuery.orderByAscending("date_inscription");
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
                                        if(president.equals(thisuser)){
                                            menu.setVisibility(View.VISIBLE);
                                        }else{
                                            menu.setVisibility(View.GONE);
                                        }

                                    } else {
                                        progressWheel.setVisibility(View.GONE);
                                        textNoMembre.setVisibility(View.GONE);
                                        listView.setAdapter(new MembreListAdapter(tontineId, context,a, membres));
                                        System.out.println("Nombre d'element dans l'adapter: " + listView.getAdapter().getCount());
                                        if(president.equals(thisuser)){
                                            menu.setVisibility(View.VISIBLE);
                                        }else{
                                            menu.setVisibility(View.GONE);
                                        }

                                    }
                                    swipeLayout.setRefreshing(false);
                                } else {
                                    Log.d("Membre", "error " + e.getMessage());
                                    progressWheel.setVisibility(View.GONE);
                                    textNoMembre.setVisibility(View.VISIBLE);
                                    swipeLayout.setRefreshing(false);
                                    System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    if(president.equals(thisuser)){
                                        menu.setVisibility(View.VISIBLE);
                                    }else{
                                        menu.setVisibility(View.GONE);
                                    }

                                }
                            }
                        });
                    } else {
                        progressWheel.setVisibility(View.GONE);
                        textNoMembre.setVisibility(View.VISIBLE);
                        Log.d("Tontine", "error " + e.getMessage());
                        System.out.println("Nombre d'element dans l'adapter: " + listView.getAdapter().getCount());
                        if(president.equals(thisuser)){
                            menu.setVisibility(View.VISIBLE);
                        }else{
                            menu.setVisibility(View.GONE);
                        }

                    }
                }
            });

        }
    }
    @Override
    public void onRefresh() {
        if (NetworkUtil.getConnectivityStatus(getActivity()) == TYPE_NOT_CONNECTED) {
            snackBar();
            swipeLayout.setRefreshing(false);

        } else {
            ParseQuery<Tontine> tontineQuery = (new Tontine()).getQuery();
            tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {

                    if((e == null) && (tontine != null)){
                        if(thisuser.equals(tontine.getPresident())){
                            suppBtn.setVisibility(View.VISIBLE);
                        }else{
                            suppBtn.setVisibility(View.GONE);
                        }
                        ParseQuery<Membre> membreParseQuery = (new Membre()).getQuery();
                        membreParseQuery.whereEqualTo("tontine",tontine);
                        membreParseQuery.findInBackground(new FindCallback<Membre>() {
                            @Override
                            public void done(List<Membre> membres, ParseException e) {
                                if(e==null && membres.size()>0){
                                    frameLayout.setVisibility(View.VISIBLE);
                                    nbMembre.setText(membres.size()+" membres");
                                }else{
                                    frameLayout.setVisibility(View.GONE);
                                    Log.i("Membres","Empty");
                                }
                            }
                        });
                        president = tontine.getPresident();
                        ParseQuery<Membre> membreQuery = (new Membre()).getQuery();
                        membreQuery.whereEqualTo("tontine", tontine);
                        membreQuery.fromLocalDatastore();
                        membreQuery.orderByAscending("date_inscription");
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
                                        if(president.equals(thisuser)){
                                            menu.setVisibility(View.VISIBLE);
                                        }else{
                                            menu.setVisibility(View.GONE);
                                        }
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    swipeLayout.setRefreshing(false);
                                    if(president.equals(thisuser)){
                                        menu.setVisibility(View.VISIBLE);
                                    }else{
                                        menu.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                    }else{
                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                        if(president.equals(thisuser)){
                            menu.setVisibility(View.VISIBLE);
                        }else{
                            menu.setVisibility(View.GONE);
                        }
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
        alertDialogEncours = UIUtils.getProgressDialog(getActivity(), R.layout.progress_dialog_encours);
        alertDialogSupp = UIUtils.getProgressDialog(getActivity(), R.layout.progress_dialog_suppression);
        thisuser = ParseUser.getCurrentUser();
        a = getActivity();
        listView = (ListView) rootView.findViewById(R.id.listviewMembre);
        suppBtn = (TextView)rootView.findViewById(R.id.suppMembresBtn);
        suppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllMembre(tontineId);

            }
        });
        registerForContextMenu(listView);
        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        frameLayout = (FrameLayout)rootView.findViewById(R.id.layoutNbMembre);
        nbMembre = (TextView)rootView.findViewById(R.id.nb_membre);
        frameLayout.setVisibility(View.GONE);
        menu = (FloatingActionMenu) rootView.findViewById(R.id.menu);

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
            public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long id) {
                final ParseUser user = membres.get(position).getAdherant();
                if(thisuser.equals(president)){
                    PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            // TODO Auto-generated method stub

                            switch (menuItem.getItemId()) {
                                case R.id.action_set_function:
                                    nommerMembre(user, view);
                                    return true;
                                case R.id.action_delete:
                                    deleteMembre(user);
                                    return true;
                                case R.id.action_profil:
                                    startProfilActivity(user, position, membres, tontineId);
                                    return true;
                                default:
                                    return getActivity().onContextItemSelected(menuItem);
                            }

                        }
                    });
                    popupMenu.show();
                }else{
                    startProfilActivity(user, position, membres, tontineId);
                }

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

            switch (v.getId()) {
                case R.id.fab_search_membre:
                    break;
                case R.id.fab_message:
                    startSendEmail(tontineId);
                    break;
                case R.id.fab_add_membre:
                    startAddMembre(tontineId,nom);
                    break;
            }

        }
    };

    public void startSendEmail(String tontineId){
        Intent i = new Intent(getActivity(), CreerAnnonceActivity.class);
        i.putExtra("TONTINE_ID",tontineId);
        startActivity(i);
    }
    public void startAddMembre(String tontineId,String nom){
        Intent i = new Intent(getActivity(), AjoutMembre.class);
        i.putExtra("TONTINE_ID",tontineId);
        i.putExtra("NOM", nom);
        startActivity(i);
    }
    public void loadMembreFromLocalDataStore(){
        Log.d("Loding membres","From localDataStore");
        ParseQuery<Tontine> tontineQuery = (new Tontine()).getQuery();
        tontineQuery.fromLocalDatastore();
        tontineQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
            @Override
            public void done(Tontine tontine, ParseException e) {

                if ((e == null) && (tontine != null)) {
                    if(thisuser.equals(tontine.getPresident())){
                        suppBtn.setVisibility(View.VISIBLE);
                    }else{
                        suppBtn.setVisibility(View.GONE);
                    }
                    ParseQuery<Membre> membreParseQuery = (new Membre()).getQuery();
                    membreParseQuery.whereEqualTo("tontine",tontine);
                    membreParseQuery.fromLocalDatastore();
                    membreParseQuery.findInBackground(new FindCallback<Membre>() {
                        @Override
                        public void done(List<Membre> membres, ParseException e) {
                            if(e==null && membres.size()>0){
                                frameLayout.setVisibility(View.VISIBLE);
                                nbMembre.setText(membres.size()+" membres");
                            }else{
                                frameLayout.setVisibility(View.GONE);
                            }
                        }
                    });
                    ParseQuery<Membre> membreQuery = (new Membre()).getQuery();
                    membreQuery.whereEqualTo("tontine", tontine);
                    membreQuery.fromLocalDatastore();
                    membreQuery.orderByAscending("date_inscription");
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
    public void startProfilActivity(ParseUser user, int position, List<Membre> membres, String tontineId){
        Intent i = new Intent(getActivity(), MembreProfile.class);
        i.putExtra("NOM", user.getString("nom").concat(" ").concat(user.getString("prenom")));
        i.putExtra("TEL", user.getString("phoneNumber"));
        i.putExtra("PROFESSION", user.getString("profession"));
        i.putExtra("PSEUDO", user.getUsername());
        i.putExtra("EMAIL", user.getEmail());
        i.putExtra("NAME",nom);
        i.putExtra("TONTINE_ID",tontineId);
        i.putExtra("POSTE", membres.get(position).getString("fonction"));
        startActivity(i);
    }
    public void nommerMembre(final ParseUser user, View view){
        PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_function, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // TODO Auto-generated method stub

                switch (menuItem.getItemId()) {
                    case R.id.action_vice_president:
                        setFunction(user, "Vice président");
                        return true;
                    case R.id.action_secretaire:
                        setFunction(user, "Sécrétaire");
                        return true;
                    case R.id.action_tresorier:
                        setFunction(user, "Trésorier");
                        return true;
                    default:
                        return getActivity().onContextItemSelected(menuItem);
                }

            }
        });
        popupMenu.show();

    }

    public void setFunction(final ParseUser user, final String function){
        if (NetworkUtil.getConnectivityStatus(getActivity()) == TYPE_NOT_CONNECTED) {
            snackBar();
        } else {
            alertDialogEncours.show();
            ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
            tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if(e==null){
                       ParseQuery<Membre> membreParseQuery = (new Membre()).getQuery();
                        membreParseQuery.whereEqualTo("tontine",tontine);
                        membreParseQuery.whereEqualTo("adherant", user);
                        membreParseQuery.getFirstInBackground(new GetCallback<Membre>() {
                            @Override
                            public void done(Membre membre, ParseException e) {
                                if(e==null){
                                    membre.setFonction(function);
                                    membre.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null){
                                                alertDialogEncours.dismiss();
                                                Toast.makeText(getActivity().getApplicationContext(),"Ok",Toast.LENGTH_LONG).show();
                                            }else{
                                                alertDialogEncours.dismiss();
                                                Toast.makeText(getActivity().getApplicationContext(),"Erreur, veuillez rééssayer svp!",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }else{
                                    alertDialogEncours.dismiss();
                                    Log.d("Membre","not found");
                                }
                            }
                        });
                    }else{
                        alertDialogEncours.dismiss();
                        Log.d("Tontine","not found");
                    }
                }
            });
        }
    }
    public void deleteAllMembre(final String tontineId){
        materialDialog = new MaterialDialog(getActivity());
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setTitle("Alerte : ");
        materialDialog.setMessage("Supprimer tous les membres ?");
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
                if (NetworkUtil.getConnectivityStatus(getActivity()) == TYPE_NOT_CONNECTED) {
                    snackBar();
                } else {
                    alertDialogSupp.show();
                    ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
                    tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                        @Override
                        public void done(Tontine tontine, ParseException e) {
                            if(e==null){
                                ParseQuery<Membre> membreParseQuery = (new Membre()).getQuery();
                                membreParseQuery.whereEqualTo("tontine",tontine);
                                membreParseQuery.whereNotEqualTo("adherant",thisuser);
                                membreParseQuery.findInBackground(new FindCallback<Membre>() {
                                    @Override
                                    public void done(List<Membre> m, ParseException e) {
                                        Log.i("membres size",""+m.size());
                                        if(e==null && m.size()>0){
                                            MembreListAdapter membreAdapter = new MembreListAdapter(tontineId,getActivity().getApplicationContext(),getActivity(),m);
                                            membreAdapter.clear();
                                            alertDialogSupp.dismiss();
                                            onRefresh();
                                        }else{
                                            alertDialogSupp.dismiss();
                                            Log.i("Membres","Not sufficient");
                                            Toast.makeText(getActivity().getApplicationContext(),"Impossible de supprimer le president !",Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });
                            }else{
                                alertDialogSupp.dismiss();
                                Log.i("Tontine","Not found");
                            }
                        }
                    });
                }
            }
        });
        materialDialog.show();

    }
    public void deleteMembre(final ParseUser user){
        if (NetworkUtil.getConnectivityStatus(getActivity()) == TYPE_NOT_CONNECTED) {
            snackBar();
        } else {
            materialDialog = new MaterialDialog(getActivity());
            materialDialog.setCanceledOnTouchOutside(true);
            materialDialog.setTitle("Supprimer le membre?");
            materialDialog.setMessage(user.getString("nom") +" "+ user.getString("prenom"));
            materialDialog.setNegativeButton("Oui", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    materialDialog.dismiss();
                    alertDialogSupp.show();
                    ParseQuery<Tontine> tontineParseQuery = (new Tontine()).getQuery();
                    tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                        @Override
                        public void done(Tontine tontine, ParseException e) {
                            if (e == null) {
                                ParseQuery<Membre> membreParseQuery = (new Membre()).getQuery();
                                membreParseQuery.whereEqualTo("adherant", user);
                                membreParseQuery.whereEqualTo("tontine", tontine);
                                membreParseQuery.getFirstInBackground(new GetCallback<Membre>() {
                                    @Override
                                    public void done(Membre membre, ParseException e) {
                                        if (e == null) {
                                            membre.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        alertDialogSupp.dismiss();
                                                        Toast.makeText(getActivity(),"Membre supprimé !",Toast.LENGTH_LONG).show();
                                                        adapter = new MembreListAdapter(tontineId, getActivity().getApplicationContext(),a, membres);
                                                        adapter.notifyDataSetChanged();
                                                        onRefresh();
                                                    }else{
                                                        alertDialogSupp.dismiss();
                                                        Toast.makeText(getActivity(),"Erreur de suppression !",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }else{
                                            alertDialogSupp.dismiss();
                                            Toast.makeText(getActivity(),"Membre introuvable !",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }else{
                                alertDialogSupp.dismiss();
                                Toast.makeText(getActivity(),"Tontine introuvable",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
            materialDialog.setPositiveButton("Non", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();

        }

    }

}
