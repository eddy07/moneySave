package com.parse.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.app.R;
import com.parse.app.model.Presence;

import java.util.ArrayList;
import java.util.List;

public class MembreAdapter extends ArrayAdapter<Presence>{
    private final Context context;
    private ParseUser membre;
    private List<Presence> values = new ArrayList<Presence>();

    public MembreAdapter(Context context, List<Presence> values) {
        super(context, R.layout.membre_item, values);
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
                rowView = inflater.inflate(R.layout.membre_item, parent, false);
            }
            //TextView statu = (TextView) rowView.findViewById(R.id.statu);
            final TextView nom = (TextView) rowView.findViewById(R.id.nom);
            final TextView montant = (TextView) rowView.findViewById(R.id.montant);
            final Presence membrePresent = values.get(position);
            membrePresent.fetchIfNeededInBackground(new GetCallback<Presence>() {
                @Override
                public void done(Presence parseObject, ParseException e) {
                    montant.setText(parseObject.getMontantCotise() + " FCFA");
                    ParseUser membre = parseObject.getMembre();
                    membre.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseObject, ParseException e) {
                            if(parseObject.getUsername() == ParseUser.getCurrentUser().getUsername()){
                                nom.setText("Moi");
                            }else {
                                nom.setText(parseObject.getUsername());
                            }
                        }
                    });
                }
            });


            return rowView;
	    }
    public List<Presence> getValuesList() {
        return values;
    }

    public void setItemList(List<Presence> valuesList) {
        this.values = valuesList;
    }

}
