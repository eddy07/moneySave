package com.parse.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.app.R;
import com.parse.app.model.Tontine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TontinesAdapter extends ArrayAdapter<Tontine>{
    private final Context context;
    private List<Tontine> values = new ArrayList<Tontine>();
    private int[]colors = { R.drawable.fab1,R.drawable.fab2,R.drawable.fab3,R.drawable.fab4,R.drawable.fab5,R.drawable.fab6,R.drawable.fab7
            ,R.drawable.fab8,R.drawable.fab9,R.drawable.fab10,R.drawable.fab11,R.drawable.fab12,R.drawable.fab13,R.drawable.fab14,
            R.drawable.fab15};

    public TontinesAdapter(Context context, List<Tontine> values) {
        super(context, R.layout.tontines_item2, values);
        this.context = context;
        this.values = values;
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
                rowView = inflater.inflate(R.layout.tontines_item2, parent, false);
            }

            final TextView nom = (TextView) rowView.findViewById(R.id.nom_tontine);
            //TextView date_creation = (TextView) rowView.findViewById(R.id.creation_date);
            final TextView nbMembre = (TextView) rowView.findViewById(R.id.nb_membre);
            final TextView description = (TextView) rowView.findViewById(R.id.description_tontine);
            final TextView icon = (TextView) rowView.findViewById(R.id.icon);
            Random r = new Random();
            int n = colors.length + 1;
            int i = r.nextInt(n - 1);
            icon.setBackgroundResource(colors[i]);
            final Tontine tontine = values.get(position);
            tontine.fetchIfNeededInBackground(new GetCallback<Tontine>() {
                @Override
                public void done(Tontine parseObject, ParseException e) {
                    String initial = parseObject.getNom().substring(0,1);
                    icon.setText(initial);
                    setFontIcon(icon);
                    nom.setText(parseObject.getNom());
                    setFontNom(nom);
                    description.setText(parseObject.getDescription());
                    setFontDes(description);
                    //date_creation.setText(tontine.getCreation_date());
                    nbMembre.setText(parseObject.getNbMembre() + " membres");
                    setFontNbmembre(nbMembre);
                }
            });

            return rowView;
	    }
    public List<Tontine> getValuesList() {
        return values;
    }

    public void setItemList(List<Tontine> valuesList) {
        this.values = valuesList;
    }
    public void setFontNom(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        tv.setTypeface(tf);
    }
    public void setFontDes(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
    public void setFontNbmembre(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-BoldItalic.ttf");
        tv.setTypeface(tf);
    }
    public void setFontIcon(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
}
