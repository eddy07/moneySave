package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Jeddy on 29/08/2015.
 */
@ParseClassName("Compte")
public class Compte extends ParseObject{


    public String getUser() {
        return getString("userId");
    }

    public void setUserId(String userId) {
        put("userId", userId);
    }

    public Integer getSolde() {
        return getInt("solde");
    }

    public void setSolde(Integer montant) {
        put("solde",montant);
    }
}
