package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 29/08/2015.
 */
@ParseClassName("Presence")
public class Presence extends ParseObject{

    public void setMembre(ParseUser membre){put("membre",membre);}
    public ParseUser getMembre(){return getParseUser("membre");}
    public void setSession(Session session){put("session",session);}
    public Session getSession(){return (Session)getParseObject("session");}
    public void setStatu(String statu){put("statu",statu);}
    public String getStatu(){return getString("statu");}
    public void setDate(String date){put("date",date);}
    public String getDate(){return getString("date");}
    public Integer getMontantCotise(){return getInt("montant");}
    public void setMontantCotise(Integer montantCotise){put("montant",montantCotise);}
}
