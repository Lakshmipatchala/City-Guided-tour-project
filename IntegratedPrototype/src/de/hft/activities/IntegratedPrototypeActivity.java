package de.hft.activities;

import org.json.JSONException;
import org.json.JSONObject;

/*import virat.android.R;
import virat.android.Route;
import virat.android.TestAndroidActivity;*/

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class IntegratedPrototypeActivity extends ListActivity {
    TextView selection;
    String[] names;

    public void onCreate(Bundle savedInstanceState)

    {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
       names = new String[] { "Select your Tour", "BarcodeScanner"};
       this.setListAdapter(new ArrayAdapter<String>(this, R.layout.custom, R.id.label , names));
       selection=(TextView)findViewById(R.id.label);
    }
    public void onListItemClick(ListView parent, View v,int position,long id)
    {
    	if (position == 0)
    	{
    		Intent i = new Intent(this, Route.class);
     	    startActivity(i);
    	}
    	
    	if (position == 1) 
    	{
    	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    	}

    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                //TextView t = new TextView(this);
                //t = (TextView)findViewById(R.id.resultField);
                //t.setText(contents);
                String url = contents;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }
    
    protected void onStart() {//activity is started and visible to the user
  	  
  	  super.onStart();  
  	 }
  	 @Override
  	 protected void onResume() {//activity was resumed and is visible again
  	
  	  super.onResume();
  	   
  	 }
  	 @Override
  	 protected void onPause() { //device goes to sleep or another activity appears
  	  //another activity is currently running (or user has pressed Home)
  	  super.onPause();
  	   
  	 }
  	 @Override
  	 protected void onStop() { //the activity is not visible anymore
  	  
  	  super.onStop();
  	   
  	 }
  	 @Override
  	 protected void onDestroy() {//android has killed this activity
  	  
  	   super.onDestroy();
  	 }
}