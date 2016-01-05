package com.parse.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.app.utilities.ImageIndicatorView;


public class WelcomeActivity extends ActionBarActivity{

    private ImageIndicatorView imageIndicatorView;
    private int orientation;
    private Button loginBtn;
    private Button registerBtn;
    private LinearLayout blockapp;
    private LinearLayout blockdescr;
    private ScrollView scroll;
    private TextView app;
    private TextView appdes;
    private TextView text1;
    private TextView text2;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        context = this;
        getSupportActionBar().hide();
        app = (TextView)findViewById(R.id.app);
        appdes = (TextView)findViewById(R.id.appDes);
        setFontApp(app);
        setFontAppDes(appdes);
        /*blockapp = (LinearLayout)findViewById(R.id.blockapp);
        blockdescr = (LinearLayout)findViewById(R.id.blockdescr);*/
        loginBtn = (Button)findViewById(R.id.loginBtn);
        registerBtn = (Button)findViewById(R.id.registerBtn);

        //text1 = (TextView)findViewById(R.id.text1);
        //text2 = (TextView)findViewById(R.id.text2);
        /*scroll = (ScrollView)findViewById(R.id.scroll);
        scroll.setSmoothScrollingEnabled(true);


        setFontText(text1,text2);*/
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, LoginActivity.class);
                startActivity(i);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, RegisterActivity.class);
                startActivity(i);
            }
        });
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent homeIntent= new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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

    public void setFontApp(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Antipasto_regular.otf");
        tv.setTypeface(tf);
    }
    public void setFontAppDes(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Thin.ttf");
        tv.setTypeface(tf);
    }
    public void setFontText(TextView tv1,TextView tv2) {
        Typeface tf1 = Typeface.createFromAsset(tv1.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv1.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(tv2.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        tv2.setTypeface(tf2);
    }
}
