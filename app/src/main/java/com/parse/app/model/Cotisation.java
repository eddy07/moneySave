package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 27/08/2015.
 */
@ParseClassName("Cotisation")
public class Cotisation  extends ParseObject{
    public Session getSession() {
        return (Session)getParseObject("session");
    }

    public void setSession(Session session) {
        put("session",session);
    }

    public ParseUser getAdherant() {
        return getParseUser("adherant");
    }

    public void setAdherant(ParseUser adherant) {
        put("adherant",adherant);
    }

    public String getDate() {
        return getString("date");
    }

    public void setDate(String date) {
        put("date",date);
    }

    public Integer getMontant() {
        return  getInt("montant");
    }

    public void setMontant(Integer montant) {
        put("montant",montant);
    }

    public ParseUser getBeneficiaire() {
        return getParseUser("beneficiaire");
    }

    public void setBeneficiaire(ParseUser beneficiaire) {
        put("beneficiaire",beneficiaire);
    }
}
