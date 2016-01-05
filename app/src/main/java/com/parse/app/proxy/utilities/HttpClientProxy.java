package com.parse.app.proxy.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;

public class HttpClientProxy {
	
	private HttpClient httpclient;
	
	public HttpClientProxy (){
		// Create a new HttpClient
		httpclient = new DefaultHttpClient();
	}
	
	public String executeHttpPost(String hostname, String path, HashMap<String, String> parameters) throws Exception{
	    // Create a Post Header
	    HttpPost httppost = new HttpPost("http://" + hostname + "/" + path);

	    // Add parameters
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(parameters.size());
	    Set<String> parametersKeys = parameters.keySet();
	    for (String parameterKey : parametersKeys) {
	    	nameValuePairs.add(new BasicNameValuePair(parameterKey, parameters.get(parameterKey)));
	    }
	    // Encode parameters
	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	    // Execute HTTP Post Request
	    HttpResponse response = httpclient.execute(httppost);
	    StatusLine statusLine = response.getStatusLine();
	    int statusCode = statusLine.getStatusCode();
	    if (statusCode == 200) {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		    return reader.readLine();
	    } else{
	    	throw new Exception("statuscode = " + statusCode);
	    }	    
	}
	
	public String executeHttpGet(String hostname, String path, HashMap<String, String> parameters) throws Exception {
		// Build parameters
		//Uri.Builder builder = Uri.parse("http://" + hostname).buildUpon();
		//builder.path(path);
		String url = "http://" + hostname + "/" + path;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		
	    Set<String> parametersKeys = parameters.keySet();
	    for (String parameterKey : parametersKeys) {
	    	builder.appendQueryParameter(parameterKey, parameters.get(parameterKey));
	    }
	    //String url = builder.build().toString();
		
	    // Create a GET Header
	    HttpResponse response = httpclient.execute(new HttpGet(builder.toString()));
	    
	    StatusLine statusLine = response.getStatusLine();
	    int statusCode = statusLine.getStatusCode();
	    if (statusCode == 200) {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		    return reader.readLine();
	    } else{
	    	throw new Exception("statuscode = " + statusCode);
	    }	    
	    /*	    
	    InputStream responseContent = response.getEntity().getContent();
		return inputStreamToString(responseContent);
		*/
	}	
/*	
	private String inputStreamToString(InputStream is) throws IOException {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    while ((line = rd.readLine()) != null) { 
	        total.append(line); 
	    }
	    
	    // Return full string
	    return total.toString();
	}
*/
}
