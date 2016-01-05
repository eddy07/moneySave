package com.parse.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Jeddy on 30/08/2015.
 */
@ParseClassName("FeedBack")
public class FeedBack extends ParseObject{

    public String getMessage(){return getString("message");}
    public void setMessage(String message){put("message",message);}
    public ParseUser getUser(){return getParseUser("user");}
    public void setUser(ParseUser user){put("user",user);}
}
