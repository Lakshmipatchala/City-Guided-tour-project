package de.hft.activity;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hft.R;
import de.hft.common.JsonConnection;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


/**
 * class to display list of points of interests in a selected category
 * on click of selected POI, it redirects to web view to display further information  
 * @author viratzz
 *
 */
public class KnowYourCityPoiActivity extends Activity
{
	ListView listView;
	private String[] POIitems;
	private String[] POIids;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); // call super class constructor

		setContentView(R.layout.city_info_poi_layout);
		listView = (ListView) findViewById(R.id.POIList);
			
		// getting the list of POIs in selected category
		String poiUrl = getString(R.string.server_url).concat("/LoadPointsOfInterestByCategory?id=");
		//String poiUrl = "http://hft.dyndns.biz:8080/MobileCityGuideServer/LoadPointsOfInterestByCategory?id=";
		
		//getting clicked category ID from previous activity
		Bundle b = getIntent().getExtras();

		String value = b.getString("categoryId");
		
		JSONArray POI = null;
		
		String[] arrayPoiId = null;
		try 
		{
			String object = JsonConnection.getStringRepresentation(poiUrl+ value);
			
			POI = new JSONArray(object);
			//getting the length of JSON array
			
			POIitems = new String[POI.length()];
			POIids = new String[POI.length()];
			
			for (int i=0; i<POI.length(); i++)
			{
				//filling arrary with POI name and POI id values
				POIitems[i] = POI.getJSONObject(i).getString("name");
				POIids[i] = POI.getJSONObject(i).getString("id");
			}
		} 
		catch (JSONException e) 
		{
			Log.e("MyStuff",Log.getStackTraceString(e));
		}
		
		
		
		
		//setting values in list view
		listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, POIitems));

		 ListView lv = listView;
		 lv.setTextFilterEnabled(true);
		 
		 //starting web browser to display description, on item click 
		 lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
				{
					String webUrl = getString(R.string.server_url).concat("/PresentPointOfInterestById.jsp?id=");
					//String webUrl= "http://hft.dyndns.biz:8080/MobileCityGuideServer/PresentPointOfInterestById.jsp?id=";
					String anId = POIids[position];
					Intent i = new Intent(KnowYourCityPoiActivity.this, Web_poi_on_map.class);
					i.putExtra("URL",webUrl+anId);
					
					Bundle b1 = new Bundle();
					b1.putString("POI_id", anId);
					i.putExtras(b1);
					
					startActivity(i);
				}
				});	 
	}
	
}
