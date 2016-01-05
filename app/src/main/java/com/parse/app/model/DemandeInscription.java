package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 29/08/2015.
 */
@ParseClassName("DemandeInscription")
public class DemandeInscription extends ParseObject{
    public void setDemandeur(ParseUser user) {
        put("demandeur",user);
    }
    public ParseUser getDemandeur() {
        return getParseUser("demandeur");
    }
    public void setTontine(Tontine tontine) {
        put("tontine", tontine);
    }
    public Tontine getTontine(){return (Tontine)getParseObject("tontine");}
    public void setDateDemande(String date) {
        put("date_demande", date);
    }
    public String getDateDemande() {
        return getString("date_demande");
    }
    public void setStatu(String statu) {
        put("statu",statu);
    }
    public String getStatu() {
        return getString("statu");
    }
    public void setResponsable(ParseUser responsable) {
        put("responsable", responsable);
    }
    public ParseUser getResponsable(){return getParseUser("responsable");}
    public void setRead(boolean read) {
        put("read",read);
    }
    public boolean getRead() {
        return getBoolean("read");
    }
    public void setTitre(String titre) {
        put("titre",titre);
    }
    public String getTitre() {
        return getString("titre");
    }
    public void setMessage(String message) {
        put("message",message);
    }
    public String getMessage() {
        return getString("message");
    }
}

