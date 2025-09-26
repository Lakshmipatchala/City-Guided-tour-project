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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import de.hft.R;
import de.hft.common.JsonConnection;
import de.hft.map.Map;

public class TourInformation extends Activity{

	Button start;
	TextView tourDescription;
	TextView estimatedTime;
	RatingBar tourRating;
	
	JSONObject json;
	ListView listView;
	List<double[]> listForPoiCoordinates = new LinkedList<double[]>();
	private String[] menuItems;
	// used link position of the menuitems with the POI id
	LinkedHashMap<Integer, Integer> listForMenuPositionAndPoiId = new LinkedHashMap<Integer, Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tourinformation);
		setContentView(R.layout.tourinformation);
		start = (Button) findViewById(R.id.button1);
		tourDescription = (TextView) findViewById(R.id.tourDescription);
		listView = (ListView) findViewById(R.id.listView);
		estimatedTime = (TextView) findViewById(R.id.estimatedTime);
		tourRating = (RatingBar) findViewById(R.id.tourRating);
		

		String poiUrl = getString(R.string.server_url).concat("/LoadPointOfInterestById?id=");
		//String poiUrl = "http://hft.dyndns.biz:8080/MobileCityGuideServer/LoadPointOfInterestById?id=";
		String routeUrl = getString(R.string.server_url).concat("/LoadRouteById?id=");
		//String routeUrl = "http://hft.dyndns.biz:8080/MobileCityGuideServer/LoadRouteById?id=";
		
		Bundle bundle = this.getIntent().getExtras();
		try {
			String object = JsonConnection.getStringRepresentation(routeUrl+ bundle.getString("routeId"));
			JSONObject route = new JSONObject(object);
			tourDescription.setText(route.getString("description"));
			
			String estTime = route.getString("estimatedTime");
			estimatedTime.setText("Estimated trip time : " + estTime );
			
			float num = Float.valueOf(route.getString("rating").trim()).floatValue();
			double ratingVal = Math.round(num*10.0)/10.0;
			//Float.valueOf(route.getString("rating").trim()).floatValue(); 
			tourRating.setRating((float) ratingVal);
			

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
		
		 lv.setOnItemClickListener(new OnItemClickListener() {
		 public void onItemClick(AdapterView<?> parent, View view,
		 int position, long id) {
			 String webUrl = getString(R.string.server_url).concat("/PresentPointOfInterestById.jsp?id=");
			 //String webUrl = "http://hft.dyndns.biz:8080/MobileCityGuideServer/PresentPointOfInterestById.jsp?id=";
			 
			 Intent i = new Intent(TourInformation.this, Web.class);
			  i.putExtra("URL",webUrl+listForMenuPositionAndPoiId.get(position));
			  startActivity(i);
			 
		 }
		 });

		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Bundle bundle = new Bundle();

				for (int i = 0; i < listForPoiCoordinates.size(); i++) {
					bundle.putDoubleArray("poi" + i,
							listForPoiCoordinates.get(i));
				}
				Intent myIntent = new Intent(TourInformation.this, Map.class);
				myIntent.putExtras(bundle);
				myIntent.setPackage(this.getClass().getName());
				startActivityForResult(myIntent, 0);
			}
		});

	}
}
