package com.parse.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.R;
import com.parse.app.model.Membre;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MembreListAdapter extends  ArrayAdapter<Membre>{
    private final Context context;
    private final Activity a;
    private List<Membre> values = new ArrayList<Membre>();
    private String tontineId;
    private int[] userIcons = {R.drawable.personblack, R.drawable.personbluedark, R.drawable.personbluelight,
            R.drawable.persongreen,R.drawable.persongreendark,R.drawable.personbluelight, R.drawable.personbluedark, R.drawable.personmarron,
            R.drawable.personorange, R.drawable.personpink, R.drawable.personpurple, R.drawable.personred, R.drawable.personreddark,
            R.drawable.personyellow};

    public MembreListAdapter(String tontineId, Context context,Activity a, List<Membre> values) {
        super(context, R.layout.membre_item, values);
        this.context = context;
        this.values = values;
        this.a = a;
        this.tontineId = tontineId;
    }
    @Override
    public void clear() {
        //values.clear();
        if(NetworkUtil.getConnectivityStatus(a.getApplicationContext())==0) {
        }else {
            for(Membre membre:values){
                membre.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Log.i("Membre","deleted");
                        }else{
                            Log.i("Membre","Fail to delete");
                        }
                    }
                });
            }
        }

        super.clear();

    }
	 
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            if(rowView == null){
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.membre_item, parent, false);
            }
            //TextView statu = (TextView) rowView.findViewById(R.id.statu);
            final TextView nom = (TextView) rowView.findViewById(R.id.nom);
            //layout.setVisibility(View.GONE);
            final TextView nbMembre = (TextView) rowView.findViewById(R.id.nb_membre);
            final TextView fonction = (TextView) rowView.findViewById(R.id.fonction);
            final ImageView user = (ImageView) rowView.findViewById(R.id.user);
            //final ImageButton option = (ImageButton) rowView.findViewById(R.id.option);
            final Membre membre = values.get(position);
            user.setImageDrawable(a.getResources().getDrawable(R.drawable.user));
            setFontNom(nom);
            setFontFonction(fonction);
            if(NetworkUtil.getConnectivityStatus(a.getApplicationContext())==0) {
                fechMembreFromLocalDataStore(membre,fonction,nom);
            }else {
                fechMembreFromParseServer(membre,fonction,nom);
            }

            return rowView;
	    }
    public void fechMembreFromParseServer(Membre membre, final TextView fonction, final TextView nom){

        membre.fetchIfNeededInBackground(new GetCallback<Membre>() {
            @Override
            public void done(Membre m, ParseException e) {
                ParseUser adherant = m.getAdherant();
                adherant.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(final ParseUser user, ParseException e) {
                        fonction.setVisibility(View.VISIBLE);
                        if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                            nom.setText("Moi");

                        } else {
                            nom.setText(user.getString("nom") + " " + user.getString("prenom"));
                        }
                        fonction.setText(user.getString("fonction"));
                    }
                });
            }
        });
    }
    public void fechMembreFromLocalDataStore(Membre membre, final TextView fonction, final TextView nom){
        membre.fetchFromLocalDatastoreInBackground(new GetCallback<Membre>() {
            @Override
            public void done(Membre m, ParseException e) {
                ParseUser adherant = m.getAdherant();
                adherant.fetchFromLocalDatastoreInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(final ParseUser user, ParseException e) {
                        fonction.setVisibility(View.VISIBLE);
                        if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                            nom.setText("Moi");

                        } else {
                            nom.setText(user.getString("nom") + " " + user.getString("prenom"));
                        }
                        fonction.setText(user.getString("fonction"));
                    }
                });
            }
        });
    }

    public void setFontFonction(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Italic.ttf");
        tv.setTypeface(tf);
    }
    public void setFontNom(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv.setTypeface(tf);
    }
    public List<Membre> getValuesList() {
        return values;
    }

    public void setItemList(List<Membre> valuesList) {
        this.values = valuesList;
    }

}
