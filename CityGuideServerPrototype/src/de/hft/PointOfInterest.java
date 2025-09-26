package de.hft;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

public class PointOfInterest {

	private int id;
	private String name;
	private String description;
	private double longitude;
	private double latitude;
	private int rating;
	private String[] pictureFiles;
	private String audioFile;
	private String videoFile;
	private String postalAddress;
	
	public PointOfInterest() {
	}
	
	public PointOfInterest(int id, String name, String description, int longitude, int latitude) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public static PointOfInterest loadPointOfInterestById(String id) {
		ResultSet resultSet;
		PointOfInterest pointOfInterest = null;
		try {
			int aId = 0;
			String aName = null;
			String aDescription = null;
			double aLongitude = 0.0;
			double aLatitude = 0.0;
			
			resultSet = Database.executeQuery("SELECT * FROM PointsOfInterest WHERE id = " + id);
			while (resultSet.next()) {
				aId = Integer.parseInt(resultSet.getString("id"));
				aName = resultSet.getString("name").trim();
				aDescription = resultSet.getString("description").trim();
				aLongitude = Double.parseDouble(resultSet.getString("longitude"));
				aLatitude = Double.parseDouble(resultSet.getString("latitude"));
			}
			
			pointOfInterest = new PointOfInterest();
			pointOfInterest.setId(aId);
			pointOfInterest.setName(aName);
			pointOfInterest.setDescription(aDescription);
			pointOfInterest.setLongitude(aLongitude);
			pointOfInterest.setLatitude(aLatitude);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			Database.closeConnection();
		}
		
		return pointOfInterest;
	}
	
}
