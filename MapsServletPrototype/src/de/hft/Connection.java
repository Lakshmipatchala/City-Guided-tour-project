package de.hft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class Connection {

	JSONObject jsonResponse = null;
	
	public Connection(String url){
		InputStream in = callService(url);
		if (in != null) {
			try {
				 this.jsonResponse = new JSONObject(
						convertStreamToString(in));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public JSONObject getJSONObject(){
		return this.jsonResponse;
	}
	

	/**
	 * @param envelope
	 */
	InputStream callService(String servletUrl) {
		InputStream in = null;

		try {
			URL url = new URL(servletUrl);
			URLConnection conn = url.openConnection();

			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("POST");

			httpConn.setDoOutput(true);
			httpConn.connect();

			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return in;
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
