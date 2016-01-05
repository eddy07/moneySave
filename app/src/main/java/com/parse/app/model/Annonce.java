package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 27/08/2015.
 */
@ParseClassName("Annonce")
public class Annonce extends ParseObject{

    public void setTitre(String titre){ put("titre", titre);}
    public String getTitre(){ return getString("titre");}
    public String getMessage() {
        return getString("message");
    }
    public void setMessage(String message) {
        put("message", message);
    }
    public ParseUser getAuteur() {
        return getParseUser("auteur");
    }
    public void setAuteur(ParseUser auteur) {
        put("auteur", auteur);
    }
    public void setTontine(Tontine tontine){put("tontine", tontine);}
    public Tontine getTontine(){return (Tontine) getParseObject("tontine");}
    public void setAdherant(ParseUser user){put("adherant", user);}
    public ParseUser getAdherant(){return (ParseUser)getParseObject("adherant");}
    public void setType(String type){put("type", type);}
    public String getType(){return getString("type");}
    public void setStatu(String statu){put("statu", statu);}
    public String getStatu(){return getString("statu");}
    public void setAccept(boolean accept){put("accept", accept);}
    public boolean getAccept(){return getBoolean("accept");}
}
