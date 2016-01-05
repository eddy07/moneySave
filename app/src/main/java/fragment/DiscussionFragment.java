package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.app.R;
import com.parse.app.adapter.TontinesAdapter;
import com.parse.app.model.Tontine;

import java.util.ArrayList;
import java.util.List;

public class DiscussionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
	private List<Tontine> mItems = new ArrayList<Tontine>();
    private ListView listview;
    private TontinesAdapter mAdapter;
    private LinearLayout infoLayout;
    private TontinesAdapter adapter;
    private String tontineId;
    private ProgressBar loadingData;
    private SwipeRefreshLayout swipeLayout;
    private boolean reachedTop = true;

    public DiscussionFragment(String tontineId){

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
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_discussion,container, false);

        return rootView;
    }

}
