package com.parse.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.app.utilities.NetworkUtil;

import java.text.DateFormat;
import java.util.Date;


public class UpdateProfileActivity extends ActionBarActivity {
    private String phoneNumber;
    private EditText mName;
    private String nom;
    private EditText mEmail;
    private String email;
    private EditText mPhone;
    private EditText mOldPassword;
    private EditText mNewPassword;
    private String password;
    private String newpassword;
    private Context context;
    private Button mUpdateBtn;
    public static int TYPE_NOT_CONNECTED = 0;
    private boolean status = false;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Modifier mon profile");
        context = this;
        date = DateFormat.getDateTimeInstance().format(new Date());
        mName = (EditText)findViewById(R.id.nom);
        mEmail = (EditText)findViewById(R.id.email);
        mOldPassword = (EditText)findViewById(R.id.password);
        mNewPassword = (EditText)findViewById(R.id.newpassword);
        mPhone = (EditText)findViewById(R.id.phone);
        mUpdateBtn = (Button)findViewById(R.id.registerBtn);

        ParseQuery<ParseUser> userParseQuery = ParseQuery.getQuery(ParseUser.class);
        userParseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(),new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e==null){
                    mName.setText(user.getUsername());
                    mEmail.setText(user.getEmail());
                    mPhone.setText(user.getString("phoneNumber"));
                    mOldPassword.setText("");
                    mNewPassword.setText("");
                }
            }
        });

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.getConnectivityStatus(context) == TYPE_NOT_CONNECTED) {
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }else{
                    nom = mName.getText().toString();
                    email = mEmail.getText().toString();
                    phoneNumber = mPhone.getText().toString();
                    password = mOldPassword.getText().toString();
                    newpassword = mNewPassword.getText().toString();
                    ParseQuery<ParseUser> userParseQuery = ParseQuery.getQuery(ParseUser.class);
                    userParseQuery.fromLocalDatastore();
                    userParseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(),new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(e==null){
                                user.setUsername(nom);
                                user.setEmail(email);
                                user.put("phoneNumber",phoneNumber);
                                user.setPassword(newpassword);
                                user.put("date_update",date);
                                user.pinInBackground();
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null){
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException iex) {
                                                iex.printStackTrace();
                                            }
                                        }else{
                                        }
                                    }
                                });

                            }else{
                            }
                        }
                    });
                }

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_profile, menu);
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
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
