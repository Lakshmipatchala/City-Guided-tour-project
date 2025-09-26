package de.hft.activity;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
 * This is the initial Manual Trip screen where a category is selected 
 * from a list in order to display its elements and to select the desired
 * points of interest to be visited from the specific category 
 * @author alfonso
 *
 */

public class ManualTripActivity extends Activity{


	JSONObject json;
	ListView listView;
	List<double[]> listForPoiCoordinates = new LinkedList<double[]>();
	private String[] menuItems;
	private String[] menuIds;
	// used link position of the menuitems with the POI id
	LinkedHashMap<Integer, Integer> listForMenuPositionAndPoiId = new LinkedHashMap<Integer, Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manual_trip_activity);
		setContentView(R.layout.manual_trip_activity);
		listView = (ListView) findViewById(R.id.listView);
		

		String poiUrl = getString(R.string.server_url).concat("/LoadPointOfInterestById?id=");

		 
		try {

			String object = JsonConnection.getStringRepresentation(getString(R.string.server_url).concat("/LoadAllCategories"));
			
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
			
			JSONObject route = new JSONObject(object);

			String[] arrayPoiId = null;

			arrayPoiId = new String[route.getJSONArray("pointsOfInterest").length()];
			menuItems = new String[arrayPoiId.length];
			for (int i = 0; i < arrayPoiId.length; i++) {
				
				arrayPoiId[i] = String.valueOf(route.getJSONArray("pointsOfInterest").get(i));
				String object2 = JsonConnection.getStringRepresentation(poiUrl + arrayPoiId[i]);
				JSONObject poi = new JSONObject(object2);
				menuItems[i] = poi.getString("name");
				double[] poiCoordinates = new double[3];
				poiCoordinates[0] = poi.getDouble("latitude");
				poiCoordinates[1] = poi.getDouble("longitude");
				poiCoordinates[2] = poi.getDouble("id");
				listForPoiCoordinates.add(poiCoordinates);
				listForMenuPositionAndPoiId.put(i,Integer.valueOf(arrayPoiId[i]));

			}
			
		} catch (JSONException e) {
			Log.e("MyStuff", Log.getStackTraceString(e));

		}

		listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, menuItems));

		 ListView lv = listView;
		 lv.setTextFilterEnabled(true);
	
	     listView.setItemsCanFocus(false);
		 
		 lv.setOnItemClickListener(new OnItemClickListener() {
		 public void onItemClick(AdapterView<?> parent, View view,
		 int position, long id) {
			 
				Bundle bundle = new Bundle();
				
				String anId = menuIds[position];
				
				Intent i = new Intent(ManualTripActivity.this, ManualTripPoiActivity.class);
				bundle.putString("categoryId", anId);
				i.putExtras(bundle);
				startActivityForResult(i,0);
			 
		 }
		 });

	}
}
