package com.parse.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.app.model.Annonce;
import com.parse.app.model.Cahier;
import com.parse.app.model.Compte;
import com.parse.app.model.Cotisation;
import com.parse.app.model.DemandeSoutient;
import com.parse.app.model.Discussion;
import com.parse.app.model.FeedBack;
import com.parse.app.model.Tontine;
import com.parse.app.model.DemandeInscription;
import com.parse.app.model.Membre;
import com.parse.app.model.Presence;
import com.parse.app.model.Session;


public class App extends Application {
    //parse credentials
    private static String PARSE_APPLICATION_ID = "sRQKxX5K0GJWmPEvAokwAcVE6byxkVfeGLr3Pd4c";
    private static String PARSE_CLIENT_KEY = "1dzxrAsKE2woDHWO7DKNVkeHNO12uesqDZAqxuWT";
    @Override
    public void onCreate() {
     super.onCreate();
        ParseObject.registerSubclass(Cahier.class);
        ParseObject.registerSubclass(Discussion.class);
        ParseObject.registerSubclass(DemandeSoutient.class);
        ParseObject.registerSubclass(Membre.class);
        ParseObject.registerSubclass(Tontine.class);
        ParseObject.registerSubclass(DemandeInscription.class);
        ParseObject.registerSubclass(Session.class);
        ParseObject.registerSubclass(Compte.class);
        ParseObject.registerSubclass(Cotisation.class);
        ParseObject.registerSubclass(Presence.class);
        ParseObject.registerSubclass(FeedBack.class);
        ParseObject.registerSubclass(Annonce.class);
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);
        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
