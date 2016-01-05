package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 27/08/2015.
 */
@ParseClassName("DemandeSoutient")
public class DemandeSoutient extends ParseObject{

    public String getMotif() {
        return getString("motif");
    }
    public void setMotif(String motif) {
        put("motif", motif);
    }
    public String getDateCreation() {
        return getString("date_creation");
    }
    public void setDateCreation(String date) {
        put("date_creation", date);
    }
    public ParseUser getAuteur() {
        return getParseUser("auteur");
    }
    public void setAuteur(ParseUser auteur) {
        put("auteur", auteur);
    }
    public Tontine getTontine() {
        return (Tontine)getParseObject("tontine");
    }
    public void setTontine(Tontine tontine) {
        put("tontine", tontine);
    }
}
