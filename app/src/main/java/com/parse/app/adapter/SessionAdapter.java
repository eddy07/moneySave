package com.parse.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.app.R;
import com.parse.app.model.Session;

import java.util.ArrayList;
import java.util.List;

public class SessionAdapter extends ArrayAdapter<Session>{
    private final Context context;
    private ParseUser thisuser = new ParseUser();
    private List<Session> values = new ArrayList<Session>();

    public SessionAdapter(Context context, List<Session> values) {
        super(context, R.layout.session_item, values);
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
                rowView = inflater.inflate(R.layout.session_item, parent, false);
            }
            final TextView statu = (TextView) rowView.findViewById(R.id.statu);
            final TextView date = (TextView) rowView.findViewById(R.id.date);
            final TextView beneficiaire = (TextView) rowView.findViewById(R.id.beneficiaire);
            final TextView montant = (TextView) rowView.findViewById(R.id.montant);
            final Session session = values.get(position);
            session.fetchIfNeededInBackground(new GetCallback<Session>() {
                @Override
                public void done(final Session parseObject, ParseException e) {
                    if(parseObject.getStatu().equals("fermée")){
                        montant.setText("Montant de la cotisation : " + parseObject.getMontantCotisation());
                        statu.setText(parseObject.getStatu());
                        statu.setTextColor(Color.GRAY);
                    }else{
                        montant.setVisibility(View.GONE);
                       // statu.setText(parseObject.getStatu());
                        statu.setText(parseObject.getStatu());
                        statu.setTextColor(getContext().getResources().getColor(R.color.hint));
                    }

                    date.setText("Session du : " + parseObject.getDateDebut().substring(0,12));
                    ParseUser beneficiaireUser = parseObject.getBeneficiaire();
                    beneficiaireUser.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser beneficiaireObject, ParseException e) {
                            beneficiaire.setText("Bénéficiaire : " + beneficiaireObject.getUsername());
                        }
                    });


                }
            });

            return rowView;
	    }
    public List<Session> getValuesList() {
        return values;
    }

    public void setItemList(List<Session> valuesList) {
        this.values = valuesList;
    }

}
