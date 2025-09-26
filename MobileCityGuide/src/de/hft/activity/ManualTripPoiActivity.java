package de.hft.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import de.hft.R;
import de.hft.common.JsonConnection;
import de.hft.map.Map;
import de.hft.pojo.Item;

/**
 * This is the selection screen for POIs by category, when a category is selected in the
 * ManualTripActivity screen, a selectable list of POIs is displayed, the user can select
 * the desired POIs he wants to visit and on the click of the "start" button the map is
 * displayed with the POIs selected.
 * @author alfonso
 *
 */

public class ManualTripPoiActivity extends Activity
{
	Button start; 
	ListView listView;
	private String[] POIitems;
	private String[] POIids;
	private ItemListAdapter adapter; //
	private List<Item> data; //
	List<double[]> listForPoiCoordinates = new LinkedList<double[]>();	
    LinkedHashMap<Integer, Integer> listForMenuPositionAndPoiId = new LinkedHashMap<Integer, Integer>();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); // call super class constructor
		setContentView(R.layout.manual_trip_poi_layout);
		listView = (ListView) findViewById(R.id.POIList);
		start = (Button) findViewById(R.id.button);
		data = new ArrayList<Item>(10); //
		
		String poiUrl = getString(R.string.server_url).concat("/LoadPointsOfInterestByCategory?id=");
		
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
				data.add(new Item(POI.getJSONObject(i).getInt("id"), POI.getJSONObject(i).getString("name")));
				
				double[] poiCoordinates = new double[3];
				poiCoordinates[0] = POI.getJSONObject(i).getDouble("latitude");
				poiCoordinates[1] = POI.getJSONObject(i).getDouble("longitude");
				poiCoordinates[2] = POI.getJSONObject(i).getDouble("id");
				listForPoiCoordinates.add(poiCoordinates);
			}
		} 
		catch (JSONException e) 
		{
			Log.e("MyStuff",Log.getStackTraceString(e));
		}
		
		// Create the adapter to render our data
		// --
		adapter = new ItemListAdapter(this, data);
//		setListAdapter(adapter);		
		
		
		//setting values in list view
		listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, POIitems));

		 ListView lv = listView;
		 lv.setTextFilterEnabled(true);

		 listView.setItemsCanFocus(false);
	     listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

	     start.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

//						showSelectedItemIds();
//						showSelectedItems();
//						showPoiSelectedItems();
				
		final long[] checkedItemIds = listView.getCheckItemIds();	

		final int checkedItemsCount = checkedItemIds.length;
		
		Bundle bundle = new Bundle();		
		
		for (int i = 0; i < checkedItemsCount; ++i) {

			final int p = (int) (long) checkedItemIds[i];

			bundle.putDoubleArray("poi" + i,
									listForPoiCoordinates.get(p));
		}
		
		Intent myIntent = new Intent(ManualTripPoiActivity.this, Map.class);
		myIntent.putExtras(bundle);
		myIntent.setPackage(this.getClass().getName());
		startActivityForResult(myIntent, 0);

				}  
			});	    		 
 			

	}
	/**
	 * Show a message giving the selected item captions
	 */
	private void showSelectedItems() {
		final StringBuffer sb = new StringBuffer("POI name: ");

		// Get an array that tells us for each position whether the item is
		// checked or not
		// --
		final SparseBooleanArray checkedItems = listView
				.getCheckedItemPositions();
		if (checkedItems == null) {
			Toast.makeText(this, "No selection info available",
					Toast.LENGTH_LONG).show();
			return;
		}

		// For each element in the status array
		// --
		boolean isFirstSelected = true;
		final int checkedItemsCount = checkedItems.size();
		for (int i = 0; i < checkedItemsCount; ++i) {
			// This tells us the item position we are looking at
			// --
			final int position = checkedItems.keyAt(i);

			// This tells us the item status at the above position
			// --
			final boolean isChecked = checkedItems.valueAt(i);

			if (isChecked) {
				if (!isFirstSelected) {
					sb.append(", ");
				}
				sb.append(data.get(position).getCaption());
				isFirstSelected = false;
			}
		}

		// Show a message with the countries that are selected
		// --
//		Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
	}

	/**
	 * Show a message giving the selected item IDs. 
	 */
	
	private void showSelectedItemIds() {
		final StringBuffer sb = new StringBuffer("Position: ");

		// Get an array that contains the IDs of the list items that are checked
		// --
		final long[] checkedItemIds = listView.getCheckItemIds();
		if (checkedItemIds == null) {
			Toast.makeText(this, "No selection", Toast.LENGTH_LONG).show();
			return;
		}

		// For each ID in the status array
		// --
		boolean isFirstSelected = true;
		final int checkedItemsCount = checkedItemIds.length;
		for (int i = 0; i < checkedItemsCount; ++i) {
			if (!isFirstSelected) {
				sb.append(", ");
			}
			sb.append(checkedItemIds[i]);
			isFirstSelected = false;
		}

		// Show a message with the IDs that are selected
		// --
//		Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
	}

	/**
	 * Show a message giving the selected item IDs. 
	 */
	
	private void showPoiSelectedItems() {
		final StringBuffer sb = new StringBuffer("POI Id: ");

		// Get an array that contains the IDs of the list items that are checked
		// --
		final long[] checkedItemIds = listView.getCheckItemIds();
		if (checkedItemIds == null) {
			Toast.makeText(this, "No selection", Toast.LENGTH_LONG).show();
			return;
		}

		// For each ID in the status array
		// --
		boolean isFirstSelected = true;
		final int checkedItemsCount = checkedItemIds.length;
		for (int i = 0; i < checkedItemsCount; ++i) {
			if (!isFirstSelected) {
				sb.append(", ");
			}
			final int p = (int) (long) checkedItemIds[i];

			sb.append(POIids[p]);
			isFirstSelected = false;
		}

		// Show a message with the IDs that are selected
		// --
//		Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
	}

	/**
	 * Uncheck all the items
	 */
	private void clearSelection() {
		final int itemCount = listView.getCount();
		for (int i = 0; i < itemCount; ++i) {
			listView.setItemChecked(i, false);
		}
	}	
	
	 //starting a display description, on item click 
/*		 lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				String webUrl= "http://hft.dyndns.biz:8080/MobileCityGuideServer/PresentPointOfInterestById.jsp?id=";
				String anId = POIids[position];
				Intent i = new Intent(KnowYourCityPoiActivity.this, Web_poi_on_map.class);
				i.putExtra("URL",webUrl+anId);
				
				Bundle b1 = new Bundle();
				b1.putString("POI_id", anId);
				i.putExtras(b1);
				
				startActivity(i);
			}
			});*/	 
}
