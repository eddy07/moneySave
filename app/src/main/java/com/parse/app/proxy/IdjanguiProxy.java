package com.parse.app.proxy;

import com.parse.ParseUser;

public interface IdjanguiProxy {
	
	/**
     *
     * @param userId
     * @param channel
     * @param alert
     * @param title
     * @return the statu of the push notification
     * @throws IdjanguiProxyException
     */
    public String sendPush(String userId,String channel, String alert, String title) throws IdjanguiProxyException;



}
