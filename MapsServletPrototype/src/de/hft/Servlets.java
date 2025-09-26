package de.hft;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Servlets extends Activity {
	
	EditText input;
	TextView output;
	Button buttonClick;
	EditText url;
	String SERVLET_URL;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servlets);
        
        input = (EditText) findViewById(R.id.editText1);
        output = (TextView) findViewById(R.id.textView1);
        buttonClick = (Button) findViewById(R.id.button1);
        url = (EditText) findViewById(R.id.editText2);
        SERVLET_URL= url.getText().toString();
        buttonClick.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String result = getResultFromServlet(input.getText().toString());
				if (result != null) {
					display(result);
				} else {
					display("Not found");
				}
			}
		});
        
        
    }
    /**
	 * @param text
	 *            {@link Editable}
	 */
	protected void display(String text) {
		output.setText(text);
	}

	private String getResultFromServlet(String text) {
		String result = "";

		InputStream in = callService(text);
		if (in != null) {
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(convertStreamToString(in));
				result = "Longitude: " + jsonResponse.getString("longitude")+" Latitude: "+jsonResponse.getString("latitude");
			} catch (JSONException e) {
				result = "Error: JSON Object couldn't be made";
			}
		} else {
			result = "Error: Service not returning result";
		}
		return result;
	}

	/**
	 * @param envelope
	 */
	private InputStream callService(String text) {
		InputStream in = null;

		try {
			URL url = new URL(SERVLET_URL);
			URLConnection conn = url.openConnection();

			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("POST");
			//httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.connect();

//			DataOutputStream dataStream = new DataOutputStream(conn
//					.getOutputStream());
//
//			dataStream.writeBytes(text);
//			dataStream.flush();
//			dataStream.close();
			
			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			display("Error: Not not connect");
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
