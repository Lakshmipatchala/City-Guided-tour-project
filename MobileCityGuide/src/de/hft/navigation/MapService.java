package de.hft.navigation;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;
/**
 * This class is used to get the Route between two Points of Interest via the Google maps API
 * @author Markus
 *
 */
public class MapService {

/** calculate route somehow  */
public static final int MODE_ANY = 0;
/** calculate Route for vehicles */
public static final int MODE_CAR = 1;
/** calculate Route for pedestrian */
public static final int MODE_WALKING = 2;


/**
 * Used to get a kml File from the Google Maps API, which contains a route between two Points
 * @param startLat Latitude of the Start Point
 * @param startLng Longitude of the Start Point
 * @param targetLat Latitude of the End Point
 * @param targetLng Longitude of the End Point
 * @param mode how should the route be calculated {@link #MODE_ANY} {@link #MODE_CAR} {@link #MODE_WALKING}
 * @return a {@link NavigationDataSet} which can be drawn
 */
public static NavigationDataSet calculateRoute(Double startLat, Double startLng, Double targetLat, Double targetLng, int mode) {
    return calculateRoute(startLat + "," + startLng, targetLat + "," + targetLng, mode);
}


/**
 * Converts given Coordinates to an URL String to get the get a kml File from the Google Maps API, which contains a route between two Points
 * @param startCoords Latitude and Longitude of the Start Point
 * @param targetCoords Latitude and Longitude of the End Point
 * @param mode how should the route be calculated {@link #MODE_ANY} {@link #MODE_CAR} {@link #MODE_WALKING}
 * @return a {@link NavigationDataSet} which can be drawn
 */
private static NavigationDataSet calculateRoute(String startCoords, String targetCoords, int mode) {
    String urlPedestrianMode = "http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
            + targetCoords + "&sll=" + startCoords + "&dirflg=w&hl=en&ie=UTF8&z=14&output=kml";

    Log.d("Stuff", "urlPedestrianMode: "+urlPedestrianMode);

    String urlCarMode = "http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
            + targetCoords + "&sll=" + startCoords + "&hl=en&ie=UTF8&z=14&output=kml";

    Log.d("Stuff", "urlCarMode: "+urlCarMode);

    NavigationDataSet navSet = null;
    // for mode_any: try pedestrian route calculation first, if it fails, fall back to car route
    if (mode==MODE_ANY||mode==MODE_WALKING) navSet = MapService.getNavigationDataSet(urlPedestrianMode);
    if (mode==MODE_ANY&&navSet==null||mode==MODE_CAR) navSet = MapService.getNavigationDataSet(urlCarMode);
    return navSet;
}

/**
 * Retrieve navigation data set from either remote URL or String
 * @param url the specific url for the Points of Interest
 * @return {@link NavigationDataSet}
 */
private static NavigationDataSet getNavigationDataSet(String url) {

    // urlString = "http://192.168.1.100:80/test.kml";
    Log.d("Stuff","urlString -->> " + url);
    NavigationDataSet navigationDataSet = null;
    try
        {           
        final URL aUrl = new URL(url);
        final URLConnection conn = aUrl.openConnection();
        conn.setReadTimeout(15 * 1000);  // timeout for reading the google maps data: 15 secs
        conn.connect();

        /* Get a SAXParser from the SAXPArserFactory. */
        SAXParserFactory spf = SAXParserFactory.newInstance(); 
        SAXParser sp = spf.newSAXParser(); 

        /* Get the XMLReader of the SAXParser we created. */
        XMLReader xr = sp.getXMLReader();

        /* Create a new ContentHandler and apply it to the XML-Reader*/ 
        NavigationSaxHandler navSax2Handler = new NavigationSaxHandler(); 
        xr.setContentHandler(navSax2Handler); 

        /* Parse the xml-data from our URL. */ 
        xr.parse(new InputSource(aUrl.openStream()));

        /* Our NavigationSaxHandler now provides the parsed data to us. */ 
        navigationDataSet = navSax2Handler.getParsedData(); 

        /* Set the result to be displayed in our GUI. */ 
        Log.d("Stuff","navigationDataSet: "+navigationDataSet.toString());

    } catch (Exception e) {
         Log.e("Stuff", "error with kml xml", e);
        navigationDataSet = null;
    }   

    return navigationDataSet;
}

}