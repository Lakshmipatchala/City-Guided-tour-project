package de.hft.activity;

import de.hft.R;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ReviewActivity extends Activity
{
		@Override
		public void onCreate(Bundle savedInstanceState) 
		{
		    super.onCreate(savedInstanceState);
		    setContentView(R.layout.review_layout);
		    setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		    
		    final Button button = (Button) findViewById(R.id.searchButton);
	        button.setOnClickListener(new View.OnClickListener() 
	        {
	            public void onClick(View v) 
	            {
	            	onSearchRequested();
	            }
	        });
		    	    
		}
}