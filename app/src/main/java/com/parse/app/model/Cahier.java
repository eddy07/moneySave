package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 30/08/2015.
 */
@ParseClassName("Cahier")
public class Cahier extends ParseObject{

    public Tontine getTontine(){return (Tontine)getParseObject("tontine");}
    public void setTontine(Tontine tontine){put("tontine", tontine);}
    public Integer getMontant(){return Integer.getInteger("montant");}
    public void setMontant(Integer montant){put("montant",montant);}
    public Integer getAmande(){return Integer.getInteger("amande");}
    public void setAmande(Integer amande){put("amande",amande);}
    public void setCreated(String createdAt){put("date_creation", createdAt);}
    public String getCreated(){return getString("date_creation");}
}
