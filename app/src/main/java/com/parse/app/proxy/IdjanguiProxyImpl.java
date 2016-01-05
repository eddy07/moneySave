package com.parse.app.proxy;

import com.parse.ParseUser;
import com.parse.app.proxy.utilities.HttpClientProxy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


public class IdjanguiProxyImpl implements IdjanguiProxy {
	
	private static String IDJANGUI_HOSTNAME = "idjangui237.livehost.fr";
	
	private HttpClientProxy httpClientProxy;
	
	private static IdjanguiProxyImpl instance;
	
	private IdjanguiProxyImpl(){
		httpClientProxy = new HttpClientProxy();
	}
	
	public static IdjanguiProxyImpl getInstance(){
		if(instance == null){
			instance = new IdjanguiProxyImpl();
		}
		return instance;
	}

    @Override
    public String sendPush(String userId, String channel, String alert, String title) throws IdjanguiProxyException {
        // Build HTTP parameters
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("userId", userId);
        parameters.put("channel", channel);
        parameters.put("title", title);
        parameters.put("alert", alert);
        try{
            // Execute HTTP request
            String httpResponse = httpClientProxy.executeHttpGet(IDJANGUI_HOSTNAME, "idjangui_push_api.php", parameters);
            JSONObject jsonResponse = new JSONObject(httpResponse);
            int status = jsonResponse.getInt("status");
            if(status == 1){ // request successfully completed
                String statu = jsonResponse.getString("result");
                return statu;
            } else{
                String errorCode = jsonResponse.getString("error_code");
                throw new IdjanguiProxyException(errorCode);
            }

        } catch (Exception ex){
            throw new IdjanguiProxyException(ex);
        }
    }
}
