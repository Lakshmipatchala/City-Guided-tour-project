package de.hft.activities;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hft.common.Connection;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Route extends ListActivity {
	private static final String URL = "http://10.0.2.2:8080/CityGuideServerPrototype/LoadRouteIndex";
	//private static final String URL = "http://192.168.43.189:8080/CG/LoadRouteIndex";
	private String[] menuItems;
	private String[] menuIds;
	private List<String> list = new LinkedList<String>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JSONObject json = new JSONObject();
		Connection server = new Connection(URL);
		
		menuItems=new String[server.getJSOnArray().length()];
		
		//menuIds = new String[server.getJSONArray().length()];
		
		menuIds = new String[server.getJSOnArray().length()];
		
		
		
		//menuItems=new String[3];
		try {
			
			for (int i = 0; i < server.getJSOnArray().length(); i++) 
			{
			
				menuItems[i]= server.getJSOnArray().getJSONObject(i).getString("name");
				
			}
			
		} catch (JSONException e) {
			Log.e("MyStuff",Log.getStackTraceString(e));
		}

		try {
			
			for (int i = 0; i < server.getJSOnArray().length(); i++) 
			{
			
				menuIds[i]= server.getJSOnArray().getJSONObject(i).getString("id");
				
			}
			
		} catch (JSONException e) {
			Log.e("MyStuff",Log.getStackTraceString(e));
		}
		
		
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Bundle bundle = new Bundle();
				
				String anId = menuIds[position];
				
				
				Intent i = new Intent(Route.this, TourInformation.class);
				bundle.putString("routeId", anId);
				i.putExtras(bundle);
				startActivityForResult(i,0);
				
				
	        	  // startActivity(i);   
			}
		});
		
		/*start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	Bundle bundle = new Bundle();
            	
            	for (int i = 0; i < listForPoiCoordinates.size(); i++) {
					bundle.putDoubleArray("poi"+i, listForPoiCoordinates.get(i));
				}            	            	
                Intent myIntent = new Intent(TourInformation.this, Map.class);
                myIntent.putExtras(bundle);
                startActivityForResult(myIntent,0);
            }
        });*/
		
	}

}