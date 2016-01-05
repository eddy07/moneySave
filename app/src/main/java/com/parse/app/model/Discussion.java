package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 27/08/2015.
 */
@ParseClassName("Discussion")
public class Discussion extends ParseObject{

    public String getMessage() {
        return getString("message");
    }
    public void setMessage(String message) {
        put("message", message);
    }
    public String getAuteur() {
        return getString("auteur");
    }
    public void setAuteur(ParseUser auteur) {
        put("auteur", auteur);
    }
    public String getDate() {return getString("posted_at");}
    public void setDate(String date) {
        put("posted_at", date);
    }
    public void setSession(Session session){put("session", session);}
    public Session getSession(){return (Session)getParseObject("session");}

}
