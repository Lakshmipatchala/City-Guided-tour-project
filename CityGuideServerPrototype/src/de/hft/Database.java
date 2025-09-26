package de.hft;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	
	private static Connection connection;

	private static void openConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbc.JDBCDriver" );
		connection = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
	}
	
	public static void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.createStatement().execute("SHUTDOWN");
				connection.close();			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void executeUpdate(String sqlStatement) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement statement = connection.createStatement();
		statement.executeUpdate(sqlStatement);
		closeConnection();
	}
	
	public static ResultSet executeQuery(String sqlStatement) throws ClassNotFoundException, SQLException {
		Statement statement;
		ResultSet resultSet = null;
		openConnection();
		statement = connection.createStatement();
		resultSet = statement.executeQuery(sqlStatement);
		closeConnection();
		
		return resultSet;
	}
	
}
