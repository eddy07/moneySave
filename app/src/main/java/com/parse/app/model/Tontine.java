package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Jeddy on 27/08/2015.
 */
@ParseClassName("Tontine")
public class Tontine extends ParseObject {

    public ParseFile getLogo() {
        return getParseFile("logo");
    }

    public void setLogo(ParseFile logo) {
        put("logo", logo);
    }

    public String getNom() {
        return getString("nom");
    }

    public void setTel(String tel) {
        put("tel", tel);
    }

    public String getTel() {
        return getString("tel");
    }

    public void setNom(String nom) {
        put("nom", nom);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public Integer getMontant() {
        return getInt("montant");
    }

    public void setMontant(Integer montant) {
        put("montant", montant);
    }

    public Integer getNbMembre() {
        return getInt("nbMembre");
    }

    public void setNbMembre(Integer nbMembre) {
        put("nbMembre", nbMembre);
    }

    public String getCreation_date() {
        return getString("date_creation");
    }

    public void setCreation_date(String creation_date) {
        put("date_creation", creation_date);
    }

    public ParseUser getPresident() {
        return getParseUser("president");
    }

    public void setPresident(ParseUser president) {
        put("president", president);
    }

    public void setJourCotisation(String jour) {
        put("jour_cotisation", jour);
    }

    public String getJourCotisation() {
        return getString("jour_cotisation");
    }

    public String getSessionStatu(){return getString("session_statu");}

    public void setSessionStatu(String statu){put("session_statu",statu);}

    public void setType(String type){put("type", type);}

    public String getType(){ return getString("type");}

    public void setAmande(String amande){ put("gerer_amande", amande);}

    public String getAmande(){return getString("gerer_amande");}

}
