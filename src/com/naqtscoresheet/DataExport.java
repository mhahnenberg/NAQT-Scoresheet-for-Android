package com.naqtscoresheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.widget.Toast;

public class DataExport {
	public enum Format {
		JSON,
		XML
	}
	
	public static void postGameData(Context context, Game game, String url, Format format) {
		String gameData;
		if (format == Format.JSON) {
			System.err.println("DEBUG: generating JSON...");
			Visitor visitor = new JSONVisitor();
			visitor.visit(game);
			gameData = visitor.toString();
		}
		else if (format == Format.XML) {
			System.err.println("DEBUG: generating XML...");
			Visitor visitor = new XMLVisitor();
			visitor.visit(game);
			gameData = visitor.toString();
		}
		else {
			throw new RuntimeException("Unknown data type.");
		}
		
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://192.168.1.142:8080/beacon");

	    try {
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("game_data", gameData));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        ResponseHandler<String> handler = new BasicResponseHandler();
	        String responseBody = httpclient.execute(httppost, handler);
	        Toast t = Toast.makeText(context, "Game upload successful.", Toast.LENGTH_SHORT);
	        t.show();
	        
	        if (responseBody != null) {
	        	game.setGameID(responseBody);
	        }
	        else {
	        	System.err.println("Response body was null.");
	        }
		} catch (HttpResponseException e) {
	    	System.err.println("DEBUG: POST failed: HttpResponseException.");
	        displayPostFailureError(context);
	    } catch (ClientProtocolException e) {
	    	System.err.println("DEBUG: POST failed: ClientProtocolException.");
	        displayPostFailureError(context);
	    } catch (IOException e) {
	    	System.err.println("DEBUG: POST failed: IOException.");
	    	e.printStackTrace();
	        displayPostFailureError(context);
	    }
	}
	
	private static void displayPostFailureError(Context context) {
		Toast t = Toast.makeText(context, "Failed to upload game.", Toast.LENGTH_SHORT);
		t.show();
	}
}
