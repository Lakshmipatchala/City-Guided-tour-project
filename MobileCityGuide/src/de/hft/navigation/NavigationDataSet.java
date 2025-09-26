package de.hft.navigation;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * This class is used to Store the Different Placemarks from the kml File
 * @author Markus
 *
 */
public class NavigationDataSet {

	private ArrayList<Placemark> placemarks = new ArrayList<Placemark>();
	private Placemark currentPlacemark;
	private Placemark routePlacemark;

	/**
	 * Displays all information from the kml File
	 */
	public String toString() {
		String s = "";
		for (Iterator<Placemark> iter = placemarks.iterator(); iter.hasNext();) {
			Placemark p = (Placemark) iter.next();
			s += p.getTitle() + "\n" + p.getDescription() + "\n\n";
		}
		return s;
	}

	/**
	 * add a Placemark to the list
	 */
	public void addCurrentPlacemark() {
		placemarks.add(currentPlacemark);
	}

	/**
	 * get all Placemarks who are in the list
	 * @return placemarks
	 */
	public ArrayList<Placemark> getPlacemarks() {
		return placemarks;
	}

	/**
	 * add a list of  Placemark
	 * @param placemarks
	 */
	public void setPlacemarks(ArrayList<Placemark> placemarks) {
		this.placemarks = placemarks;
	}

	/**
	 * get the current Placemark
	 * @return currentPlacemark
	 */
	public Placemark getCurrentPlacemark() {
		return currentPlacemark;
	}

	/**
	 * set the current Placemark
	 * @param currentPlacemark
	 */
	public void setCurrentPlacemark(Placemark currentPlacemark) {
		this.currentPlacemark = currentPlacemark;
	}

	/**
	 * get the Placemark for the route
	 * @return
	 */
	public Placemark getRoutePlacemark() {
		return routePlacemark;
	}

	/**
	 * set the Placemark for the route
	 * @param routePlacemark
	 */
	public void setRoutePlacemark(Placemark routePlacemark) {
		this.routePlacemark = routePlacemark;
	}

}