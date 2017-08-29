package com.prasad.examples.jersey;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class DBConnection 
{

	public Connection getConnection() throws Exception
	{
		try
		{
			String connectionURL = "jdbc:mysql://localhost:3306/test";
			Connection connection = null;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "admin");
			return connection;
		}
		catch (SQLException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw e;
		}
	}
 
}