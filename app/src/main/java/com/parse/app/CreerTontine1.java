package com.parse.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;


public class CreerTontine1 extends ActionBarActivity {

    private EditText nom;
    private EditText tel;
    private TextView mTitre, mAmande;
    private TextView mEtape ,mType;
    private RadioGroup groupType;
    private RadioGroup groupAmande;
    private ImageButton nextBtn;
    private RadioButton rbtype;
    private RadioButton rbAmande;
    private SnackBar snackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_tontine1);
        getSupportActionBar().hide();
        nom = (EditText)findViewById(R.id.nom);
        tel = (EditText)findViewById(R.id.tel);
        mTitre = (TextView)findViewById(R.id.titre);
        mEtape = (TextView)findViewById(R.id.etape);
        mType = (TextView)findViewById(R.id.typetontine);
        mAmande = (TextView)findViewById(R.id.amande);
        nextBtn = (ImageButton)findViewById(R.id.nextBtn);
        groupAmande = (RadioGroup)findViewById(R.id.groupAmande);
        groupType = (RadioGroup)findViewById(R.id.groupeType);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
        setFontText(mTitre,mEtape);
        setFontDes(mAmande);
        setFontDes(mType);
    }

    public void next() {
        String nomValue = nom.getText().toString();
        String  telValue = tel.getText().toString();
        String typeValue, amandeValue;
        int typeSelected = groupType.getCheckedRadioButtonId();
        int amandeSelected = groupAmande.getCheckedRadioButtonId();
        rbtype = (RadioButton) findViewById(typeSelected);
        rbAmande = (RadioButton) findViewById(amandeSelected);
        if (rbAmande == null || rbtype == null || nomValue.isEmpty()) {
            error();
        } else {
            typeValue = rbtype.getText().toString();
            amandeValue = rbAmande.getText().toString();
            if (!nomValue.isEmpty() && !typeValue.isEmpty() && !amandeValue.isEmpty()) {
                Intent i = new Intent(this, CreerTontine2.class);
                i.putExtra("nom", nomValue);
                i.putExtra("type", typeValue);
                i.putExtra("tel",telValue);
                i.putExtra("amande", amandeValue);
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(
                                    this,
                                    R.anim.anim_left_right,
                                    R.anim.anim_right_left).toBundle();
                    startActivity(i, bndlanimation);
                    finish();
                } else {
                    startActivity(i);
                }
            } else {
                error();
            }
        }
    }
    public void error(){

            snackBar = new SnackBar(this, "Données manquantes", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackBar.dismiss();
                }
            });


        snackBar.show();
    }
    public void setFontDes(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Medium.ttf");
        tv.setTypeface(tf);
    }
    public void setFontText(TextView tv1,TextView tv2) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv1.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-ThinItalic.ttf");
        tv2.setTypeface(tf2);
    }
}

