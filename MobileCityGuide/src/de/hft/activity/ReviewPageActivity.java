package de.hft.activity;

import org.json.JSONException;
import org.json.JSONObject;

import de.hft.R;
import de.hft.common.JsonConnection;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewPageActivity extends Activity implements RatingBar.OnRatingBarChangeListener
{

	RatingBar rating; // declare RatingBar object
	TextView ratingText;// declare TextView Object
	TextView POI_description; //declaring description TextView object
	String valueId;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); // call super class constructor

		setContentView(R.layout.review_layout_screen2);
		ratingText=(TextView)findViewById(R.id.ratingText);// create TextView object
		rating=(RatingBar)findViewById(R.id.ratingBar);// create RatingBar object
		rating.setOnRatingBarChangeListener(this);
		POI_description = (TextView)findViewById(R.id.POI_description);
		
		
		TextView POI_name = (TextView)findViewById(R.id.POI_name);
		
		String poiUrl = getString(R.string.server_url).concat("/LoadPointOfInterestById?id=");
		//String poiUrl = "http://hft.dyndns.biz:8080/MobileCityGuideServer/LoadPointOfInterestById?id=";
		
		Bundle b = getIntent().getExtras();
		Bundle b1 = getIntent().getExtras();

		String value = b.getString("POI_name");
		valueId = b1.getString("POI_id");
		
		POI_name.setText(value);
		
		try 
		{
			String object = JsonConnection.getStringRepresentation(poiUrl+ valueId);
			JSONObject POI = new JSONObject(object);
			POI_description.setText(POI.getString("description"));
		}
		catch (JSONException e) 
		{
			Log.e("MyStuff", Log.getStackTraceString(e));
		}
		
		
		
		Button uploadPhotosButton = (Button)findViewById(R.id.photo_upload_button);
		
		uploadPhotosButton.setOnClickListener(uploadPhotoButtonListner);
	}
	
	private android.view.View.OnClickListener uploadPhotoButtonListner = new android.view.View.OnClickListener() 
	{
		
		@Override
		public void onClick(View v) 
		{
			
			Bundle b3 = new Bundle();
			b3.putString("POI_ID_image", valueId);
			
			Intent intent = new Intent(ReviewPageActivity.this, ImageUploadActivity.class);
			
			intent.putExtras(b3);
			
			/*ReviewPageActivity.this.startActivity(intent);*/
			startActivityForResult(intent,0);
			
		}
	};
	// implement abstract method onRatingChanged
	public void onRatingChanged(RatingBar ratingBar,float rating, boolean fromUser)
	{
		String POI_id = valueId;
		int POI_rating_int = (int)this.rating.getRating();
		String POI_rating = Integer.toString(POI_rating_int);
		
		String updateUrl = getString(R.string.server_url).concat("/UpdateRatingOfPointOfInterest?id=");
		String url = updateUrl + POI_id + "&rating=" + POI_rating;
		boolean b = JsonConnection.sendDataToServer(url);
		if (b == true)
		{
			Context context = getApplicationContext();
			CharSequence text = "Rating submitted successfully!";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		else
		{
			Context context = getApplicationContext();
			CharSequence text = "Some error occured while uploading rating, please try again";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		
		}
		
		ratingText.setText("Your rating :"+""+ (int)this.rating.getRating()); // display rating as number in TextView, use "this.rating" to not confuse with "float rating"
	}

}