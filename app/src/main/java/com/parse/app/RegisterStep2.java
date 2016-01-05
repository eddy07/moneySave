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
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;


public class RegisterStep2 extends ActionBarActivity implements View.OnClickListener{

    private TextView titre, etape;
    private EditText mEmail, mTel;
    private ImageButton nextBtn;
    private SnackBar snackBar;
    private String nom, prenom, profession;
    private String e, t, email, tel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);
        getSupportActionBar().hide();
        titre = (TextView)findViewById(R.id.titre);
        etape = (TextView)findViewById(R.id.etape);
        mEmail = (EditText)findViewById(R.id.email);
        mTel = (EditText)findViewById(R.id.tel);
        nom = getIntent().getExtras().getString("NOM");
        prenom = getIntent().getExtras().getString("PRENOM");
        profession = getIntent().getExtras().getString("PROFESSION");
        setFontField(mEmail);
        setFontField(mTel);
        setFontTitle(titre, etape);
        if(getIntent() != null){
            e = getIntent().getExtras().getString("EMAIL");
            t = getIntent().getExtras().getString("TEL");
            mEmail.setText(e);
            mTel.setText(t);
        }
        nextBtn = (ImageButton)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(this);
        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mEmail.setTextColor(Color.WHITE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_step2, menu);
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
        String email, tel;
        email = mEmail.getText().toString();
        tel = mTel.getText().toString();
        if(!email.isEmpty() && !tel.isEmpty()){
            if(email.contains("@")) {
                this.email = email;
                this.tel = tel;
                Intent i = new Intent(this, RegisterFinalStep.class);
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
                emailError();
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
    public void emailError(){
        snackBar = new SnackBar(this, "Email invalide", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
        mEmail.setTextColor(Color.RED);

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
        Intent step1intent= new Intent(this, RegisterStep1.class);
        step1intent.putExtra("NOM", nom);
        step1intent.putExtra("PRENOM", prenom);
        step1intent.putExtra("PROFESSION", profession);
        step1intent.putExtra("EMAIL", this.email);
        step1intent.putExtra("TEL", this.tel);
        startActivity(step1intent);
    }
}
