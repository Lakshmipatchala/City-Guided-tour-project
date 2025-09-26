package de.hft.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.hft.R;
import de.hft.map.Map;


/** 
 * This class is called when the activity is first created.
 * it displays the main menu in a list view 
 * 
 * 
 * @author virat
 * 
 **/
public class MainMenuActivity extends ListActivity {
	

	/**
	 * subclass to create custom adaptor for list view 
	 * @author virat
	 *
	 */
	public class MyCustomAdapter extends ArrayAdapter<String> {

		public MyCustomAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			
		}

		/**
		 * sets the list view and selects different image icons in list view menu  
		 * @author virat
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.custom, parent, false);
			TextView label = (TextView) row.findViewById(R.id.main_menu_item);
			label.setText(MainMenuItems[position]);
			ImageView icon = (ImageView)row.findViewById(R.id.icon);

			if (MainMenuItems[position] == "Select your tour") 
			{
				icon.setImageResource(R.drawable.icon_1a);
			} 
			if (MainMenuItems[position] == "Plan your tour") 
			{
				icon.setImageResource(R.drawable.icon_2a);
			}
			if (MainMenuItems[position] == "Map and location") 
			{
				icon.setImageResource(R.drawable.icon_3a);
			}
			if (MainMenuItems[position] == "Know your city") 
			{
				icon.setImageResource(R.drawable.icon_4a);
			}
			if (MainMenuItems[position] == "Your reviews and photos") 
			{
				icon.setImageResource(R.drawable.icon_5a);
			}


			return row;
		}
	}

	TextView selection;
	String[] names;

	/**
	 * string containing items to be displayed in main menu
	 */
	String[] MainMenuItems = { "Select your tour", "Plan your tour",
			"Map and location", "Know your city", "Your reviews and photos" };

	
	/**
	 * method to be executed on create of activity
	 * checks for internet connectivity and redirects to settings if internet not available or exits the application
	 * @author virat
	 */
	public void onCreate(Bundle savedInstanceState)

	{
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		// names = new String[] {"Select your Tour", "BarcodeScanner"};
		// this.setListAdapter(new ArrayAdapter<String>(this, R.layout.custom,
		// R.id.label , names));
		// selection=(TextView)findViewById(R.id.label);
		setListAdapter(new MyCustomAdapter(MainMenuActivity.this,
				R.layout.custom, MainMenuItems));
		
		final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) 
		{
		    //notify user you are online
		} 
		else 
		{
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Internet not connected, please take action!")
			       .setCancelable(false)
			       .setPositiveButton("data settings", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	    Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
			   				ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings");
			   				intent.setComponent(cName);
			   			//	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			   			//	intent.setAction(Settings.ACTION_DATA_ROAMING_SETTINGS);
			   				startActivity(intent);
			           }
			       })
			       .setNeutralButton("wi-fi settings", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
							final Intent intent = new Intent(Intent.ACTION_MAIN, null);
							intent.addCategory(Intent.CATEGORY_LAUNCHER);
							final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
							intent.setComponent(cn);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity( intent);
			           }
			       })
			       .setNegativeButton("Exit", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			                MainMenuActivity.this.finish();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
			
			
		} 
		
	}

	
	/**
	 * method to launch different activities on click of items in list view
	 * @author virat
	 */
	public void onListItemClick(ListView parent, View v, int position, long id) {
		// launching select your tour
		if (position == 0) {

			Intent i = new Intent(MainMenuActivity.this, /*MainMenuActivity.class*/Route.class);
			startActivity(i);

		}

		// launching plan your tour intent
		if (position == 1) {
			Intent intent1 = new Intent(MainMenuActivity.this,
					ManualTripActivity.class);
			startActivity(intent1);
		}

		// launching map and location intent
		if (position == 2) {
			
			Intent intent2 = new Intent(this,Map.class);
			intent2.setPackage(this.getClass().getName());
	        startActivity(intent2);
	        
		}

		// launching city info intent
		if (position == 3) 
		{
			Intent intent3 = new Intent(MainMenuActivity.this,KnowYourCityActivity.class);
			startActivity(intent3);
		}

		// launching review and rating intent
		if (position == 4) 
		{
			Intent intent4 = new Intent(MainMenuActivity.this,ReviewActivity.class);
			startActivity(intent4);
		}

	}

	
	/**
	 * method to handle results from barcode scanner
	 * @author virat
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				String url = contents;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

	protected void onStart() {
		super.onStart();
	}

	/**
	 * activity was resumed and is visible again
	 * check again for internet connection presence and redirects to settings, if not available
	 */
	@Override
	protected void onResume() {// 

		super.onResume();
		final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) 
		{
		    //notify user you are online
		} 
		else 
		{
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Internet not connected, please take action!")
			       .setCancelable(false)
			       .setPositiveButton("Data settings", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	    Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
			   				ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings");
			   				intent.setComponent(cName);
			   			//	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			   			//	intent.setAction(Settings.ACTION_DATA_ROAMING_SETTINGS);
			   				startActivity(intent);
			           }
			       })
			       .setNeutralButton("Wi-fi settings", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
							final Intent intent = new Intent(Intent.ACTION_MAIN, null);
							intent.addCategory(Intent.CATEGORY_LAUNCHER);
							final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
							intent.setComponent(cn);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity( intent);
			           }
			       })
			       .setNegativeButton("Exit", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			                MainMenuActivity.this.finish();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
			
			
		} 
		

	}

	
	/**
	 * device goes to sleep or another activity appears 
	 * another activity is currently running (or user has pressed Home)
	 */
	@Override
	protected void onPause() { 
		super.onPause();

	}
	/**
	 * the activity is not visible anymore
	 */
	@Override
	protected void onStop() {  

		super.onStop();

	}

	/**
	 * android has killed this activity
	 */
	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
}