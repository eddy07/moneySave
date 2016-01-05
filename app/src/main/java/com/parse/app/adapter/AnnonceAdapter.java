package com.parse.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.app.InscriptionActivity;
import com.parse.app.R;
import com.parse.app.model.Annonce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AnnonceAdapter extends ArrayAdapter<Annonce>{
    private final Context context;
    private Annonce annonce;
    private List<Annonce> values = new ArrayList<Annonce>();
    private SnackBar snackBar;
    private Activity activity;
    private String tontineId;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

    public AnnonceAdapter(Activity activity, Context context, List<Annonce> values, String tontineId) {
        super(context, R.layout.annonce_item1, values);
        this.context = context;
        this.values = values;
        this.activity = activity;
        this.tontineId =tontineId;
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
            //rowView.setBackgroundColor(activity.getResources().getColor(android.R.color.background_light)); //default color

            if (mSelection.get(position) != null) {
                rowView.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red
            }
            //TextView statu = (TextView) rowView.findViewById(R.id.statu);
            //TextView auteur = (TextView) rowView.findViewById(R.id.auteur);
            TextView titre = (TextView) rowView.findViewById(R.id.titre);
            final TextView hint = (TextView) rowView.findViewById(R.id.hint);
            final LinearLayout snapLayout = (LinearLayout) rowView.findViewById(R.id.snapLayout);
            final LinearLayout bodyLayout = (LinearLayout) rowView.findViewById(R.id.bodyLayout);
            final TextView snap = (TextView) rowView.findViewById(R.id.snapText);
            final TextView message = (TextView) rowView.findViewById(R.id.message);
            final LinearLayout messageLayout = (LinearLayout) rowView.findViewById(R.id.blocMessage);
            final ImageButton expand = (ImageButton) rowView.findViewById(R.id.exp);
            final ImageButton close = (ImageButton) rowView.findViewById(R.id.close);
            annonce = values.get(position);
            //auteur.setText(annonce.getAuteur().getUsername());
            titre.setText(annonce.getTitre());
            snap.setText(annonce.getTitre());
            hint.setText(annonce.getMessage());
            message.setText(annonce.getMessage());
            setFontMessage(hint);
            setFontMessage(message);
            setFontMessage(snap);
            setFontTitle(titre);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remove(annonce);
                }
            });
            bodyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(annonce.getType().equals("inscription")){
                        Intent i = new Intent(activity,InscriptionActivity.class);
                        i.putExtra("TONTINE_ID", tontineId);
                        i.putExtra("ANNONCE_ID", annonce.getObjectId());
                        activity.startActivity(i);
                    }else{
                        boolean i = messageLayout.isShown();
                        if(i == true){
                            messageLayout.setVisibility(View.GONE);
                        }else{
                            messageLayout.setVisibility(View.VISIBLE);
                            annonce.fetchIfNeededInBackground(new GetCallback<Annonce>() {
                                @Override
                                public void done(Annonce an, ParseException e) {
                                    an.setStatu("Read");
                                    an.saveInBackground();
                                }
                            });
                        }
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
    public void deleteItem(Set<Integer> listPosition){
        for(Integer position:listPosition){
            values.get(position).deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        snakBar(true, null);
                        mSelection = new HashMap<Integer, Boolean>();
                        notifyDataSetChanged();
                    } else {
                        snakBar(false, null);
                    }
                }
            });
        }

    }
    public void delete(Annonce annonce){
        annonce.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    snakBar(true, null);
                    notifyDataSetChanged();
                } else {
                    snakBar(false, null);
                }
            }
        });
    }
    public void setItemList(List<Annonce> valuesList) {
        this.values = valuesList;
    }
    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        deleteItem(getCurrentCheckedPosition());
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
