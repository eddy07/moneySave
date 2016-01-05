package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 27/08/2015.
 */
@ParseClassName("Membre")
public class Membre extends ParseObject{

    public void setAdherant(ParseUser user) {
        put("adherant",user);
    }
    public ParseUser getAdherant() {
        return getParseUser("adherant");
    }
    public void setTontine(Tontine tontine) {
        put("tontine", tontine);
    }
    public Tontine getTontine(){return (Tontine)getParseObject("tontine");}
    public void setDateInscription(String date) {
        put("date_inscription", date);
    }
    public String getDateInscription() {
        return getString("date_inscription");
    }
    public void setResponsable(ParseUser user) {
        put("responsable",user);
    }
    public ParseUser getResponsable() {
        return getParseUser("responsable");
    }
    public void setFonction(String  fonction) {
        put("fonction",fonction);
    }
    public String getFonction() {
        return getString("fonction");
    }
}
