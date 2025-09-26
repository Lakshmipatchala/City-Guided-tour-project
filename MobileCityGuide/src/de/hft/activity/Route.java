package de.hft.activity;


import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.hft.R;
import de.hft.common.JsonConnection;

/**
 * class to show list of predefined routes
 * @author virat
 *
 */

public class Route extends ListActivity {
	private String[] menuItems;
	private String[] menuIds;
	
	/**
	 * subclass to get the list of pre defined tours from the server
	 */
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String routeUrl = getString(R.string.server_url).concat("/LoadRouteIndex");
		String object = JsonConnection.getStringRepresentation(routeUrl);
		JSONArray json = null;
		try {
			json = new JSONArray(object);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		menuItems=new String[json.length()];
		
		
		menuIds = new String[json.length()];
		
		
		try {
			
			for (int i = 0; i < json.length(); i++) 
			{
				menuItems[i]= json.getJSONObject(i).getString("name");	
			}
			
		} catch (JSONException e) {
			Log.e("MyStuff",Log.getStackTraceString(e));
		}

		try {
			
			for (int i = 0; i < json.length(); i++) 
			{
				menuIds[i]= json.getJSONObject(i).getString("id");	
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
			}
		});
			
	}

}