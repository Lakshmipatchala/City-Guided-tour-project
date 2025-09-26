package de.hft.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import de.hft.R;
import de.hft.common.JsonConnection;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * class to display list of categories and option to start barcode scanner
 * 
 * @author viratzz
 * 
 */
public class KnowYourCityActivity extends Activity {
	// displaying barcode scanner item in list 1
	private String[] list1 = { "Barcode Scanner" };
	// static list in case of no response from server
	private String[] categoryList = { "Category 1", "Category 2", "Category 3",
			"Category 4", "Category 5", "Category 6", "Category 7",
			"Category 8", "Category 9", "Category 10" };
	private String[] categoryListIds;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// getting array of categories from the server
		
		String url_cat = getString(R.string.server_url).concat("/LoadAllCategories");
		String object = JsonConnection.getStringRepresentation(url_cat);
		JSONArray json = null;
		try {
			json = new JSONArray(object);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		categoryList = new String[json.length()];
		categoryListIds = new String[json.length()];

		try {
			for (int i = 0; i < json.length(); i++) {
				categoryList[i] = json.getJSONObject(i).getString("name");
			}

		} catch (JSONException e) {
			Log.e("MyStuff", Log.getStackTraceString(e));
		}

		try {
			for (int i = 0; i < json.length(); i++) {
				categoryListIds[i] = json.getJSONObject(i).getString("id");
			}
		} catch (JSONException e) {
			Log.e("MyStuff", Log.getStackTraceString(e));
		}

		setContentView(R.layout.city_info);

		ListView barcodescanner = (ListView) findViewById(R.id.BarcodeScanner);
		ListView categorylist = (ListView) findViewById(R.id.CategoryList);

		ArrayAdapter aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list1);
		ArrayAdapter aa1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, categoryList);

		barcodescanner.setAdapter(aa);
		categorylist.setAdapter(aa1);

		barcodescanner
				.setOnItemClickListener(new ListView.OnItemClickListener() {
					/**
					 * method to launch barcode scanner for result activity
					 * searches for already installed barcode scaner app in
					 * device, if not found redirects to android market to
					 * download one
					 */
					public void onItemClick(AdapterView parent, View view,
							int position, long id) {
						String intentToCheck = "com.google.zxing.client.android.SCAN";
						final PackageManager packageManager = getPackageManager();
						final Intent intent1 = new Intent(intentToCheck);
						List list = packageManager.queryIntentActivities(
								intent1, PackageManager.MATCH_DEFAULT_ONLY);
						final boolean isAvailable = list.size() > 0;

						if (!isAvailable) {
							Intent marketIntent = new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("market://details?id=com.google.zxing.client.android"));
							// Intent marketIntent = new
							// Intent(Intent.ACTION_VIEW,
							// Uri.parse("market://search?q=barcode scanner"));
							startActivity(marketIntent);
						}

						if (isAvailable) {
							Intent intent = new Intent(
									"com.google.zxing.client.android.SCAN");
							intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
							startActivityForResult(intent, 0);
						}

					}

				});

		categorylist.setOnItemClickListener(new ListView.OnItemClickListener() {
			/**
			 * handling click on items of category list launches new list of
			 * contained POIs in selected category
			 * 
			 */
			public void onItemClick(AdapterView parent, View view,
					int position, long id) {
				Bundle bundle = new Bundle();

				String anId = categoryListIds[position];

				Intent i = new Intent(KnowYourCityActivity.this,
						KnowYourCityPoiActivity.class);
				bundle.putString("categoryId", anId);
				i.putExtras(bundle);
				startActivityForResult(i, 0);

			}

		});

	}

	/**
	 * handling the result produced out of barcode scanner interpreting the
	 * scanned URL and launching web browser to display the required page
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				// TextView t = new TextView(this);
				// t = (TextView)findViewById(R.id.resultField);
				// t.setText(contents);

				// checking if scanned URL contains valid URL or not

				try {

					URL url = new URL(contents);
					String urli = contents;
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(urli));
					startActivity(i);

				} catch (MalformedURLException e) {
					Context context = getApplicationContext();
					CharSequence text = "No relavent information in the barcode!";
					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

	protected void onStart() {// activity is started and visible to the user

		super.onStart();
	}

	@Override
	protected void onResume() {// activity was resumed and is visible again

		super.onResume();

	}

	@Override
	protected void onPause() { // device goes to sleep or another activity
								// appears
		// another activity is currently running (or user has pressed Home)
		super.onPause();

	}

	@Override
	protected void onStop() { // the activity is not visible anymore

		super.onStop();

	}

	@Override
	protected void onDestroy() {// android has killed this activity

		super.onDestroy();
	}
}
