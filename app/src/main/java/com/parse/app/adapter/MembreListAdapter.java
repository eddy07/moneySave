package com.parse.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.R;
import com.parse.app.model.Membre;
import com.parse.app.model.Tontine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MembreListAdapter extends ArrayAdapter<Membre>{
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
        values.clear();
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
            final TextView fonction = (TextView) rowView.findViewById(R.id.fonction);
            final ImageView user = (ImageView) rowView.findViewById(R.id.user);
            final Membre membre = values.get(position);
            user.setImageDrawable(a.getResources().getDrawable(R.drawable.user));
            membre.fetchIfNeededInBackground(new GetCallback<Membre>() {
                @Override
                public void done(Membre m, ParseException e) {
                    ParseUser adherant = m.getAdherant();
                    adherant.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(final ParseUser user, ParseException e) {
                            if(user.getUsername().equals(ParseUser.getCurrentUser().getUsername())){
                                nom.setText("Moi");
                                fonction.setVisibility(View.VISIBLE);
                                fonction.setText("President");

                            }else{
                                nom.setText(user.getString("nom")+" "+user.getString("prenom"));
                                ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
                                tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                                    @Override
                                    public void done(Tontine tontine, ParseException e) {
                                        if(user.getString("pseudo").equals(tontine.getPresident().getString("speudo"))){

                                        }else{
                                            fonction.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            setFontNom(nom);
                            setFontFonction(fonction);

                        }
                    });
                }
            });


            return rowView;
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
