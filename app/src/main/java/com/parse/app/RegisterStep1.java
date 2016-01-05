package com.parse.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;


public class RegisterStep1 extends ActionBarActivity implements View.OnClickListener{

    private TextView titre,etape;
    private EditText mNom, mPrenom, mProfession;
    private ImageButton nextBtn;
    private SnackBar snackBar;
    private String n,p,pf;
    private String email, tel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step1);
        getSupportActionBar().hide();
        titre = (TextView)findViewById(R.id.wel);
        etape = (TextView)findViewById(R.id.etape);
        mNom = (EditText)findViewById(R.id.nom);
        mPrenom = (EditText)findViewById(R.id.prenom);
        mProfession = (EditText)findViewById(R.id.profession);
        nextBtn = (ImageButton)findViewById(R.id.nextBtn);
        setFontField(mNom);
        setFontField(mPrenom);
        setFontField(mProfession);
        setFontTitle(titre, etape);
        if(getIntent().getExtras()!=null){
            n = getIntent().getExtras().getString("NOM");
            p = getIntent().getExtras().getString("PRENOM");
            pf = getIntent().getExtras().getString("PROFESSION");
            email = getIntent().getExtras().getString("EMAIL");
            tel = getIntent().getExtras().getString("TEL");
            mNom.setText(n);
            mPrenom.setText(p);
            mProfession.setText(pf);
        }
        nextBtn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_step1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        String nom, prenom, profession;
        nom = mNom.getText().toString();
        prenom = mPrenom.getText().toString();
        profession = mProfession.getText().toString();
        if(!nom.isEmpty() && !prenom.isEmpty() && !profession.isEmpty()){
            Intent i = new Intent(this, RegisterStep2.class);
            i.putExtra("NOM", nom);
            i.putExtra("PRENOM", prenom);
            i.putExtra("PROFESSION", profession);
            i.putExtra("EMAIL", email);
            i.putExtra("TEL", tel);
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
                finish();
            }
        }else{
            fieldError();
        }
    }

    public void fieldError(){
        snackBar = new SnackBar(this, "Données manquantes ou invalides", "", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackBar.dismiss();
                    }
                });

            snackBar.show();
    }

    public void setFontField(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
    public void setFontTitle(TextView tv1,TextView tv2) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv1.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-ThinItalic.ttf");
        tv2.setTypeface(tf2);
    }
    @Override
    public void onBackPressed() {
        Intent intent= new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
