package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 27/08/2015.
 */
@ParseClassName("Session")
public class Session extends ParseObject {

    public String getDateCreation() {
        return getString("date_creation");
    }
    public void setDateCreation(String dateCreation) {
        put("date_creation",dateCreation);
    }
    public String getDateDebut() {
        return getString("date_debut");
    }
    public void setDateDebut(String dateDebut) {
        put("date_debut",dateDebut);
    }
    public String getDateFin() {
        return getString("date_fin");
    }
    public void setDateFin(String dateFin) {
        put("date_fin",dateFin);
    }
    public String getStatu() {
        return getString("statu");
    }
    public void setStatu(String statu) {
        put("statu",statu);
    }
    public ParseUser getBeneficiaire() {
        return getParseUser("beneficiaire");
    }
    public void setBeneficiaire(ParseUser beneficiaire) {
        put("beneficiaire",beneficiaire);
    }
    public ParseUser getPresident() {
        return getParseUser("president");
    }
    public void setPresident(ParseUser president) {
        put("president",president);
    }
    public Tontine getTontine() {
        return (Tontine)getParseObject("tontine");
    }
    public void setTontine(Tontine tontine) {
        put("tontine",tontine);
    }
    public Integer getMontantCotisation() {
        return getInt("montant_cotisation");
    }
    public void setMontantCotisation(Integer montantCotisation) { put("montant_cotisation",montantCotisation);}
    public Integer getAmande() {
        return getInt("amande");
    }
    public void setAmande(Integer amande) {
        put("amande", amande);
    }
    public void setMontantTotal(Integer montant){put("montant_total", montant);}
    public Integer getMontantTotal(){return getInt("montant_total");}
    public void setTour(Integer numTour){ put("tour", numTour); }
    public Integer getTour(){ return  Integer.getInteger("tour"); }
}
