package de.hft.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;

import de.hft.R;
import de.hft.common.JsonConnection;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * class to provide the android search manager functionality
 * reads all POI values from the server and let the user enter the string
 * compares user strings with each POI and displays results in a list appropriately
 * 
 * @author viratzz
 *
 */
public class SearchableActivity extends ListActivity {

	private String[] values;
	private ArrayAdapter<String> adapter;
	private String[] valueIds;
	LinkedHashMap<String, String> a = new LinkedHashMap<String, String>();
	ArrayList<String> list1 = new ArrayList<String>();
	ArrayList<String> s1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		values = new String[] { "Android", "Android2", "Andere", "iPhone",
				"Windows7" };
		
		String loadPoiUrl = getString(R.string.server_url).concat("/LoadAllPointsOfInterest");
		String object = JsonConnection.getStringRepresentation(loadPoiUrl);
		JSONArray json = null;
		try 
		{
			json = new JSONArray(object);
		} 
		catch (JSONException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		values = new String[json.length()];
		valueIds = new String[json.length()];
		
		//getting all POI name values in a string
		try 
		{	
			for (int i = 0; i < json.length(); i++) 
			{
				values[i]= json.getJSONObject(i).getString("name");		
			}
			
		} 
		catch (JSONException e) 
		{
			Log.e("MyStuff",Log.getStackTraceString(e));
		}
		
		//getting all IDs for all POIs in a string
		try 
		{	
			for (int i = 0; i < json.length(); i++) 
			{
				//valueIds[i]= json.getJSONObject(i).getString("id");		
				a.put(json.getJSONObject(i).getString("id"), json.getJSONObject(i).getString("name"));
			}
			
		} 
		
		catch (JSONException e) 
		{
			Log.e("MyStuff",Log.getStackTraceString(e));
		}
		
		
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1);
		setListAdapter(adapter);
		performSearch();

	}

	/**
	 * handling the action on click of result list items
	 * sends the name and ID of selected POI to next activity
	 */
	public void onListItemClick(ListView parent, View v, int position, long id) 
	{
		String POI_name = (String) this.getListView().getItemAtPosition(position);
		
		String idToSend = list1.get(position);
		
		//anId = anId.toString();
		Bundle b = new Bundle();
		Bundle b1 = new Bundle();
		
		b.putString("POI_name", POI_name);
		b1.putString("POI_id", idToSend);
		
		Intent myintent = new Intent(SearchableActivity.this, ReviewPageActivity.class);
		myintent.putExtras(b);
		myintent.putExtras(b1);
		
		startActivity(myintent);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		adapter.clear();
		setIntent(intent);
		performSearch();
	}

	/**
	 * method responsible to perform the search on the data retrived from server
	 * and displays the results
	 */
	private void performSearch() {
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			findMatches(query);
			setTitle("Search results for: " + query);
		}

	}
	
	
	/**
	 * method to perform the real search
	 * takes parameter- query - the user entered string
	 * 
	 * @param query
	 */
	private void findMatches(String query) 
	{
		    for (String s : values) 
		    {
		    	if (s.toLowerCase().contains(query.toLowerCase())) 
		    	{
		    		adapter.add(s);	
		    		
		    		 s1 = getKeysFromValue(a, s);
		    		 list1.addAll(s1);	    		
		    		
		    	}
		    }	
	}
	
	
	public static ArrayList<String> getKeysFromValue(Map<String, String> hm, String value)
	{
	    ArrayList <String>list = new ArrayList<String>();
	    for(String o:hm.keySet())
	    {
	        if(hm.get(o).equals(value)) 
	        {
	            list.add(o);
	        }
	    }
	    return list;
	}
	
}
