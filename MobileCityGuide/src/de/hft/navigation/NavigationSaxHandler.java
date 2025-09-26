package de.hft.navigation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



/**
 * This Class is used to parse the kml File 
 * @author Markus
 *
 */
public class NavigationSaxHandler extends DefaultHandler{ 

/**
 * see if parser is in kml tag
 */
 private boolean in_kmltag = false; 
 /**
  * see if parser is in placemark tag
  */
 private boolean in_placemarktag = false; 
 /**
  * see if parser is in name tag
  */
 private boolean in_nametag = false;
 /**
  * see if parser is in description tag
  */
 private boolean in_descriptiontag = false;
 /**
  * see if parser is in geometrycollection tag
  */
 private boolean in_geometrycollectiontag = false;
/**
 * see if parser is in linestring tag
 */
 private boolean in_linestringtag = false;
/**
 * see if parser is in point tag
 */
 private boolean in_pointtag = false;
 /**
  * see if parser is in coordinate tag
  */
 private boolean in_coordinatestag = false;

 /**
  * buffer for parsing kml file
  */
 private StringBuffer buffer;
/**
 * the set which will be returned
 */
 private NavigationDataSet navigationDataSet = new NavigationDataSet(); 


/**
 * get the Parsed Data from the kml File
 * @return {@link NavigationDataSet}
 */
 public NavigationDataSet getParsedData() {
      navigationDataSet.getCurrentPlacemark().setCoordinates(buffer.toString().trim());
      return this.navigationDataSet; 
 } 

 /**
  * initializes the NavigationDataset
  */
 @Override 
 public void startDocument() throws SAXException { 
      this.navigationDataSet = new NavigationDataSet(); 
 } 

 /**
  * does nothing but has to be implemented because of inheritance
  */
 @Override 
 public void endDocument() throws SAXException { 
      // Nothing to do
 } 

 /** Gets be called on opening tags like: 
  * <tag> 
  * Can provide attribute(s), when xml was like: 
  * <tag attribute="attributeValue">*/ 
 @Override 
 public void startElement(String namespaceURI, String localName, 
           String qName, Attributes atts) throws SAXException { 
      if (localName.equals("kml")) { 
           this.in_kmltag = true;
      } else if (localName.equals("Placemark")) { 
           this.in_placemarktag = true; 
           navigationDataSet.setCurrentPlacemark(new Placemark());
      } else if (localName.equals("name")) { 
           this.in_nametag = true;
      } else if (localName.equals("description")) { 
          this.in_descriptiontag = true;
      } else if (localName.equals("GeometryCollection")) { 
          this.in_geometrycollectiontag = true;
      } else if (localName.equals("LineString")) { 
          this.in_linestringtag = true;              
      } else if (localName.equals("point")) { 
          this.in_pointtag = true;          
      } else if (localName.equals("coordinates")) {
          buffer = new StringBuffer();
          this.in_coordinatestag = true;                        
      }
 } 

 /** Gets be called on closing tags like: 
  * </tag> */ 
 @Override 
 public void endElement(String namespaceURI, String localName, String qName) 
           throws SAXException { 
       if (localName.equals("kml")) {
           this.in_kmltag = false; 
       } else if (localName.equals("Placemark")) { 
           this.in_placemarktag = false;

       if ("Route".equals(navigationDataSet.getCurrentPlacemark().getTitle())) 
               navigationDataSet.setRoutePlacemark(navigationDataSet.getCurrentPlacemark());
        else navigationDataSet.addCurrentPlacemark();

       } else if (localName.equals("name")) { 
           this.in_nametag = false;           
       } else if (localName.equals("description")) { 
           this.in_descriptiontag = false;
       } else if (localName.equals("GeometryCollection")) { 
           this.in_geometrycollectiontag = false;
       } else if (localName.equals("LineString")) { 
           this.in_linestringtag = false;              
       } else if (localName.equals("point")) { 
           this.in_pointtag = false;          
       } else if (localName.equals("coordinates")) { 
           this.in_coordinatestag = false;
       }
 } 

 /** Gets be called on the following structure: 
  * <tag>characters</tag> */ 
 @Override 
public void characters(char ch[], int start, int length) { 
    if(this.in_nametag){ 
        if (navigationDataSet.getCurrentPlacemark()==null) navigationDataSet.setCurrentPlacemark(new Placemark());
        navigationDataSet.getCurrentPlacemark().setTitle(new String(ch, start, length));            
    } else 
    if(this.in_descriptiontag){ 
        if (navigationDataSet.getCurrentPlacemark()==null) navigationDataSet.setCurrentPlacemark(new Placemark());
        navigationDataSet.getCurrentPlacemark().setDescription(new String(ch, start, length));          
    } else
    if(this.in_coordinatestag){        
        if (navigationDataSet.getCurrentPlacemark()==null) navigationDataSet.setCurrentPlacemark(new Placemark());
        //navigationDataSet.getCurrentPlacemark().setCoordinates(new String(ch, start, length));
        buffer.append(ch, start, length);
    }
} 
}