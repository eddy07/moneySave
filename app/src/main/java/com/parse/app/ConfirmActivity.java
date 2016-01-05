package com.parse.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class ConfirmActivity extends ActionBarActivity {

    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        setTitle("Confirm your Number");
        phoneNumber = getIntent().getStringExtra("PHONENUMBER");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.foward) {
            startRegister();
        }

        return super.onOptionsItemSelected(item);
    }

        public void startRegister(){
            Intent i = new Intent(this, RegisterActivity.class);
            i.putExtra("PHONENUMBER", phoneNumber);
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(
                                this,
                                R.anim.anim_left_right,
                                R.anim.anim_right_left).toBundle();
                startActivity(i, bndlanimation);
                finish();
            }else{
                startActivity(i);
                finish();
            }

    }
}
