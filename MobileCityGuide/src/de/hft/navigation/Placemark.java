package de.hft.navigation;


/**
 * This class is a Object representation of the Data from the kml File
 * @author Markus
 *
 */
public class Placemark {
/**
 * title of the Placemark
 */
String title;
/**
 * description of the Placemark
 */
String description;
/**
 * coordinates of the Placemark
 */
String coordinates;
/**
 * address of the Placemark
 */
String address;

/**
 * get The Title of the Placemark
 * @return title
 */
public String getTitle() {
    return title;
}
public void setTitle(String title) {
    this.title = title;
}
/**
 * get the Description of the Placemark
 * @return description
 */
public String getDescription() {
    return description;
}
/**
 * set the Description of the Placemark
 * @param description
 */
public void setDescription(String description) {
    this.description = description;
}
/**
 * get The Coordinates of the Placemark
 * @return coordinates
 */
public String getCoordinates() {
    return coordinates;
}
/**
 * set the Coordinates of the Placemark
 * @param coordinates
 */
public void setCoordinates(String coordinates) {
    this.coordinates = coordinates;
}

/**
 * get the Address of the Placemark
 * @return address
 */
public String getAddress() {
    return address;
}

/**
 * set the Address of the Placemark
 * @param address
 */
public void setAddress(String address) {
    this.address = address;
}

}