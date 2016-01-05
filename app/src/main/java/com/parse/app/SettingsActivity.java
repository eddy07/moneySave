package com.parse.app;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.parse.ParseAnalytics;


public class SettingsActivity extends ActionBarActivity {

    private TextView itemProfile;
    private TextView itemHelp;
    private TextView itemContacts;
    private TextView itemFeedback;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        context = this;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Paramètres");
        itemProfile = (TextView)findViewById(R.id.itemProfile);
        itemHelp = (TextView)findViewById(R.id.itemHelp);
        itemFeedback = (TextView)findViewById(R.id.itemFeedback);
        itemProfile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MyProfile.class);
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(
                                    context,
                                    R.anim.anim_left_right,
                                    R.anim.anim_right_left).toBundle();
                    startActivity(i, bndlanimation);
                } else {
                    startActivity(i);
                }
            }
        });
        itemFeedback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FeedBackActivity.class);
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(
                                    context,
                                    R.anim.anim_left_right,
                                    R.anim.anim_right_left).toBundle();
                    startActivity(i, bndlanimation);
                } else {
                    startActivity(i);
                }
            }
        });



        itemHelp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, HelpActivity.class);
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(
                                    context,
                                    R.anim.anim_left_right,
                                    R.anim.anim_right_left).toBundle();
                    startActivity(i, bndlanimation);
                } else {
                    startActivity(i);
                }

            }
        });

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
        if (id == R.id.action_settings) {
            return true;
        }else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
