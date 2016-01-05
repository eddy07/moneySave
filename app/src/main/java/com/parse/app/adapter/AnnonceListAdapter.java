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
import com.parse.app.model.Annonce;

import java.util.ArrayList;
import java.util.List;

public class AnnonceListAdapter extends ArrayAdapter<Annonce>{
    private final Context context;
    private List<Annonce> values = new ArrayList<Annonce>();

    public AnnonceListAdapter(Context context, List<Annonce> values) {
        super(context, R.layout.annonce_item, values);
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
                rowView = inflater.inflate(R.layout.annonce_item, parent, false);
            }
            //TextView statu = (TextView) rowView.findViewById(R.id.statu);
            final TextView nom = (TextView) rowView.findViewById(R.id.nom);
            //final TextView montant = (TextView) rowView.findViewById(R.id.montant);
            final Annonce annonce = values.get(position);
            annonce.fetchIfNeededInBackground(new GetCallback<Annonce>() {
                @Override
                public void done(Annonce m, ParseException e) {
                    ParseUser adherant = m.getAdherant();
                    adherant.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            /*if(parseObject.getUsername().equals(ParseUser.getCurrentUser())){
                                nom.setText("Moi");
                            }else{*/
                                nom.setText(user.getUsername());
                            //}
                        }
                    });
                }
            });

            return rowView;
	    }
    public List<Annonce> getValuesList() {
        return values;
    }

    public void setItemList(List<Annonce> valuesList) {
        this.values = valuesList;
    }

}
