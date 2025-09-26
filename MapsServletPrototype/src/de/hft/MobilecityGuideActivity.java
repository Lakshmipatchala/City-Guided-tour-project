package de.hft;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MobilecityGuideActivity extends Activity {
    /** Called when the activity is first created. */
	
	TextView servlet;
	TextView maps;
	TextView mapsServlets;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        servlet = (TextView)findViewById(R.id.textViewServlets);
        
        servlet.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				 Intent myIntent = new Intent(v.getContext(), Servlets.class);
	                startActivityForResult(myIntent, 0);
				return false;
			}
		});
        
        maps = (TextView)findViewById(R.id.textViewMaps);
        
        maps.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Maps.class);
                startActivityForResult(myIntent, 0);
				
			}
		});
        
        mapsServlets =(TextView)findViewById(R.id.textViewMapsServlets);
        
        mapsServlets.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				Intent myIntent = new Intent(v.getContext(),MapsServlet.class);
				startActivityForResult(myIntent, 0);
				return false;
			}
		});
        
    }
}