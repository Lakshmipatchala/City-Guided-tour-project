package de.hft.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/**
 * Class to get JSON String form Server via URL
 * @author Markus
 *
 */

public class JsonConnection {
	
	
	/**
	 * get JSON String from Server
	 * @param url of the Servlet
	 * @return a String representation of a {@link JSONObject} or {@link JSONArray}
	 * <p>
	 * <b>Sample code for usage:</b> <br>
	 * <code>
	 * String object = JsonConnection.getStringRepresentation(url); <br>
	 * JSONObject json = new JSONObject(object); <br>
	 * String description = json.getString("description"); <br>
	 * </code>
	 * 
	 
	 */
	public static String getStringRepresentation (String url){
		InputStream in = callService(url);
		return convertStreamToString(in);
	}
	
	/**
	 * opens a connection to the Server and opens an inputStream
	 * @param servletUrl the Url of the Servlet
	 * @return the response from the Servlet as a {@link InputStream} 
	 */
	private static InputStream callService(String servletUrl) {
		InputStream in = null;

		try {
			URL url = new URL(servletUrl);
			URLConnection conn = url.openConnection();

			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.connect();
			
			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			Log.e("MyStuff",Log.getStackTraceString(ex));
		}
		return in;
	}
	
	/**
	 * !!! experimental method for transmitting photos to the server !!!
	 * @param servletUrl
	 * @param fileInputStream from the File which will be send
	 * @param filename the filename of the File
	 * @return nothing usable till now
	 */
	public static boolean sendPictureToServer(String servletUrl, InputStream fileInputStream, String filename){
		boolean result = false;
		try {
			
//			FileInputStream fstream2 = new FileInputStream(file.getAbsolutePath());
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(servletUrl);
			MultipartEntity entity = new MultipartEntity();
			entity.addPart(" ", new InputStreamBody(fileInputStream, "image/jpeg", filename));
			httpPost.setEntity(entity);
			HttpResponse servletResponse = httpClient.execute(httpPost);
 
			
			
			/**HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("encType", "multipart/form-data");
			httpConn.setDoOutput(true);
			httpConn.connect();
			DataOutputStream dataStream = new DataOutputStream(conn.getOutputStream());
			dataStream.writeBytes(text);
			dataStream.flush();
			dataStream.close();
			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				result=true;
			}*/
		} catch (Exception ex) {
			Log.e("MyStuff",Log.getStackTraceString(ex));
		}
		return result;
	}
	
	
	/**
	 * send String to Server
	 * @param servletUrl url of the Servlet
	 * @return true if transmit was successfull, if not it will return false
	 * <p>
	 * <b>Sample code for usage:</b> <br>
	 * <code>
	 * String url = "http://hft.dyndns.biz:8080/MobileCityGuideServer/UpdateRatingOfPointOfInterest?id=3&rating=3";<br>
	 * boolen result = JsonConnection.getStringRepresentation(url) <br>
	 * </code>
	 */
	public static boolean sendDataToServer(String servletUrl){
		boolean result = false;
		try {
			URL url = new URL(servletUrl);
			URLConnection conn = url.openConnection();

			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.connect();
			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				result=true;
			}
		} catch (Exception ex) {
			Log.e("MyStuff",Log.getStackTraceString(ex));
		}
		return result;
	}
	
	
//	 private String sendToServer( String request, String url ) throws IOException {
//	        String result = null;
//	        maybeCreateHttpClient();
//	        HttpPost post = new HttpPost( url );
//	        post.addHeader( "Content-Type", "text/vnd.aexp.json.req" );
//	        post.setEntity( new StringEntity( request ) );    
//	        HttpResponse resp = httpClient.execute( post );
//	// Execute the POST transaction and read the results
//	        int status = resp.getStatusLine().getStatusCode(); 
//	        if( status != HttpStatus.SC_OK )
//	                throw new IOException( "HTTP status: "+Integer.toString( status ) );
//	        DataInputStream is = new DataInputStream( resp.getEntity().getContent() );
//	        result = is.readLine();
//	        return result;
//	    }
//	
//	
//	    private void maybeCreateHttpClient() {
//	        if ( httpClient == null) {
//	            httpClient = new DefaultHttpClient();
//	            HttpParams params = httpClient.getParams();
//	            HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
//	            HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
//	            ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
//	        }
//	    }

	/**
	 * used to convert an input stream from Server to a String
	 * @param is the input stream 
	 * @return the string representation of the stream
	 */
	private static String convertStreamToString(InputStream is) {
		StringBuilder sb = new StringBuilder();
		if(is != null){
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e("MyStuff",Log.getStackTraceString(e));
			}
		}
		}
		return sb.toString();
	}

}
