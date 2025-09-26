package de.hft.activities;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import de.hft.activities.R;
import de.hft.common.Connection;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TourInformation extends Activity {

	Button start;
	TextView tourDescription;
	JSONObject json;
	ListView listView;
	Connection con;
	List<double[]> listForPoiCoordinates = new LinkedList<double[]>();
	private String[] menuItems = new String[3];
	LinkedHashMap<Integer, Integer> aaa = new LinkedHashMap<Integer, Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tourinformation);

		start = (Button) findViewById(R.id.button1);
		tourDescription = (TextView) findViewById(R.id.tourDescription);
		listView = (ListView) findViewById(R.id.listView);

		// String url =
		// "http://192.168.43.189:8080/CG/LoadPointOfInterestById?id=";
		// String url =
		// "http://192.168.43.38:8080/CG/LoadPointOfInterestById?id=";
		// String url = "http://10.0.2.2:8080/CG/LoadPointOfInterestById?id=";
		String poiUrl = "http://10.0.2.2:8080/CityGuideServerPrototype/LoadPointOfInterestById?id=";
		String routeUrl = "http://10.0.2.2:8080/CityGuideServerPrototype/LoadRouteById?id=";
		
		tourDescription
				.setText("The mixed tour will guide you through the New Castle, the Stuttgart Central Station and the Mercedes-Benz Museum");

		Bundle bundle = this.getIntent().getExtras();
		Connection routeConnection = new Connection(routeUrl
				+ bundle.getString("routeId"));
		JSONObject route = routeConnection.getJSONObject();
		try {
			tourDescription.setText(route.getString("description"));

			String[] arrayPoiId = null;

			String poiId = route.getString("poiId");
			poiId = poiId.replaceAll(",", "");
			arrayPoiId = new String[poiId.length()];
			for (int i = 0; i < arrayPoiId.length; i++) {
				arrayPoiId[i] = String.valueOf(poiId.charAt(i));

				Connection con = new Connection(poiUrl + arrayPoiId[i]);
				JSONObject poi = con.getJSONObject();

				menuItems[i] = poi.getString("name");
				double[] poiCoordinates = new double[3];
				poiCoordinates[0] = poi.getDouble("latitude");
				poiCoordinates[1] = poi.getDouble("longitude");
				poiCoordinates[2] = poi.getDouble("id");
				listForPoiCoordinates.add(poiCoordinates);
				
				aaa.put(i,Integer.valueOf(arrayPoiId[i]));

			}
			
			for(int i=0; i<arrayPoiId.length;i++){
				//aaa.put(arrayPoiId[i], "");
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
			 String webUrl= "http://10.0.2.2:8080/CityGuideServerPrototype/PresentPointOfInterestById.jsp?id=";
			 
			 Intent i = new Intent(TourInformation.this, Web.class);
			  i.putExtra("URL",webUrl+aaa.get(position));
			  startActivity(i);
			 
//			 Intent i = new Intent(Intent.ACTION_VIEW);
//			  i.setData(Uri.parse(webUrl+aaa.get(position)));
//			  startActivity(i);
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
				startActivityForResult(myIntent, 0);
			}
		});

	}
}
