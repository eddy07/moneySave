package com.parse.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class NotificationActivity extends ActionBarActivity {

    private String tontineId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Notifications");
        tontineId = getIntent().getExtras().getString("TONTINE_ID");

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this, SessionActivity.class);
        i.putExtra("TONTINE_ID",tontineId);
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(
                            this,
                            R.anim.anim_right_left,
                            R.anim.anim_left_right).toBundle();
            startActivity(i, bndlanimation);
            finish();
        } else {
            startActivity(i);
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
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
        }else if (id == android.R.id.home) {
             onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
