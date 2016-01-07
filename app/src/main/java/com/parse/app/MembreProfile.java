package com.parse.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.app.model.Tontine;


public class MembreProfile extends ActionBarActivity{

    private String nom;
    private String name;
    private String tel;
    private String tontineId;
    private String profession;
    private String pseudo;
    private String email;
    private String poste;
    private TextView nomtv;
    private TextView teltv;
    private TextView professiontv;
    private TextView pseudotv;
    private TextView postetv;
    private TextView emailtv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membre_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nom = getIntent().getExtras().getString("NOM");
        tel = getIntent().getExtras().getString("TEL");
        profession = getIntent().getExtras().getString("PROFESSION");
        poste = getIntent().getExtras().getString("POSTE");
        email = getIntent().getExtras().getString("EMAIL");
        pseudo = getIntent().getExtras().getString("PSEUDO");
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
        tontineParseQuery.getInBackground(tontineId,new GetCallback<Tontine>() {
            @Override
            public void done(Tontine tontine, ParseException e) {
                if(e==null){
                    name= tontine.getNom();
                }
            }
        });
        nomtv = (TextView)findViewById(R.id.nom);
        teltv = (TextView)findViewById(R.id.tel);
        professiontv = (TextView)findViewById(R.id.profession);
        postetv = (TextView)findViewById(R.id.poste);
        pseudotv = (TextView)findViewById(R.id.pseudo);
        emailtv = (TextView)findViewById(R.id.email);
        getSupportActionBar().setTitle(pseudo);
        nomtv.setText(nom);
        teltv.setText(tel);
        pseudotv.setText(pseudo);
        professiontv.setText(profession);
        postetv.setText(poste);
        emailtv.setText(email);
        setFont(nomtv, teltv, professiontv, postetv, pseudotv, emailtv);

    }
    public void setFont(TextView tv1, TextView tv2, TextView tv3, TextView tv4, TextView tv5, TextView tv6) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv1.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv2.setTypeface(tf2);
        Typeface tf3 = Typeface.createFromAsset(tv3.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv3.setTypeface(tf3);
        Typeface tf4 = Typeface.createFromAsset(tv4.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv4.setTypeface(tf4);
        Typeface tf5 = Typeface.createFromAsset(tv5.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv5.setTypeface(tf5);
        Typeface tf6 = Typeface.createFromAsset(tv6.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv6.setTypeface(tf6);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this,MainTontineActivity.class);
        i.putExtra("TONTINE_ID",tontineId);
        i.putExtra("NOM",name);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
