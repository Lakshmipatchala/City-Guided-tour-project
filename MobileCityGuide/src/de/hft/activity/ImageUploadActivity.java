package de.hft.activity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import de.hft.R;
import de.hft.common.JsonConnection;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * class to upload user images to server
 * user can select the image from android image gallery
 * 
 * @author viratzz
 *
 */


public class ImageUploadActivity extends Activity {
	private static final int PICK_IMAGE = 1;
	private ImageView imgView;
	private Button upload;
	private EditText caption;
	private Bitmap bitmap;
	private ProgressDialog dialog;
	String POI_id_value;
	File file;
	FileInputStream fi;
	
	String filePath;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageupload);

		Bundle b3 = this.getIntent().getExtras();
		POI_id_value = b3.getString("POI_ID_image");

		imgView = (ImageView) findViewById(R.id.ImageView);
		upload = (Button) findViewById(R.id.Upload);
		//caption = (EditText) findViewById(R.id.Caption);
		
		Context con = getApplicationContext();
        CharSequence text = "Tap on options menu to choose a photo from gallery";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(con, text, duration);
        toast.show();
		
		/**
		 * handling the action performed on click of upload button
		 * asking to select image, in case of no image selected
		 * executing imageUploadTask otherwise
		 */
        upload.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (bitmap == null) {
					Toast.makeText(getApplicationContext(),
							"Please select image by pressing options menu", Toast.LENGTH_SHORT).show();
				} else {
					dialog = ProgressDialog.show(ImageUploadActivity.this,
							"Uploading", "Please wait...", true);
					new ImageUploadTask().execute();
				}
			}
		});

	}

	/**
	 * creates an option menu and inflates it with gallery link
	 * @param menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.imageupload_menu, menu);
		return true;
	}

	/**
	 * opens android gallery on click of item in options menu.
	 * can be extended to open camera also
	 * 
	 * @param item (from option's menu)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.ic_menu_gallery:
			try {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						PICK_IMAGE);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * handling the action on selection of image from gallery
	 * setting the path of selected image by calling getPath() method
	 * filling the image view with user's selected image by calling decodeFile() method
	 * 
	 * @param requestcode - to determine from where the result is returned
	 * @param resultcode - to get the result
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();
				filePath = null;

				try {
					// OI FILE Manager
					String filemanagerstring = selectedImageUri.getPath();

					// MEDIA GALLERY
					String selectedImagePath = getPath(selectedImageUri);

					if (selectedImagePath != null) {
						filePath = selectedImagePath;
					} else if (filemanagerstring != null) {
						filePath = filemanagerstring;
					} else {
						Toast.makeText(getApplicationContext(), "Unknown path",
								Toast.LENGTH_LONG).show();
						Log.e("Bitmap", "Unknown path");
					}
					
					file = new File(filePath);
					fi = new FileInputStream(file);
					
					
					
					if (filePath != null) {
						decodeFile(filePath);
					} else {
						bitmap = null;
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Internal error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}
			}
			break;
		default:
		}
	}

	/**
	 * subclass to handle image upload task
	 * runs Asynchronously in background
	 * creates a bitmap by decoding the image file, compresses the file into JPG, resizes it
	 * converts it into byte stream
	 * uses httpmime jar from apache to send the file to server over http
	 * 
	 * @author viratzz
	 *
	 */
	class ImageUploadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... unsued) {
			Bitmap bitmap;
			String exsistingFileName = filePath;
			
			 try {  
				  
			        HttpClient httpClient = new DefaultHttpClient();  
			        HttpContext localContext = new BasicHttpContext();  
			  
			        // setting the URL of server + ID of the selected POI
			        
			        String url_string = getString(R.string.server_url).concat("/AddPictureToPointOfInterest?id=");
			        //String url_string = "http://hft.dyndns.biz:8080/MobileCityGuideServer/AddPictureToPointOfInterest?id=";
					String url_id_pic = url_string + POI_id_value;
			  
					HttpPost httpPost = new HttpPost(url_id_pic); 
			        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
			        bitmap = BitmapFactory.decodeFile(exsistingFileName);  
			  
			         
			        //resizing image to 640 x 480  
			  
			        Bitmap bmpCompressed = Bitmap.createScaledBitmap(bitmap, 640, 480, true);  
			        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
			  
			        // setting CompressFormat to JPG  
			  
			        bmpCompressed.compress(CompressFormat.JPEG, 100, bos);  
			        byte[] data = bos.toByteArray();  
			  
			        // adding image to entity
			  
			        entity.addPart("myImage", new ByteArrayBody(data, "temp.jpg"));  
			  
			        //sending image to server
			        httpPost.setEntity(entity);  
			        HttpResponse response = httpClient.execute(httpPost, localContext);  	                
 
					return "success";
					
			    } catch (Exception e) {  
			    	if (dialog.isShowing())
					{
			    		dialog.dismiss();
			    		}
			    	Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
			    	
			    	Log.e(e.getClass().getName(), e.getMessage(), e);
			        Log.e("mImageUploadStuff", Log.getStackTraceString(e));  
			        return "fail";
			  
			    }  
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		/**
		 * handles the action on execution of POST
		 * notifies the user of successful image upload
		 */
		@Override
		protected void onPostExecute(String sResponse) {
		try {
				if (dialog.isShowing())
					dialog.dismiss();
				
				Context context = getApplicationContext();
		        CharSequence text = "Photo uploaded successfully!";
		        int duration = Toast.LENGTH_SHORT;

		        Toast toast = Toast.makeText(context, text, duration);
		        toast.show();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
	}
	}

	/**
	 * gets the path of selected image file
	 * @param uri
	 * @return
	 */
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF OI FILE MANAGER IS USED  FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	/**
	 * decodes the file and sets the imageview
	 * @param filePath
	 */
	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		imgView.setImageBitmap(bitmap);

	}
}