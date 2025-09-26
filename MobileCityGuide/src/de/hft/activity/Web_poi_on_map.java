package de.hft.activity;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import de.hft.R;
import de.hft.common.JsonConnection;
import de.hft.map.Map;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class Web_poi_on_map extends Activity 
{
	//String poiUrl = getString(R.string.server_url).concat("/LoadPointsOfInterestByCategory?id=");
	String poiUrl = "http://hft.dyndns.biz:8080/MobileCityGuideServer/LoadPointsOfInterestByCategory?id=";
	WebView mWebView;
	String POI_id;
	List<double[]> listForPoiCoordinates = new LinkedList<double[]>();
	Button start;

	// public static final String URL = "";
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_poi_on_map);
		
		start = (Button) findViewById(R.id.mapButton);
		
		String turl = getIntent().getStringExtra("URL");
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(turl);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setWebViewClient(new HelloWebViewClient());

		Bundle b1 = getIntent().getExtras();
		POI_id = b1.getString("POI_id");

		String object2 = JsonConnection.getStringRepresentation(poiUrl + POI_id);
		JSONObject poi;
		try 
		{
			poi = new JSONObject(object2);

			double[] poiCoordinates = new double[3];

			poiCoordinates[0] = poi.getDouble("latitude");
			poiCoordinates[1] = poi.getDouble("longitude");
			poiCoordinates[2] = poi.getDouble("id");
			listForPoiCoordinates.add(poiCoordinates);
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) 
			{
					Bundle bundle = new Bundle();
					for (int i = 0; i < listForPoiCoordinates.size(); i++) 
					{
						bundle.putDoubleArray("poi" + i,listForPoiCoordinates.get(i));
					}
				Intent myIntent = new Intent(Web_poi_on_map.this, Map.class);
				myIntent.putExtras(bundle);
				startActivityForResult(myIntent, 0);
			}
		});

	}

	private class HelloWebViewClient extends WebViewClient 
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) 
		{
			view.loadUrl(url);
			return true;
		}
	}

}
