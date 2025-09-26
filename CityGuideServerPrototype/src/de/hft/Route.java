package de.hft;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Route {

	private int id;
	private String name;
	private String description;
	private ArrayList<PointOfInterest> pointOfInterestList;
	
	public Route() {
	}
	
	public Route(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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
	
	public void addPointOfInterest(PointOfInterest pointOfInterest) {
		pointOfInterestList.add(pointOfInterest);
	}
	
	public List<PointOfInterest> getPointsOfInterest() {
		return pointOfInterestList;
	}

	public static JSONArray loadRouteIndex() {
		ResultSet resultSet;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject;
		try {
			resultSet = Database.executeQuery("SELECT * FROM Routes");
			while (resultSet.next()) {
				jsonObject = new JSONObject();
				String id = resultSet.getString("id").trim();
				jsonObject.put("id", id);
				jsonObject.put("name", resultSet.getString("name").trim());
				jsonArray.put(jsonObject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			Database.closeConnection();
		}
		
		return jsonArray;
	}

	public static JSONArray loadAllRoutes() {
		ResultSet resultSet;
		ResultSet innerResultSet;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject;
		try {
			resultSet = Database.executeQuery("SELECT * FROM Routes");
			while (resultSet.next()) {
				jsonObject = new JSONObject();
				String id = resultSet.getString("id").trim();
				jsonObject.put("id", id);
				jsonObject.put("name", resultSet.getString("name").trim());
				jsonObject.put("description", resultSet.getString("description").trim());
				
				innerResultSet = Database.executeQuery("SELECT * FROM Mapping WHERE routeId = " + id);
				String pointsOfInterest = "";
				while (innerResultSet.next()) {
					if (pointsOfInterest.isEmpty()) {
						pointsOfInterest = innerResultSet.getString("poiId").trim();					
					} else {
						pointsOfInterest += "," + innerResultSet.getString("poiId").trim();
					}
				}
				jsonObject.put("poiId", pointsOfInterest);
				
				jsonArray.put(jsonObject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			Database.closeConnection();
		}
		
		return jsonArray;
	}
	
	public static JSONObject loadRouteById(String id) {
		ResultSet resultSet;
		JSONObject jsonObject = new JSONObject();
		try {
			resultSet = Database.executeQuery("SELECT * FROM Routes WHERE id = " + id);
			while (resultSet.next()) {
				jsonObject.put("id", resultSet.getString("id").trim());
				jsonObject.put("name", resultSet.getString("name").trim());
				jsonObject.put("description", resultSet.getString("description").trim());
			}
			
			resultSet = Database.executeQuery("SELECT * FROM Mapping WHERE routeId = " + id);
			String pointsOfInterest = "";
			while (resultSet.next()) {
				if (pointsOfInterest.isEmpty()) {
					pointsOfInterest = resultSet.getString("poiId").trim();					
				} else {
					pointsOfInterest += "," + resultSet.getString("poiId").trim();
				}
			}
			jsonObject.put("poiId", pointsOfInterest);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			Database.closeConnection();
		}
		
		return jsonObject;
	}
	
}
