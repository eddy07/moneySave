package com.parse.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.app.R;
import com.parse.app.model.DemandeInscription;

import java.util.ArrayList;
import java.util.List;

public class DemandeInscriptionAdapter extends ArrayAdapter<DemandeInscription>{
    private final Context context;
    private DemandeInscription demandeInscription;
    private List<DemandeInscription> values = new ArrayList<DemandeInscription>();

    public DemandeInscriptionAdapter(Context context, List<DemandeInscription> values) {
        super(context, R.layout.demande_inscription_item, values);
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
                rowView = inflater.inflate(R.layout.demande_inscription_item, parent, false);
            }
            //TextView statu = (TextView) rowView.findViewById(R.id.statu);
            //TextView auteur = (TextView) rowView.findViewById(R.id.auteur);
            TextView titre = (TextView) rowView.findViewById(R.id.titre);
            TextView message = (TextView) rowView.findViewById(R.id.message);
            //final LinearLayout messageLayout = (LinearLayout) rowView.findViewById(R.id.messageLayout);
            //final ImageButton expand = (ImageButton) rowView.findViewById(R.id.expand);
            demandeInscription = values.get(position);
            //auteur.setText(invitation.getAuteur().getUsername());
            titre.setText(demandeInscription.getTitre());
            message.setText(demandeInscription.getMessage());
            setFontMessage(message);
            setFontTitle(titre);
            /*expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(messageLayout.isShown() == true){
                        messageLayout.setVisibility(View.GONE);
                        expand.setImageResource(R.drawable.ic_action_expand);
                    }else{
                        messageLayout.setVisibility(View.VISIBLE);
                        expand.setImageResource(R.drawable.ic_action_collapse);
                    }

                }
            });*/
            return rowView;
	    }
    public List<DemandeInscription> getValuesList() {
        return values;
    }

    public void setItemList(List<DemandeInscription> valuesList) {
        this.values = valuesList;
    }

    public void setFontTitle(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        tv.setTypeface(tf);
    }
    public void setFontMessage(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
}
