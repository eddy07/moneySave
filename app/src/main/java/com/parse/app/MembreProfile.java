package com.parse.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


public class MembreProfile extends ActionBarActivity{

    private String nom;
    private String tel;
    private String profession;
    private TextView nomtv;
    private TextView teltv;
    private TextView professiontv;
    private TextView labelnom;
    private TextView labeltel;
    private TextView labelprofession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membre_profile);
        getSupportActionBar().hide();
        nom = getIntent().getExtras().getString("NOM");
        tel = getIntent().getExtras().getString("TEL");
        profession = getIntent().getExtras().getString("PROFESSION");
        nomtv = (TextView)findViewById(R.id.nom);
        teltv = (TextView)findViewById(R.id.tel);
        professiontv = (TextView)findViewById(R.id.profession);
        labelnom = (TextView)findViewById(R.id.labelNom);
        labeltel = (TextView)findViewById(R.id.labelTel);
        labelprofession = (TextView)findViewById(R.id.labelProfession);

        nomtv.setText(nom);
        teltv.setText(tel);
        professiontv.setText(profession);
        setFont(nomtv, teltv, professiontv);
        setFontLabel(labelnom, labeltel, labelprofession);

    }
    public void setFontLabel(TextView tv1, TextView tv2, TextView tv3) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv1.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv1.setTypeface(tf2);
        Typeface tf3 = Typeface.createFromAsset(tv3.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv1.setTypeface(tf3);
    }
    public void setFont(TextView tv1, TextView tv2, TextView tv3) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv1.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv1.setTypeface(tf2);
        Typeface tf3 = Typeface.createFromAsset(tv3.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv1.setTypeface(tf3);
    }

}
