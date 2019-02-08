package com.huskehhh.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 * 
 * @author -_Husky_-
 * @author tips48
 */
public abstract class Database {

	protected Connection connection;

	/**
	 * Creates a new Database
	 *
	 */
	protected Database() {
		connection = null;
	}

	/**
	 * Opens a connection with the database
	 * 
	 * @return Opened connection
	 * @throws SQLException 
	 * @throws ClassNotFoundException
	 *             if the driver cannot be found
	 */
	public abstract Connection openConnection() throws SQLException;

	/**
	 * Checks if a connection is open with the database
	 * 
	 * @return true if the connection is open
	 */
	public boolean checkConnection() {
		try {
			return connection != null && !connection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Gets the connection with the database
	 * 
	 * @return Connection with the database, null if none
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Closes the connection with the database
	 * 
	 * @return true if successful
	 */
	public boolean closeConnection() {
		if (connection == null) {
			return false;
		}

		try {
			connection.close();
		} catch (SQLException e) {
		}

		return true;
	}

	/**
	 * Executes a SQL Query<br>
	 * 
	 * If the connection is closed, it will be opened
	 * 
	 * @param query
	 *            Query to be run
	 * @return the results of the query
	 * @throws SQLException 
	 */
	public ResultSet querySQL(String query) throws SQLException {
		ResultSet result = null;

		if (!checkConnection())
			openConnection();

		try {
			result = connection.createStatement().executeQuery(query);
		} catch (SQLException e) {
			openConnection();
			result = connection.createStatement().executeQuery(query);
		}

		return result;
	}

	/**
	 * Executes an Update SQL Query<br>
	 * See {@link java.sql.Statement#executeUpdate(String)}<br>
	 * If the connection is closed, it will be opened
	 * 
	 * @param query
	 *            Query to be run
	 * @return Result Code, see {@link java.sql.Statement#executeUpdate(String)}
	 * @throws SQLException 
	 */
	public int updateSQL(String query) throws SQLException {
		int result = 0;
		if (!checkConnection())
			openConnection();

		try {
			result = connection.createStatement().executeUpdate(query);
		} catch (SQLException e) {
			openConnection();
			result = connection.createStatement().executeUpdate(query);
		}

		return result;
	}
}