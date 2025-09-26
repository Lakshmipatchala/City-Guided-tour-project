package de.hft.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
/**
 * This class is used to display a line between two GeoPoints. This is a subclass which draws lines to display a route between Points of Interest 
 * @author Markus
 *
 */
public class RouteOverlay extends Overlay { 

	/**
	 * start point for line
	 */
	private GeoPoint gp1;
	/**
	 * end Point for line
	 */
	private GeoPoint gp2;
	/**
	 * radius for endPoint or startPoint
	 */
	private int mRadius = 6;

/**
 * in which mode is the line. Used to distinguish what part of a route is drawn <br>
 * 1 is start of the route <br>
 * 2 is a line on the route <br>
 * 3 is end of the route <br> 
 */
private int mode=0;
/**
 * color for line
 */
private int defaultColor;
/**
 * text for the line
 */
private String text="";
/**
 * image for the line
 */
private Bitmap img = null;

/**
 * Constructor to draw a line
 * @param gp1 start point
 * @param gp2 end point
 * @param mode in which mode is the line. Used to distinguish what part of a route is drawn <br>
 * 1 is start of the route <br>
 * 2 is path of the route <br>
 * 3 is end of the route <br> 
 */
public RouteOverlay(GeoPoint gp1,GeoPoint gp2,int mode) { // GeoPoint is a int. (6E)
    this.gp1 = gp1;
    this.gp2 = gp2;
    this.mode = mode;
    defaultColor = 999; // no defaultColor
}
/**
 * Constructor to draw a line
 * @param gp1 start point
 * @param gp2 end point
 * @param mode in which mode is the line. Used to distinguish what part of a route is drawn <br>
 * 1 is start of the route <br>
 * 2 is path of the route <br>
 * 3 is end of the route <br> 
 * @param defaultColor gives the route a specific color
 */
public  RouteOverlay(GeoPoint gp1,GeoPoint gp2,int mode, int defaultColor) {
    this.gp1 = gp1;
    this.gp2 = gp2;
    this.mode = mode;
    this.defaultColor = defaultColor;
}

/**
 * set the text of the line
 * @param t the text for the line
 */
public synchronized  void setText(String t) {
    this.text = t;
}

/**
 * set a Picture for the line
 * @param bitmap for the line
 */
public synchronized  void setBitmap(Bitmap bitmap) { 
    this.img = bitmap;
}

/**
 * get the Mode of the actual line
 * @return mode of the line
 */
public synchronized  int getMode() {
    return mode;
}

/**
 * draws a line for the route in the specified mode
 */
@Override
public synchronized  boolean draw (Canvas canvas, MapView mapView, boolean shadow, long when) {
    Projection projection = mapView.getProjection();
    if (shadow == false) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Point point = new Point();
        projection.toPixels(gp1, point);
        // mode=1&#65306;start
        if(mode==1) {
            if(defaultColor==999)
            paint.setColor(Color.BLACK); // Color.BLUE
            else
            paint.setColor(defaultColor);
            RectF oval=new RectF(point.x - mRadius, point.y - mRadius,
            point.x + mRadius, point.y + mRadius);
            // start point
            canvas.drawOval(oval, paint);
        }
        // mode=2&#65306;path
        else if(mode==2) {
            if(defaultColor==999)
            paint.setColor(Color.RED);
            else
            paint.setColor(defaultColor);
            Point point2 = new Point();
            projection.toPixels(gp2, point2);
            paint.setStrokeWidth(5);
            paint.setAlpha(defaultColor==Color.parseColor("#6C8715")?220:120);
            canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
        }
        /* mode=3&#65306;end */
        else if(mode==3) {
            /* the last path */

            if(defaultColor==999)
                paint.setColor(Color.BLACK);  // Color.GREEN
            else
                paint.setColor(defaultColor);

            Point point2 = new Point();
            projection.toPixels(gp2, point2);
            paint.setStrokeWidth(5);
            paint.setAlpha(defaultColor==Color.parseColor("#6C8715")?220:120);
            canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
            RectF oval=new RectF(point2.x - mRadius,point2.y - mRadius,
            point2.x + mRadius,point2.y + mRadius);
            /* end point */
            paint.setAlpha(255);
            canvas.drawOval(oval, paint);
        }
    }
    return super.draw(canvas, mapView, shadow, when);
}

}