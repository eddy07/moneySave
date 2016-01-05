package com.parse.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.app.R;
import com.parse.app.model.Annonce;

import java.util.ArrayList;
import java.util.List;

public class AnnonceAdapter1 extends ArrayAdapter<Annonce>{
    private final Context context;
    private Annonce annonce;
    private List<Annonce> values = new ArrayList<Annonce>();
    private SnackBar snackBar;
    private Activity activity;

    public AnnonceAdapter1(Activity activity, Context context, List<Annonce> values) {
        super(context, R.layout.annonce_item1, values);
        this.context = context;
        this.values = values;
        this.activity = activity;
    }
    @Override
    public void clear() {
        values.clear();
        super.clear();

    }
    @Override
    public void remove(Annonce annonce){
        values.remove(annonce);
        delete(annonce);
    }
	 
	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            if(rowView == null){
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.annonce_item1, parent, false);
            }
            //TextView statu = (TextView) rowView.findViewById(R.id.statu);
            //TextView auteur = (TextView) rowView.findViewById(R.id.auteur);
            TextView titre = (TextView) rowView.findViewById(R.id.titre);
            final TextView hint = (TextView) rowView.findViewById(R.id.hint);
            final LinearLayout snapLayout = (LinearLayout) rowView.findViewById(R.id.snapLayout);
            final TextView snap = (TextView) rowView.findViewById(R.id.snapText);
            final TextView message = (TextView) rowView.findViewById(R.id.message);
            final LinearLayout messageLayout = (LinearLayout) rowView.findViewById(R.id.blocMessage);
            final ImageButton expand = (ImageButton) rowView.findViewById(R.id.exp);
            final ImageButton close = (ImageButton) rowView.findViewById(R.id.close);
            annonce = values.get(position);
            //auteur.setText(annonce.getAuteur().getUsername());
            titre.setText(annonce.getTitre());
            snap.setText(annonce.getTitre().charAt(1));
            hint.setText(annonce.getMessage());
            
            //message.setText(annonce.getMessage());
            setFontMessage(hint);
            setFontTitle(titre);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remove(annonce);
                }
            });
            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean i = message.isShown();
                    if(i == true){
                        message.setVisibility(View.GONE);
                        hint.setVisibility(View.VISIBLE);
                        expand.setImageResource(R.drawable.ic_action_expand);
                    }else{
                        message.setVisibility(View.VISIBLE);
                        hint.setVisibility(View.GONE);
                        expand.setImageResource(R.drawable.ic_action_collapse);
                    }

                }
            });
            return rowView;
	    }
    public void snakBar(boolean status, String context){
        if(status==true && context==null) {
            snackBar = new SnackBar(activity, "Une annonce supprimée !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else if(status==false && context==null) {
            snackBar = new SnackBar(activity, "Erreur lors de la suppression !", "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }else{
            snackBar = new SnackBar(activity, activity.getResources().getString(R.string.no_internet), "Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });
        }
        snackBar.show();
    }
    public List<Annonce> getValuesList() {
        return values;
    }

    public void deleteItem(int position){
        values.get(position).deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    snakBar(true, null);
                } else {
                    snakBar(false, null);
                }
            }
        });
    }
    public void delete(Annonce annonce){
        annonce.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    snakBar(true, null);
                } else {
                    snakBar(false, null);
                }
            }
        });
    }
    public void setItemList(List<Annonce> valuesList) {
        this.values = valuesList;
    }

    public void setFontTitle(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv.setTypeface(tf);
    }
    public void setFontMessage(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
}
